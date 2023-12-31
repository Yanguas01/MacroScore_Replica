from datetime import datetime

from pydantic import BaseModel


class MealIn(BaseModel):
    name: str
    datetime: str
    save_meal: bool


class MealOrder(BaseModel):
    id: str
    name: str
    index: int


class MealOut(BaseModel):
    id: str
    name: str
    datetime: datetime
