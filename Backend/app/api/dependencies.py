from typing import Generator, AsyncGenerator

from aioredis import Redis
from fastapi import Depends, HTTPException, status
from fastapi.security import OAuth2PasswordBearer
from motor.motor_asyncio import AsyncIOMotorClient, AsyncIOMotorDatabase

from app.cache.client import get_redis_client
from app.core.security import verify_access_token
from app.crud.crud_user import get_user_by_username
from app.db.client import get_db_client
from app.models.domain import User

oauth2_scheme = OAuth2PasswordBearer(tokenUrl='/login')


async def get_db() -> AsyncGenerator[AsyncIOMotorDatabase, None]:
    client: AsyncIOMotorClient = await get_db_client()
    try:
        db: AsyncIOMotorDatabase = client.appDB
        yield db
    finally:
        pass


async def get_cache() -> AsyncGenerator[Redis, None]:
    client: Redis = await get_redis_client()
    try:
        yield client
    finally:
        pass


async def get_current_user(db: AsyncIOMotorDatabase = Depends(get_db), token: str = Depends(oauth2_scheme)) -> User:
    credentials_exception: HTTPException = HTTPException(
        status_code=status.HTTP_401_UNAUTHORIZED,
        detail='Could not validate credentials',
        headers={'WWW-Authenticate': 'Bearer'}
    )
    username: str = verify_access_token(token, credentials_exception)
    user: User = await get_user_by_username(db, username)
    if user is None:
        raise credentials_exception
    return user
