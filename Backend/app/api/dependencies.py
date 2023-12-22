from typing import Generator

from fastapi import Depends, HTTPException, status
from fastapi.security import OAuth2PasswordBearer
from motor.motor_asyncio import AsyncIOMotorDatabase

from app.core.security import verify_access_token
from app.crud.crud_user import get_user_by_username
from app.db.client import get_db_client
from app.models.domain import User

oauth2_scheme = OAuth2PasswordBearer(tokenUrl="/login/token")


async def get_db() -> Generator:
    client = await get_db_client()
    try:
        db = client.local
        yield db
    finally:
        pass


async def get_current_user(db: AsyncIOMotorDatabase = Depends(get_db), token: str = Depends(oauth2_scheme)) -> User:
    credentials_exception = HTTPException(
        status_code=status.HTTP_401_UNAUTHORIZED,
        detail="Could not validate credentials",
        headers={"WWW-Authenticate": "Bearer"},
    )
    username = verify_access_token(token, credentials_exception)
    user = await get_user_by_username(db, username)
    if user is None:
        raise credentials_exception
    return user
