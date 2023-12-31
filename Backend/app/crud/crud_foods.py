import logging
from typing import List

from bson import ObjectId, Regex
from motor.motor_asyncio import AsyncIOMotorDatabase

from app.models.domain import Food
from app.utils.mappers import food_mongo_to_domain, foods_mongo_to_domain


async def get_food_by_id(db: AsyncIOMotorDatabase, food_id: str) -> Food | None:
    food: dict = await db.get_collection('foods').find_one({'_id': ObjectId(food_id)})
    return food_mongo_to_domain(food) if food else None


async def get_foods_by_pattern(db: AsyncIOMotorDatabase, regex: Regex, skip: int, limit: int) -> List[Food]:
    try:
        foods: list = await db.get_collection('foods').find(
            {'name': regex}
        ).skip(skip=skip).to_list(length=limit)
        return foods_mongo_to_domain(foods=foods)
    except Exception as e:
        logging.error(f'An error occurred during the database operation: {e}')
        raise


async def get_total_count(db: AsyncIOMotorDatabase, regex: Regex) -> int:
    try:
        foods_count: int = await db.get_collection('foods').count_documents(
            filter={'name': regex}
        )
        return foods_count
    except Exception as e:
        logging.error(f'An error occurred during the database operation: {e}')
        raise
