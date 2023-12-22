import logging
from datetime import datetime
from typing import Sequence
from bson import ObjectId

from pymongo import InsertOne, DeleteOne, DeleteMany, ReplaceOne, UpdateOne, UpdateMany
from motor.motor_asyncio import AsyncIOMotorDatabase

from app.utils.mappers import meals_mongo_to_domain


async def save_meal(db: AsyncIOMotorDatabase, meal_data: dict):
    result = await db.get_collection("meals").insert_one(meal_data)
    return str(result.inserted_id)


async def get_meals_by_date(db: AsyncIOMotorDatabase, user_id: str, target_date: str, limit: int = 100):
    try:
        meals = await db.get_collection("meals").find({"user_id": user_id, "date": convert_date(target_date)}).to_list(limit)
        return meals_mongo_to_domain(meals)
    except ValueError:
        raise ValueError("Invalid date format. Expected format: YYYY-MM-DD")


async def count_meals_by_date(db: AsyncIOMotorDatabase, user_id: str, target_date: str):
    try:
        meals_count = await db.get_collection("meals").count_documents({"user_id": user_id, "date": convert_date(target_date)})
        return meals_count
    except ValueError:
        raise ValueError("Invalid date format. Expected format: YYYY-MM-DD")


async def perform_bulk_meal_updates(db: AsyncIOMotorDatabase, bulk_updates: Sequence[InsertOne | DeleteOne | DeleteMany | ReplaceOne | UpdateOne | UpdateMany]):
    try:
        result = await db.get_collection("meals").bulk_write(bulk_updates)
        return result
    except Exception as e:
        logging.error(f"An error occurred during the database operation: {e}")
        raise


def convert_date(target_date: str) -> datetime:
    try:
        date = datetime.strptime(target_date, "%Y-%m-%d")
        return date
    except ValueError:
        raise ValueError("Invalid date format. Expected format: YYYY-MM-DD")
