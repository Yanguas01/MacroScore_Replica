from datetime import date, datetime
from typing import List

from bson import ObjectId
from motor.motor_asyncio import AsyncIOMotorDatabase
from pymongo import UpdateOne

from app.crud.crud_meals import save_meal, update_meals_indexes
from app.models.domain import Meal, User
from app.models.schemas.meal_schema import MealOrder


async def create_default_meals(db: AsyncIOMotorDatabase, user: User, target_date: date):
    created_meals = []
    for index, meal_name in enumerate(user.order_meal):
        meal: Meal = Meal(
            user_id=user.id,
            date=datetime.combine(target_date, datetime.min.time()),
            name=meal_name,
            index=index
        )
        meal_id = await save_meal(db, meal.model_dump(exclude="id"))
        meal.id = meal_id
        created_meals.append(meal)
    return created_meals


async def set_new_indexes(db: AsyncIOMotorDatabase, meals: List[MealOrder]):
    bulk_updates = [ UpdateOne(
            {'_id': ObjectId(meal.id)},
            {'$set': {'index': meal.index}}
        ) for meal in meals
    ]
    print(type(bulk_updates))
    print(type(UpdateOne({},{})))
    await update_meals_indexes(db, bulk_updates)
