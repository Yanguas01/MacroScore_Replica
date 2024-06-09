from typing import List

from fastapi import APIRouter, Body, Depends, Query, status, HTTPException
from motor.motor_asyncio import AsyncIOMotorDatabase

from app.api.dependencies import get_current_user, get_db
from app.crud.crud_meals import (count_meals_by_date, delete_food_by_id,
                                 delete_meal_by_id, get_meals_by_date,
                                 get_meals_by_week, save_meal,
                                 set_food_weight_by_id)
from app.crud.crud_user import add_meal, update_order_meal
from app.models.domain import FoodItem, FoodWeight, Meal, User
from app.models.schemas.food_schema import FoodIn
from app.models.schemas.meal_schema import MealIn, MealOrder, MealOut
from app.services.meal_service import (add_new_food, create_default_meals,
                                       set_new_indexes, set_new_names)
from app.utils.helpers import convert_date

router = APIRouter(prefix='/meals')


@router.post('', response_model=Meal)
async def create_meal(
    meal_in: MealIn,
    user: User = Depends(get_current_user),
    db: AsyncIOMotorDatabase = Depends(get_db)
) -> Meal:
    meal_datetime = convert_date(meal_in.datetime)
    meal_index = await count_meals_by_date(db, user.id, meal_datetime)
    meal: Meal = Meal(
        user_id=user.id,
        date=meal_datetime,
        name=meal_in.name,
        index=meal_index
    )
    meal.id = await save_meal(db, meal.model_dump(exclude='id'))

    if not meal.id:
        raise HTTPException(status_code=400, detail="Meal already exists")

    if meal_in.save_meal:
        await add_meal(db, meal.name, user.id)

    return meal


@router.patch('/{meal_id}/rename', status_code=status.HTTP_200_OK)
async def rename_meal(
    meal_id: str,
    new_name: str = Body(...),
    user: User = Depends(get_current_user),
    db: AsyncIOMotorDatabase = Depends(get_db)
) -> dict:
    modified_count: int = await set_new_names(
        db=db,
        user=user,
        meal_id=meal_id,
        new_name=new_name
    )
    return {
        'message': f'Se han actualizado {modified_count} nombres',
        'meal_id': meal_id,
        'new_name': new_name
    }


@router.delete('/{meal_id}', status_code=status.HTTP_204_NO_CONTENT)
async def delete_meal(
    meal_id: str,
    user: User = Depends(get_current_user),
    db: AsyncIOMotorDatabase = Depends(get_db)
) -> None:
    await delete_meal_by_id(
        db=db,
        user_id=user.id,
        meal_id=meal_id
    )


@router.get('/week')
async def get_weekly_meals(
    start_week_date: str = Query(None),
    end_week_date: str = Query(None),
    user: User = Depends(get_current_user),
    db: AsyncIOMotorDatabase = Depends(get_db)
) -> dict:
    meals: List[Meal] = await get_meals_by_week(
        db=db,
        user_id=user.id,
        start_week_date=start_week_date,
        end_week_date=end_week_date
    )
    return {
        'meals': meals,
        'user_profile': user.profile
    }


@router.get('', response_model=List[Meal])
async def get_meals(
    target_date: str = Query(None),
    user: User = Depends(get_current_user),
    db: AsyncIOMotorDatabase = Depends(get_db)
) -> List[Meal]:
    meals: list = await get_meals_by_date(db, user.id, target_date)
    if not meals:
        meals: list = await create_default_meals(db, user, target_date)
    return meals


@router.patch('/reorder')
async def reorder_meals(
    meals: List[MealOrder],
    user: User = Depends(get_current_user),
    db: AsyncIOMotorDatabase = Depends(get_db)
) -> dict:
    modified_count: int = await set_new_indexes(db, user.id, meals)
    order_mapping: dict = {meal.name: meal.index for meal in meals}
    default_meals_list: list = sorted(
        {meal for meal in user.order_meal if meal in order_mapping},
        key=lambda meal: order_mapping[meal]
    )
    if default_meals_list != set(user.order_meal):
        await update_order_meal(db, user.id, default_meals_list)
    return {
        'message': f'Se han actualizado {modified_count} índices'
    }


@router.post('/{meal_id}/foods', response_model=FoodItem)
async def add_food(
    meal_id: str,
    food_in: FoodIn,
    user: User = Depends(get_current_user),
    db: AsyncIOMotorDatabase = Depends(get_db)
) -> FoodItem:
    print(food_in)
    food_out: FoodItem = await add_new_food(db, user.id, meal_id, FoodWeight(**food_in.model_dump()))
    return food_out


@router.patch('/{meal_id}/foods/{food_id}/weight')
async def patch_new_weight(
    meal_id: str,
    food_id: str,
    weight: float = Body(...),
    user: User = Depends(get_current_user),
    db: AsyncIOMotorDatabase = Depends(get_db)
) -> dict:
    modified_count: int = await set_food_weight_by_id(db, user.id, meal_id, food_id, weight)
    return {
        'message': f'Peso actualizado en {modified_count} alimentos con éxito',
        'meal_id': meal_id,
        'food_id': food_id,
        'weight': weight
    }


@router.delete('/{meal_id}/foods/{food_id}', status_code=status.HTTP_204_NO_CONTENT)
async def delete_food(
    meal_id: str,
    food_id: str,
    user: User = Depends(get_current_user),
    db: AsyncIOMotorDatabase = Depends(get_db)
) -> None:
    await delete_food_by_id(db, user.id, meal_id, food_id)
