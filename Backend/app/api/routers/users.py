from fastapi import APIRouter, Body, Depends, Query, status
from motor.motor_asyncio import AsyncIOMotorDatabase

from app.api.dependencies import get_current_user, get_db
from app.crud.crud_user import (get_user_by_email, get_user_by_username,
                                update_order_meal, update_user_by_id)
from app.models.domain import User
from app.models.schemas.user_schema import UserOut, UserUpdateRequest
from app.services.user_service import set_new_password

router: APIRouter = APIRouter(prefix='/users')


router.get('/check-email')


async def check_user_by_email(
    email: str = Query(...),
    db: AsyncIOMotorDatabase = Depends(get_db)
):
    user: User = get_user_by_email(email=email, db=db)
    return {
        'message': 'The email is already used'if user else 'Email is not used',
        'status': '0' if user else '1',
        'email': email
    }


router.get('/check-username')


async def check_user_by_username(
    username: str = Query(...),
    db: AsyncIOMotorDatabase = Depends(get_db)
):
    user: User = get_user_by_username(username=username, db=db)
    return {
        'message': 'The username is already used'if user else 'Email is not used',
        'status': '0' if user else '1',
        'username': username
    }


@router.get('/me', response_model=UserOut, status_code=status.HTTP_200_OK)
async def get_user_data(user: User = Depends(get_current_user)) -> UserOut:
    return UserOut(**user.model_dump(exclude='hashed_password'))


@router.delete('/me/{meal_name}', status_code=status.HTTP_204_NO_CONTENT)
async def remove_meal_in_template(
    meal_name: str,
    user: User = Depends(get_current_user),
    db: AsyncIOMotorDatabase = Depends(get_db)
):
    if meal_name not in user.order_meal:
        return
    new_order_meal = [meal for meal in user.order_meal if meal != meal_name]
    await update_order_meal(
        db=db,
        user_id=user.id,
        new_order_meal=new_order_meal
    )


@router.patch('/me/update', status_code=status.HTTP_200_OK)
async def update_user(
    update_fields: UserUpdateRequest = Body(...),
    user: User = Depends(get_current_user),
    db: AsyncIOMotorDatabase = Depends(get_db)
) -> dict:
    if update_fields.email is not None:
        user.email = update_fields.email
    if update_fields.gender is not None:
        user.profile['gender'] = update_fields.gender
    if update_fields.height is not None:
        user.profile['height'] = update_fields.height
    if update_fields.weight is not None:
        user.profile['weight'] = update_fields.weight
    if update_fields.age is not None:
        user.profile['age'] = update_fields.age
    if update_fields.physical_activity_level is not None:
        user.profile['physical_activity_level'] = update_fields.physical_activity_level
    user: User = await update_user_by_id(db=db, user_data=user.model_dump())
    return {
        'message': 'User updated successfully',
        'user': UserOut(**user.model_dump(exclude='password'))
    }


@router.patch('/me/update_password', status_code=status.HTTP_200_OK)
async def update_password(
    new_password: str = Body(...),
    user: User = Depends(get_current_user),
    db: AsyncIOMotorDatabase = Depends(get_db)
) -> dict:
    message: str = 'Se ha actualizado correctamente la contraseña' if await set_new_password(
        db=db,
        user_id=user.id,
        new_password=new_password
    ) == 1 else 'No se ha actualizado correctamente la contraseña'
    return {'message': message}
