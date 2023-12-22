from datetime import timedelta

from fastapi import APIRouter, Depends
from fastapi.security import OAuth2PasswordRequestForm
from motor.motor_asyncio import AsyncIOMotorDatabase

from app.api.dependencies import get_db
from app.core.config import settings
from app.core.security import create_access_token, create_refresh_token
from app.services.user_service import authenticate_user

router: APIRouter = APIRouter(prefix="/login")


@router.post("/token")
async def login_for_access_token(db: AsyncIOMotorDatabase = Depends(get_db), form_data: OAuth2PasswordRequestForm = Depends()):
    user = await authenticate_user(db, form_data.username, form_data.password)
    access_token = create_access_token(data={"sub": user.username})
    refresh_token = await create_refresh_token(
        db,
        data={"sub": user.username}
    ) if "keep_logged_in" in form_data.scopes else None
    return {
        "access_token": access_token,
        "refresh_token": refresh_token,
        "token_type": "bearer"
    }
