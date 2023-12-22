from datetime import datetime, timedelta

from fastapi import HTTPException
from jose import JWTError, jwt
from motor.motor_asyncio import AsyncIOMotorDatabase

from app.core.config import settings
from app.crud.crud_token import delete_token_by_id, get_token_by_id, save_token


def create_access_token(data: dict):
    to_encode = data.copy()
    expire = datetime.utcnow() + timedelta(minutes=settings.access_token_expire_minutes)
    to_encode.update({"exp": expire.timestamp(), "type": "access"})
    encoded_jwt = jwt.encode(
        claims=to_encode,
        key=settings.secret_key,
        algorithm=settings.algorithm
    )
    return encoded_jwt


async def create_refresh_token(db: AsyncIOMotorDatabase, data: dict):
    to_encode = data.copy()
    jti = await save_token(db, {"created_at": datetime.utcnow()})
    expire = datetime.utcnow() + timedelta(minutes=settings.refresh_token_expire_minutes)
    to_encode.update({
        "jti": jti,
        "exp": expire.timestamp(),
        "type": "refresh"
    })
    encoded_jwt = jwt.encode(
        claims=to_encode,
        key=settings.secret_key,
        algorithm=settings.algorithm
    )
    return encoded_jwt


def verify_access_token(token: str, credentials_exception: HTTPException):
    try:
        payload = jwt.decode(
            token=token,
            key=settings.secret_key,
            algorithms=[settings.algorithm]
        )
        if payload.get("type") != "access":
            raise credentials_exception
        username: str = payload.get("sub")
        if username is None:
            raise credentials_exception
    except JWTError:
        raise credentials_exception
    return username


async def verify_refresh_token(db: AsyncIOMotorDatabase, token: str, exception: HTTPException):
    try:
        payload = jwt.decode(
            token=token,
            key=settings.secret_key,
            algorithms=[settings.algorithm]
        )
        if payload.get("type") != "refresh":
            raise exception
        jti = payload.get("jti")
        if await get_token_by_id(db, jti) is None:
            raise exception
        username: str = payload.get("sub")
        if username is None:
            raise exception
    except JWTError:
        raise exception
    await delete_token_by_id(db, jti)
    return username
