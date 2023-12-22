from datetime import date, datetime
from typing import List

from fastapi import APIRouter, Depends
from motor.motor_asyncio import AsyncIOMotorDatabase

from app.api.dependencies import get_current_user, get_db
from app.crud.crud_meals import count_meals_by_date, get_meals_by_date, save_meal
from app.crud.crud_user import add_meal, update_order_meal
from app.models.domain import FoodItem, Meal, User
from app.models.schemas.food_item_schema import FoodItemIn 
from app.models.schemas.meal_schema import MealIn, MealOrder
from app.services.meal_service import create_default_meals, set_new_indexes

router = APIRouter(prefix="/meals")


@router.post("/")
async def create_meal(meal_in: MealIn, user: User = Depends(get_current_user), db: AsyncIOMotorDatabase = Depends(get_db)):
    meal_index = await count_meals_by_date(db, user.id, meal_in.datetime)
    meal: Meal = Meal(
        user_id=user.id,
        date=datetime.combine(meal_in.datetime, datetime.min.time()),
        name=meal_in.name,
        index=meal_index
    )
    await save_meal(db, meal.model_dump(exclude="id"))
    if meal_in.save_meal:
        await add_meal(db, meal.name, user.id)


@router.get("/{target_date}", response_model=List[Meal])
async def get_meals(target_date: date, user: User = Depends(get_current_user), db: AsyncIOMotorDatabase = Depends(get_db)):
    if meals := await get_meals_by_date(db, user.id, target_date):
        return meals
    meals = await create_default_meals(db, user, target_date)
    return meals


@router.patch("/reorder")
async def reorder_meals(meals: List[MealOrder], user: User = Depends(get_current_user), db: AsyncIOMotorDatabase = Depends(get_db)):
    await set_new_indexes(db, meals)
    order_mapping = {meal.name: meal.index for meal in meals}
    default_meals = [meal for meal in user.order_meal if meal in order_mapping]
    default_meals.sort(key=lambda meal: order_mapping[meal])
    if set(default_meals) != set(user.order_meal):
        await update_order_meal(db, user.id, default_meals)


@router.post("/{meal_id}/foods/")
async def add_food(meal_id: str, food_in: FoodItemIn, user: User = Depends(get_current_user), db: AsyncIOMotorDatabase = Depends(get_db)):
    # await add_food_by_meal_id(db, meal_id, food_in.model_dump())
    return None
