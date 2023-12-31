from contextlib import asynccontextmanager

from fastapi import FastAPI

from app.api.routers import foods, login, meals, refresh, signup, users
from app.cache.client import close_redis_connection, connect_to_redis
from app.db.client import close_mongo_connection, connect_to_mongo


@asynccontextmanager
async def lifespan(app: FastAPI):
    await connect_to_mongo()
    await connect_to_redis()
    yield
    await close_redis_connection()
    await close_mongo_connection()

app = FastAPI(lifespan=lifespan)

app.include_router(foods.router)
app.include_router(login.router)
app.include_router(signup.router)
app.include_router(meals.router)
app.include_router(refresh.router)
app.include_router(users.router)
