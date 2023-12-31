from motor.motor_asyncio import AsyncIOMotorClient, AsyncIOMotorDatabase

from app.core.config import settings

__client__: AsyncIOMotorClient = None


async def connect_to_mongo():
    global __client__
    __client__ = AsyncIOMotorClient(settings.database_url)


async def close_mongo_connection():
    global __client__
    __client__.close()


async def get_db_client() -> AsyncIOMotorClient:
    return __client__
