from bson.objectid import ObjectId
from motor.motor_asyncio import AsyncIOMotorDatabase

from app.utils.mappers import user_mongo_to_domain

from typing import List


async def create_user(db: AsyncIOMotorDatabase, user_data: dict):
    result = await db.get_collection("users").insert_one(user_data)
    return result.inserted_id


async def get_user_by_id(db: AsyncIOMotorDatabase, id: str):
    user = await db.get_collection("users").find_one({"_id": ObjectId(id)})
    return user_mongo_to_domain(user) if user else None


async def get_user_by_username(db: AsyncIOMotorDatabase, username: str):
    user = await db.get_collection("users").find_one({"username": username})
    return user_mongo_to_domain(user) if user else None


async def get_user_by_email(db: AsyncIOMotorDatabase, email: str):
    user = await db.get_collection("users").find_one({"email": email})
    return user_mongo_to_domain(user) if user else None


async def add_meal(db: AsyncIOMotorDatabase, meal_name: str, user_id: str):
    await db.get_collection("users").update_one(
        {"_id": ObjectId(user_id)},
        {"$push": {"order_meal": meal_name}}
    )


async def update_order_meal(db: AsyncIOMotorDatabase, user_id: str, new_order_meal: List[str]):
    await db.get_collection("users").update_one(
        {"_id": ObjectId(user_id)},
        {"$set": {"order_meal": new_order_meal}}
    )
