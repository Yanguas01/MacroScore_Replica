from bson import ObjectId

from motor.motor_asyncio import AsyncIOMotorDatabase

from app.utils.mappers import food_mongo_to_domain


async def get_food_by_id(db: AsyncIOMotorDatabase, food_id: str):
    food = await db.get_collection("foods").find_one({"_id": ObjectId(food_id)})
    return food_mongo_to_domain(food) if food else None