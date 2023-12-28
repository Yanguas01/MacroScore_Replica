from datetime import date
from typing import List

from fastapi import APIRouter, Depends
from motor.motor_asyncio import AsyncIOMotorDatabase

from app.api.dependencies import get_current_user, get_db
from app.crud.crud_meals import count_meals_by_date, get_meals_by_date, save_meal
from app.crud.crud_user import add_meal, update_order_meal
from app.models.domain import Meal, User, FoodWeight
from app.models.schemas.food_schema import FoodIn
from app.models.schemas.meal_schema import MealIn, MealOrder, MealOut
from app.services.meal_service import (
    add_new_food,
    create_default_meals,
    set_new_indexes,
)
from app.utils.helpers import convert_date

router = APIRouter(prefix="/meals")


@router.post("/", response_model=MealOut)
async def create_meal(
    meal_in: MealIn,
    user: User = Depends(get_current_user),
    db: AsyncIOMotorDatabase = Depends(get_db),
) -> MealOut:
    meal_index = await count_meals_by_date(db, user.id, meal_in.datetime)
    meal: Meal = Meal(
        user_id=user.id,
        date=convert_date(meal_in.datetime),
        name=meal_in.name,
        index=meal_index,
    )
    meal_id = await save_meal(db, meal.model_dump(exclude="id"))
    if meal_in.save_meal:
        await add_meal(db, meal.name, user.id)
    return MealOut(id=meal_id, name=meal.name, datetime=meal_in.datetime)


@router.get("/{target_date}", response_model=List[Meal])
async def get_meals(
    target_date: str,
    user: User = Depends(get_current_user),
    db: AsyncIOMotorDatabase = Depends(get_db),
) -> List[Meal]:
    if meals := await get_meals_by_date(db, user.id, target_date):
        print(meals)
        return meals
    """meals = await create_default_meals(db, user, target_date)
    return meals"""
    return []


@router.patch("/reorder")
async def reorder_meals(
    meals: List[MealOrder],
    user: User = Depends(get_current_user),
    db: AsyncIOMotorDatabase = Depends(get_db),
):
    await set_new_indexes(db, meals)
    order_mapping = {meal.name: meal.index for meal in meals}
    default_meals = [meal for meal in user.order_meal if meal in order_mapping]
    default_meals.sort(key=lambda meal: order_mapping[meal])
    if set(default_meals) != set(user.order_meal):
        await update_order_meal(db, user.id, default_meals)


@router.post("/{meal_id}/foods/")
async def add_food(
    meal_id: str,
    food_in: FoodIn,
    user: User = Depends(get_current_user),
    db: AsyncIOMotorDatabase = Depends(get_db),
):
    food_out = await add_new_food(db, meal_id, FoodWeight(**food_in.model_dump()))
    return food_out
