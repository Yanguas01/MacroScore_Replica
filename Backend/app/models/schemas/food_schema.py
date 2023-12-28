from pydantic import BaseModel


class FoodIn(BaseModel):
    id: str
    weight: float
