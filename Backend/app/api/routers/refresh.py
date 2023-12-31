from aioredis import Redis
from fastapi import APIRouter, Body, Depends, HTTPException, status
from motor.motor_asyncio import AsyncIOMotorDatabase

from app.api.dependencies import get_cache, get_db
from app.core.security import (create_access_token, create_refresh_token,
                               verify_refresh_token)
from app.crud.crud_user import get_user_by_username
from app.models.domain import User

router: APIRouter = APIRouter(prefix='/refresh')


@router.post('/')
async def refresh_token(
    cache: Redis = Depends(get_cache),
    db: AsyncIOMotorDatabase = Depends(get_db),
    refresh_token: str = Body(...)
) -> dict:
    refresh_exception: HTTPException = HTTPException(
        status_code=status.HTTP_401_UNAUTHORIZED,
        detail='Could not validate refresh token',
        headers={'WWW-Authenticate': 'Bearer'},
    )
    username: str = await verify_refresh_token(
        cache=cache,
        token=refresh_token,
        exception=refresh_exception
    )
    user: User = await get_user_by_username(db=db, username=username)
    if not user:
        raise refresh_exception
    access_token: str = create_access_token(data={'sub': user.username})
    new_refresh_token: str = await create_refresh_token(cache=cache, data={'sub': user.username})
    return {
        'access_token': access_token,
        'refresh_token': new_refresh_token,
        'token_type': 'bearer'
    }
