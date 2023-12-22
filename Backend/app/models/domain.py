from datetime import datetime
from typing import List

from pydantic import BaseModel, Field


class User(BaseModel):
    id: str = Field(default="-1", description="The user's id")
    username: str = Field(..., description="The user's username")
    email: str = Field(..., description="The user's email")
    hashed_password: str = Field(..., description="The user's hashed password")
    order_meal: List[str] = Field(
        default=[], description="The user's meals template in order")


class FoodItem(BaseModel):
    id: str = Field(default="-1", description="The food's id")
    name: str = Field(..., description="The food's name")
    kcal: float = Field(..., description="The food's kilocalories")
    carbs: float = Field(..., description="The food's carbohydrates")
    prots: float = Field(..., description="The food's proteins")
    fats: float = Field(..., description="The food's fats")


class Meal(BaseModel):
    id: str = Field(default="-1", description="The meal's id")
    user_id: str = Field(..., description="The meal's user's id")
    date: datetime = Field(..., description="The meal's date")
    name: str = Field(..., description="The meal's name")
    index: int = Field(..., description="The meal's index")
    items: List[FoodItem] = Field(default=[], description="The meal's foods")


class Token(BaseModel):
    id: str = Field(default="-1", description="The token's id")
    created_at: datetime = Field(..., description="The token's creation datetime")