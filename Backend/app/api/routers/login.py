from aioredis import Redis
from fastapi import APIRouter, Depends
from fastapi.security import OAuth2PasswordRequestForm
from motor.motor_asyncio import AsyncIOMotorDatabase

from app.api.dependencies import get_cache, get_db
from app.core.security import create_access_token, create_refresh_token
from app.models.domain import User
from app.services.user_service import authenticate_user

router: APIRouter = APIRouter(prefix='/login')


@router.post('/token')
async def login_for_access_token(
    cache: Redis = Depends(get_cache),
    db: AsyncIOMotorDatabase = Depends(get_db),
    form_data: OAuth2PasswordRequestForm = Depends()
) -> dict:
    user: User = await authenticate_user(
        db=db,
        username=form_data.username,
        password=form_data.password
    )
    access_token: str = create_access_token(data={'sub': user.username})
    refresh_token: str = await create_refresh_token(
        cache=cache,
        data={'sub': user.username}
    ) if 'keep_logged_in' in form_data.scopes else None
    return {
        'access_token': access_token,
        'refresh_token': refresh_token,
        'token_type': 'bearer'
    }
