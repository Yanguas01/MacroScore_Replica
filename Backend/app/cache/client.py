from aioredis import Redis

from app.core.config import settings

__client__: Redis = None


async def connect_to_redis():
    global __client__
    __client__ = await Redis.from_url(settings.cache_url)


async def close_redis_connection():
    global __client__
    await __client__.close()


async def get_redis_client() -> Redis:
    return __client__
