import logging
from datetime import datetime
from typing import List, Sequence

from bson import ObjectId
from motor.motor_asyncio import AsyncIOMotorDatabase
from pymongo import (DeleteMany, DeleteOne, InsertOne, ReplaceOne, UpdateMany,
                     UpdateOne)
from pymongo.results import (BulkWriteResult, DeleteResult, InsertOneResult,
                             UpdateResult)

from app.models.domain import Meal
from app.utils.helpers import convert_date
from app.utils.mappers import meal_mongo_to_domain, meals_mongo_to_domain


async def save_meal(
    db: AsyncIOMotorDatabase,
    meal_data: dict
) -> str:
    try:
        if await meal_exists(db, meal_data['user_id'], meal_data['date'], meal_data['name']):
            logging.info(f'Meal already exists: user_id={meal_data["user_id"]}, date={meal_data["date"]}, name={meal_data["name"]}')
            return None

        result: InsertOneResult = await db.get_collection('meals').insert_one(meal_data)
        return str(result.inserted_id)
    except Exception as e:
        logging.error(f'An error occurred during the database operation: {e}')
        raise

async def meal_exists(
    db: AsyncIOMotorDatabase, 
    user_id: str, 
    date: datetime, 
    name: str
) -> bool:
    try:
        count: int = await db.get_collection('meals').count_documents({
            'user_id': user_id,
            'date': date,
            'name': name
        })
        return count > 0
    except Exception as e:
        logging.error(f'An error occurred during the database operation: {e}')
        raise

def generate_pipeline(
    user_id: str,
    start_date: str,
    end_date: str | None = None
) -> list:
    if end_date:
        match_filter = {
            'date': {
                '$gte': convert_date(start_date),
                '$lte': convert_date(end_date)
            },
            'user_id': user_id
        }
    else:
        match_filter = {
            'date': convert_date(start_date),
            'user_id': user_id
        }
    return [
        {'$match': match_filter},
        {
            '$unwind': {
                'path': '$items',
                'preserveNullAndEmptyArrays': True
            }
        },
        {
            '$lookup': {
                'from': 'foods',
                'localField': 'items.id',
                'foreignField': '_id',
                'as': 'items.foodDetails'
            }
        },
        {
            '$unwind': {
                'path': '$items.foodDetails',
                'preserveNullAndEmptyArrays': True
            }
        },
        {
            '$group': {
                '_id': '$_id',
                'user_id': {'$first': '$user_id'},
                'date': {'$first': '$date'},
                'name': {'$first': '$name'},
                'index': {'$first': '$index'},
                'items': {
                    '$push': {
                        'id': '$items.id',
                        'weight': '$items.weight',
                        'name': '$items.foodDetails.name',
                        'kcal_per_100': '$items.foodDetails.kcal_per_100',
                        'carbs_per_100': '$items.foodDetails.carbs_per_100',
                        'prots_per_100': '$items.foodDetails.prots_per_100',
                        'fats_per_100': '$items.foodDetails.fats_per_100'
                    }
                }
            }
        },
        {
            '$project': {
                '_id': 1,
                'user_id': 1,
                'date': 1,
                'name': 1,
                'index': 1,
                'items': {
                    '$filter': {
                        'input': '$items',
                        'as': 'item',
                        'cond': {'$ifNull': ['$$item.id', False]}
                    }
                }
            }
        }
    ]


async def get_meals_by_date(
    db: AsyncIOMotorDatabase,
    user_id: str,
    target_date: str,
    limit: int = 100
) -> List[Meal] | None:
    try:
        pipeline: list = generate_pipeline(
            user_id=user_id, start_date=target_date)
        meals: list = await db.get_collection('meals').aggregate(pipeline=pipeline).to_list(limit)
        return meals_mongo_to_domain(meals)
    except ValueError:
        raise ValueError('Invalid date format. Expected format: YYYY-MM-DD')


async def get_meals_by_week(
    db: AsyncIOMotorDatabase,
    user_id: str,
    start_week_date: str,
    end_week_date: str,
    limit: int = 100
) -> List[Meal] | None:
    try:
        pipeline: list = generate_pipeline(
            user_id=user_id,
            start_date=start_week_date,
            end_date=end_week_date
        )
        meals: list = await db.get_collection('meals').aggregate(pipeline=pipeline).to_list(limit)
        return meals_mongo_to_domain(meals)
    except ValueError:
        raise ValueError('Invalid date format. Expected format: YYYY-MM-DD')


async def get_meal_by_id(
    db: AsyncIOMotorDatabase,
    user_id: str,
    meal_id: str
) -> dict | None:
    try:
        meal: dict = await db.get_collection('meals').find_one(
            filter={'user_id': user_id, '_id': ObjectId(meal_id)}
        )
        return meal
    except Exception as e:
        logging.error(f'An error occurred during the database operation: {e}')
        raise


async def delete_meal_by_id(
    db: AsyncIOMotorDatabase,
    user_id: str,
    meal_id: str
) -> int:
    try:
        result: DeleteResult = await db.get_collection('meals').delete_one(
            filter={'user_id': user_id, '_id': ObjectId(meal_id)}
        )
        return result.deleted_count
    except Exception as e:
        logging.error(f'An error occurred during the database operation: {e}')
        raise


async def set_new_names_by_old_name(
    db: AsyncIOMotorDatabase,
    user_id: str,
    meal_old_name: str,
    meal_new_name: str
) -> int:
    try:
        result: UpdateResult = await db.get_collection('meals').update_many(
            filter={'user_id': user_id, 'name': meal_old_name},
            update={'$set': {'name': meal_new_name}}
        )
        return result.modified_count
    except Exception as e:
        logging.error(f'An error occurred during the database operation: {e}')
        raise


async def set_new_name_by_id(
    db: AsyncIOMotorDatabase,
    user_id: str,
    meal_id: str,
    meal_new_name: str
) -> int:
    try:
        result: UpdateResult = await db.get_collection("meals").update_one(
            filter={'user_id': user_id, '_id': ObjectId(meal_id)},
            update={'$set': {'name': meal_new_name}}
        )
        return result.modified_count
    except Exception as e:
        logging.error(f'An error occurred during the database operation: {e}')
        raise


async def count_meals_by_date(
    db: AsyncIOMotorDatabase,
    user_id: str,
    target_date: datetime
) -> int:
    try:
        meals_count: int = await db.get_collection('meals').count_documents(
            filter={'user_id': user_id, 'date': target_date}
        )
        return meals_count
    except ValueError:
        raise ValueError('Invalid date format. Expected format: YYYY-MM-DD')


async def perform_bulk_meal_updates(
    db: AsyncIOMotorDatabase,
    bulk_updates: Sequence[
        InsertOne |
        DeleteOne |
        DeleteMany |
        ReplaceOne |
        UpdateOne |
        UpdateMany
    ],
) -> int:
    try:
        result: BulkWriteResult = await db.get_collection('meals').bulk_write(requests=bulk_updates)
        return result.modified_count
    except Exception as e:
        logging.error(f'An error occurred during the database operation: {e}')
        raise


async def add_new_food_item(
    db: AsyncIOMotorDatabase,
    user_id: str,
    meal_id: str,
    food_data: dict
) -> int:
    try:
        result: UpdateResult = await db.get_collection('meals').update_one(
            filter={'_id': ObjectId(meal_id), 'user_id': user_id},
            update={'$push': {'items': food_data}}
        )
        return result.modified_count
    except Exception as e:
        logging.error(f'An error occurred during the database operation: {e}')
        raise


async def set_food_weight_by_id(
    db: AsyncIOMotorDatabase,
    user_id: str,
    meal_id: str,
    food_id: str,
    weight: float
) -> int:
    try:
        result: UpdateResult = await db.get_collection('meals').update_one(
            filter={
                '_id': ObjectId(meal_id),
                'user_id': user_id,
                'items.id': ObjectId(food_id)
            },
            update={'$set': {'items.$[item].weight': weight}},
            array_filters=[{'item.id': ObjectId(food_id)}]
        )
        return result.modified_count
    except Exception as e:
        logging.error(f'An error occurred during the database operation: {e}')
        raise


async def delete_food_by_id(
    db: AsyncIOMotorDatabase,
    user_id: str,
    meal_id: str,
    food_id: str
) -> int:
    try:
        result: UpdateResult = await db.get_collection('meals').update_one(
            filter={
                '_id': ObjectId(meal_id),
                'user_id': user_id,
                'items.id': ObjectId(food_id)
            },
            update={'$pull': {'items': {'id': ObjectId(food_id)}}}
        )
        return result.modified_count
    except Exception as e:
        logging.error(f'An error occurred during the database operation: {e}')
        raise
