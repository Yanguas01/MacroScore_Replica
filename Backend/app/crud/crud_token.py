from bson.objectid import ObjectId
from motor.motor_asyncio import AsyncIOMotorDatabase

from app.utils.mappers import token_mongo_to_domain


async def save_token(db: AsyncIOMotorDatabase, token_data: dict):
    result = await db.get_collection("tokens").insert_one(token_data)
    return str(result.inserted_id)


async def get_token_by_id(db: AsyncIOMotorDatabase, token_id: str):
    token = await db.get_collection("tokens").find_one({"_id": ObjectId(token_id)})
    return token_mongo_to_domain(token)


async def delete_token_by_id(db: AsyncIOMotorDatabase, token_id: str):
    await db.get_collection("tokens").find_one_and_delete({"_id": ObjectId(token_id)})
