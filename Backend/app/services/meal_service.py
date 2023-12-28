from datetime import date, datetime
from typing import List

from bson import ObjectId
from motor.motor_asyncio import AsyncIOMotorDatabase
from pymongo import UpdateOne

from app.crud.crud_foods import get_food_by_id
from app.crud.crud_meals import (
    add_new_food_item,
    save_meal,
    perform_bulk_meal_updates,
)
from app.models.domain import Meal, User, Food, FoodItem, FoodWeight
from app.models.schemas.meal_schema import MealOrder


async def create_default_meals(db: AsyncIOMotorDatabase, user: User, target_date: date):
    created_meals = []
    for index, meal_name in enumerate(user.order_meal):
        meal: Meal = Meal(
            user_id=user.id,
            date=datetime.combine(target_date, datetime.min.time()),
            name=meal_name,
            index=index,
        )
        meal_id = await save_meal(db, meal.model_dump(exclude="id"))
        meal.id = meal_id
        created_meals.append(meal)
    return created_meals


async def set_new_indexes(db: AsyncIOMotorDatabase, meals: List[MealOrder]):
    bulk_updates = [
        UpdateOne({"_id": ObjectId(meal.id)}, {"$set": {"index": meal.index}})
        for meal in meals
    ]
    await perform_bulk_meal_updates(db, bulk_updates)


async def add_new_food(db: AsyncIOMotorDatabase, meal_id: str, food_weight: FoodWeight):
    food_dict = food_weight.model_dump()
    await add_new_food_item(db, meal_id, {'id': ObjectId(food_dict['id']), 'weight': food_dict['weight']})
    food: Food = await get_food_by_id(db, food_weight.id)
    return FoodItem(**{**food.model_dump(), **{k: v for k, v in food_weight.model_dump().items() if k != 'id'}})