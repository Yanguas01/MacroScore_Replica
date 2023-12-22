from fastapi import HTTPException, status
from motor.motor_asyncio import AsyncIOMotorDatabase
from app.crud.crud_user import get_user_by_email, get_user_by_username, create_user
from app.models.domain import User
from app.models.schemas.user_schema import UserIn
from app.utils.helpers import get_password_hash, verify_password

async def authenticate_user(db: AsyncIOMotorDatabase, username: str, password: str):
    user : User = await get_user_by_username(db, username)
    if not user:
        raise HTTPException(status_code = status.HTTP_400_BAD_REQUEST, detail = "Nombre de usuario incorrecto")
    if not verify_password(password, user.hashed_password):
        raise HTTPException(status_code = status.HTTP_400_BAD_REQUEST, detail = "Contraseña incorrecta")
    return user

async def register_new_user(db: AsyncIOMotorDatabase, user_in: UserIn):
    if await get_user_by_email(db, user_in.email) is not None:
        raise HTTPException(status_code=status.HTTP_400_BAD_REQUEST,
                            detail = "Email asociado a una cuenta")
    if await get_user_by_username(db, user_in.username) is not None:
        raise HTTPException(status_code = status.HTTP_400_BAD_REQUEST,
                            detail = "Nombre de usuario ya registrado")
    user_dict = {"username": user_in.username, "email": user_in.email, "hashed_password": get_password_hash(user_in.password), "order_meal": []}
    new_user_id = await create_user(db, user_dict)
    return new_user_id