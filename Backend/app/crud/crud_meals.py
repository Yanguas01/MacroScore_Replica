import logging
from typing import Sequence
from bson import ObjectId

from pymongo import InsertOne, DeleteOne, DeleteMany, ReplaceOne, UpdateOne, UpdateMany
from motor.motor_asyncio import AsyncIOMotorDatabase

from app.utils.helpers import convert_date
from app.utils.mappers import meals_mongo_to_domain


async def save_meal(db: AsyncIOMotorDatabase, meal_data: dict):
    result = await db.get_collection("meals").insert_one(meal_data)
    return str(result.inserted_id)


"""async def get_meals_by_date(
    db: AsyncIOMotorDatabase, user_id: str, target_date: str, limit: int = 100
):
    try:
        meals = (
            await db.get_collection("meals")
            .find({"user_id": user_id, "date": convert_date(target_date)})
            .to_list(limit)
        )
        return meals_mongo_to_domain(meals)
    except ValueError:
        raise ValueError("Invalid date format. Expected format: YYYY-MM-DD")"""


async def get_meals_by_date(
    db: AsyncIOMotorDatabase, user_id: str, target_date: str, limit: int = 100
):
    try:
        pipeline = [
            {
                '$unwind': '$items'
            }, {
                '$lookup': {
                    'from': 'foods',
                    'localField': 'items.id',
                    'foreignField': '_id',
                    'as': 'items.food'
                }
            }, {
                '$unwind': '$items.food'
            }, {
                '$addFields': {
                    'items.food.weight': '$items.weight'
                }
            }, {
                '$group': {
                    '_id': '$_id',
                    'date': {
                        '$first': '$date'
                    },
                    'user_id': {
                        '$first': '$user_id'
                    },
                    'name': {
                        '$first': '$name'
                    },
                    'index': {
                        '$first': '$index'
                    },
                    'items': {
                        '$push': '$items.food'
                    }
                }
            }
        ]
        meals = await db.meals.aggregate(pipeline).to_list(None)
        return meals_mongo_to_domain(meals)
    except ValueError:
        raise ValueError("Invalid date format. Expected format: YYYY-MM-DD")


async def count_meals_by_date(db: AsyncIOMotorDatabase, user_id: str, target_date: str):
    try:
        meals_count = await db.get_collection("meals").count_documents(
            {"user_id": user_id, "date": convert_date(target_date)}
        )
        return meals_count
    except ValueError:
        raise ValueError("Invalid date format. Expected format: YYYY-MM-DD")


async def perform_bulk_meal_updates(
    db: AsyncIOMotorDatabase,
    bulk_updates: Sequence[
        InsertOne | DeleteOne | DeleteMany | ReplaceOne | UpdateOne | UpdateMany
    ],
):
    try:
        result = await db.get_collection("meals").bulk_write(bulk_updates)
        return result
    except Exception as e:
        logging.error(f"An error occurred during the database operation: {e}")
        raise


async def add_new_food_item(db: AsyncIOMotorDatabase, meal_id: str, food_data: dict):
    print(type(food_data))
    await db.get_collection("meals").update_one(
        {"_id": ObjectId(meal_id)}, {"$push": {"items": food_data}}
    )


async def aggregate_pipeline(db: AsyncIOMotorDatabase, pipeline: list):
    print("Pipeline:", pipeline)
    cursor = db.get_collection("meals").aggregate(pipeline)
    print("Cursor:", cursor)

    result = []
    async for document in cursor:
        print("Documento encontrado:", document)
        result.append(document)

    if not result:
        print("No se encontraron documentos.")

    return result
