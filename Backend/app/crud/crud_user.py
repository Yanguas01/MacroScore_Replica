import logging
from typing import List

from bson.objectid import ObjectId
from motor.motor_asyncio import AsyncIOMotorDatabase
from pymongo import ReturnDocument
from pymongo.results import InsertOneResult, UpdateResult

from app.models.domain import User
from app.utils.mappers import user_mongo_to_domain


async def create_user(
    db: AsyncIOMotorDatabase,
    user_data: dict
) -> str:
    try:
        result: InsertOneResult = await db.get_collection('users').insert_one(user_data)
        return str(result.inserted_id)
    except Exception as e:
        logging.error(f'An error occurred during the database operation: {e}')
        raise


async def get_user_by_id(
    db: AsyncIOMotorDatabase,
    id: str
) -> User | None:
    try:
        user: dict = await db.get_collection('users').find_one({'_id': ObjectId(id)})
        return user_mongo_to_domain(user) if user else None
    except Exception as e:
        logging.error(f'An error occurred during the database operation: {e}')
        raise


async def get_user_by_username(
    db: AsyncIOMotorDatabase,
    username: str
) -> User | None:
    try:
        user: dict = await db.get_collection('users').find_one({'username': username})
        return user_mongo_to_domain(user) if user else None
    except Exception as e:
        logging.error(f'An error occurred during the database operation: {e}')
        raise


async def get_user_by_email(
    db: AsyncIOMotorDatabase,
    email: str
) -> User | None:
    try:
        user: dict = await db.get_collection('users').find_one({'email': email})
        return user_mongo_to_domain(user) if user else None
    except Exception as e:
        logging.error(f'An error occurred during the database operation: {e}')
        raise


async def add_meal(
    db: AsyncIOMotorDatabase,
    meal_name: str,
    user_id: str
) -> int:
    try:
        result: UpdateResult = await db.get_collection('users').update_one(
            {'_id': ObjectId(user_id)},
            {'$push': {'order_meal': meal_name}}
        )
        return result.modified_count
    except Exception as e:
        logging.error(f'An error occurred during the database operation: {e}')
        raise


async def update_order_meal(
    db: AsyncIOMotorDatabase,
    user_id: str,
    new_order_meal: List[str]
) -> int:
    try:
        result: UpdateResult = await db.get_collection('users').update_one(
            {'_id': ObjectId(user_id)},
            {'$set': {'order_meal': new_order_meal}}
        )
        return result.modified_count
    except Exception as e:
        logging.error(f'An error occurred during the database operation: {e}')
        raise


async def update_user_by_id(
    db: AsyncIOMotorDatabase,
    user_data: dict
) -> User | None:
    try:
        id: str = user_data.pop('id')
        user: dict = await db.get_collection('users').find_one_and_replace(
            filter={'_id': ObjectId(id)},
            replacement=user_data,
            return_document=ReturnDocument.AFTER
        )
        return user_mongo_to_domain(user=user) if user else None
    except Exception as e:
        logging.error(f'An error occurred during the database operation: {e}')
        raise


async def update_password_by_id(
    db: AsyncIOMotorDatabase,
    user_id: str,
    new_hashed_password: str
) -> int:
    try:
        result: UpdateResult = await db.get_collection('users').update_one(
            {'_id': ObjectId(user_id)},
            {'$set': {'hashed_password': new_hashed_password}}
        )
        return result.modified_count
    except Exception as e:
        logging.error(f'An error occurred during the database operation: {e}')
        raise
