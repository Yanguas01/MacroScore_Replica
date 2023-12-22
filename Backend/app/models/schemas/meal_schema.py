from pydantic import BaseModel
from datetime import date

class MealIn(BaseModel):
    name: str
    datetime: date
    save_meal: bool

class MealOrder(BaseModel):
    id: str
    name: str
    index: int