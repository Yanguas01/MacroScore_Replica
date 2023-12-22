from fastapi import APIRouter, Depends
from motor.motor_asyncio import AsyncIOMotorDatabase
from app.api.dependencies import get_db
from app.models.schemas.user_schema import UserIn, UserOut
from app.services.user_service import register_new_user

router: APIRouter = APIRouter(prefix = "/signup")

@router.post("/", response_description = "Add new user", response_model = UserOut)
async def signup(user_in: UserIn, db: AsyncIOMotorDatabase = Depends(get_db)):
    await register_new_user(db, user_in)
    user_out = UserOut(**user_in.model_dump(exclude={"password"}))
    return user_out