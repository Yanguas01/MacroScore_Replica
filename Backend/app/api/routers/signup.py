from fastapi import APIRouter, Depends
from motor.motor_asyncio import AsyncIOMotorDatabase

from app.api.dependencies import get_db
from app.models.schemas.user_schema import UserIn, UserOut
from app.services.user_service import register_new_user

router: APIRouter = APIRouter(prefix='/signup')


@router.post('/', response_model=UserOut)
async def signup(
    user_in: UserIn,
    db: AsyncIOMotorDatabase = Depends(get_db)
) -> UserOut:
    user_id: str = await register_new_user(db=db, user_in=user_in)
    user_out_data: dict = user_in.model_dump(exclude='password')
    user_out_data['id'] = user_id
    user_out_data['order_meal'] = []
    return UserOut(**user_out_data)
