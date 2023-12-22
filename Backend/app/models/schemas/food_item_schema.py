from pydantic import BaseModel

class FoodItemIn(BaseModel):
    name: str
    kcal: float
    carbs: float
    prots: float
    fats: float