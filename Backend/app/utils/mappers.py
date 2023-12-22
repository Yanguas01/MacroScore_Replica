from typing import List

from app.models.domain import Meal, Token, User

def user_mongo_to_domain(user: dict) -> User:
    return User(
        id = str(user["_id"]),
        username = user["username"],
        email = user["email"],
        hashed_password = user["hashed_password"],
        order_meal = user["order_meal"]
    )

def meal_mongo_to_domain(meal: dict) -> Meal:
    return Meal(
        id = str(meal["_id"]),
        user_id = meal["user_id"],
        date = meal["date"],
        name = meal["name"],
        index = meal["index"],
        items = meal["items"]
    )

def meals_mongo_to_domain(meals: list) -> List[Meal]:
    return [meal_mongo_to_domain(meal) for meal in meals]


def token_mongo_to_domain(token: dict) -> Token:
    return Token(
        id = str(token["_id"]),
        created_at= token["created_at"]
    )
