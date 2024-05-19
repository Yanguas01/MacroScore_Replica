from datetime import datetime, timedelta, timezone
from uuid import uuid4

from aioredis import Redis
from fastapi import HTTPException
from jose import JWTError, jwt

from app.core.config import settings


def create_access_token(data: dict) -> str:
    to_encode: dict = data.copy()
    expire: datetime = datetime.now(timezone.utc) + timedelta(minutes=settings.access_token_expire_minutes)
    to_encode.update({'exp': expire.timestamp(), 'type': 'access'})
    return jwt.encode(
        claims=to_encode,
        key=settings.secret_key,
        algorithm=settings.algorithm
    )


async def create_refresh_token(cache: Redis, data: dict) -> str:
    jti: str = str(uuid4())
    ttl: int = settings.refresh_token_expire_minutes * 60
    print(jti)
    await cache.setex(name=jti, time=ttl, value='active')
    expire: datetime = datetime.now(timezone.utc) + timedelta(seconds=ttl)
    to_encode: dict = data.copy()
    to_encode.update({
        'jti': jti,
        'exp': expire.timestamp(),
        'type': 'refresh'
    })
    return jwt.encode(
        claims=to_encode,
        key=settings.secret_key,
        algorithm=settings.algorithm
    )


def verify_access_token(token: str, credentials_exception: HTTPException) -> str:
    try:
        payload: dict = jwt.decode(
            token=token,
            key=settings.secret_key,
            algorithms=[settings.algorithm]
        )
        if payload.get('type') != 'access':
            raise credentials_exception
        username: str = payload.get('sub')
        if username is None:
            raise credentials_exception
    except JWTError:
        raise credentials_exception
    return username


async def verify_refresh_token(cache: Redis, token: str, exception: HTTPException) -> str:
    try:
        payload: dict = jwt.decode(
            token=token,
            key=settings.secret_key,
            algorithms=[settings.algorithm]
        )
        if payload.get('type') != 'refresh':
            raise exception
        jti: str = payload.get('jti')
        if await cache.get(jti) is None:
            raise exception
        username: str = payload.get('sub')
        if username is None:
            raise exception
    except JWTError:
        raise exception
    await cache.delete(jti)
    return username
