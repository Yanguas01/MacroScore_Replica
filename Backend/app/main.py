from contextlib import asynccontextmanager

from fastapi import FastAPI

from app.api.routers import login, meals, refresh, signup
from app.db.client import close_mongo_connection, connect_to_mongo


@asynccontextmanager
async def lifespan(app: FastAPI):
    await connect_to_mongo()
    yield
    await close_mongo_connection()

app = FastAPI(lifespan=lifespan)


@app.get("/")
async def root():
    return {"Saludo": "Hola Mundo"}

app.include_router(login.router)
app.include_router(signup.router)
app.include_router(meals.router)
app.include_router(refresh.router)
