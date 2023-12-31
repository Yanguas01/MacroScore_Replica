from datetime import date
from typing import List

from bson import ObjectId
from motor.motor_asyncio import AsyncIOMotorDatabase
from pymongo import UpdateOne

from app.crud.crud_foods import get_food_by_id
from app.crud.crud_meals import (add_new_food_item, get_meal_by_id,
                                 perform_bulk_meal_updates, save_meal,
                                 set_new_name_by_id, set_new_names_by_old_name)
from app.crud.crud_user import update_order_meal
from app.models.domain import Food, FoodItem, FoodWeight, Meal, User
from app.models.schemas.meal_schema import MealOrder
from app.utils.helpers import convert_date


async def create_default_meals(
    db: AsyncIOMotorDatabase,
    user: User,
    target_date: date
) -> List[Meal]:
    created_meals: List[Meal] = []
    for index, meal_name in enumerate(user.order_meal):
        meal: Meal = Meal(
            user_id=user.id,
            date=convert_date(target_date),
            name=meal_name,
            index=index,
        )
        meal_id: str = await save_meal(db, meal.model_dump(exclude='id'))
        meal.id = meal_id
        created_meals.append(meal)
    return created_meals


async def set_new_names(
    db: AsyncIOMotorDatabase,
    user: User,
    meal_id: str,
    new_name: str
) -> int:
    user_id: str = user.id
    meal: dict = await get_meal_by_id(db, user_id, meal_id)
    if meal is None:
        raise ValueError('Meal not found')
    old_name: str = meal['name']
    order_meal_set: set = set(user.order_meal)
    if new_name in order_meal_set:
        raise ValueError('New name already exists in order_meal list')
    if old_name in order_meal_set:
        modified_count: int = await set_new_names_by_old_name(db, user_id, old_name, new_name)
        new_order_meal = list(
            map(lambda ordered_meal: new_name if ordered_meal ==
                old_name else ordered_meal, user.order_meal))
        await update_order_meal(db, user_id, new_order_meal)
        return modified_count
    else:
        return await set_new_name_by_id(db, user_id, meal_id, new_name)


async def set_new_indexes(
    db: AsyncIOMotorDatabase,
    user_id: str,
    meals: List[MealOrder]
) -> int:
    bulk_updates = list(
        map(lambda meal: UpdateOne(
            {'_id': ObjectId(meal.id), 'user_id': user_id},
            {'$set': {'index': meal.index}}
        ), meals))
    return await perform_bulk_meal_updates(db, bulk_updates)


async def add_new_food(
    db: AsyncIOMotorDatabase,
    user_id: str,
    meal_id: str,
    food_weight: FoodWeight
) -> FoodItem:
    food_dict = food_weight.model_dump()
    await add_new_food_item(db, user_id, meal_id, {'id': ObjectId(food_dict['id']), 'weight': food_dict['weight']})
    food: Food = await get_food_by_id(db, food_weight.id)
    return FoodItem(**{**food.model_dump(), **{k: v for k, v in food_weight.model_dump().items() if k != 'id'}})
