from datetime import datetime
from typing import List

from pydantic import BaseModel, Field, validator


class User(BaseModel):
    id: str = Field(default='-1', description='The user\'s id')
    username: str = Field(..., description='The user\'s username')
    email: str = Field(..., description='The user\'s email')
    hashed_password: str = Field(
        ...,
        description='The user\'s hashed password'
    )
    order_meal: List[str] = Field(
        default=[], description='The user\'s meals template in order')
    profile: dict = Field(..., description='The user\'s profile')


class Food(BaseModel):
    id: str = Field(default='-1', description='The food\'s id')
    name: str = Field(..., description='The food\'s name')
    kcal_per_100: float = Field(..., description='The food\'s kilocalories')
    carbs_per_100: float = Field(..., description='The food\'s carbohydrates')
    prots_per_100: float = Field(..., description='The food\'s proteins')
    fats_per_100: float = Field(..., description='The food\'s fats')


class FoodWeight(BaseModel):
    id: str = Field(default='-1', description='The food\'s id')
    weight: float = Field(..., description='The food\'s weight')


class FoodItem(Food, FoodWeight):
    @validator('id')
    def validate_ids(cls, v, values, **kwargs):
        if 'id' in values and v != values[id]:
            raise ValueError('Los id\'s no coinciden')
        return v


class Meal(BaseModel):
    id: str = Field(default='-1', description='The meal\'s id')
    user_id: str = Field(..., description='The meal\'s user\'s id')
    date: datetime = Field(..., description='The meal\'s date')
    name: str = Field(..., description='The meal\'s name')
    index: int = Field(..., description='The meal\'s index')
    items: List[FoodItem] = Field(default=[], description='The meal\'s foods')
