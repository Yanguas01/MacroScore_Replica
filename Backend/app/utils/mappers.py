from typing import List

from app.models.domain import Meal, Token, User, Food

def user_mongo_to_domain(user: dict) -> User:
    return User(
        id = str(user['_id']),
        username = user['username'],
        email = user['email'],
        hashed_password = user['hashed_password'],
        order_meal = user['order_meal']
    )

def meal_mongo_to_domain(meal: dict) -> Meal:
    return Meal(
        id = str(meal['_id']),
        user_id = meal['user_id'],
        date = meal['date'],
        name = meal['name'],
        index = meal['index'],
        items = meal['items']
    )

def meals_mongo_to_domain(meals: list) -> List[Meal]:
    return [meal_mongo_to_domain(meal) for meal in meals]


def token_mongo_to_domain(token: dict) -> Token:
    return Token(
        id = str(token['_id']),
        created_at= token['created_at']
    )

def food_mongo_to_domain(food: dict) -> Food:
    return Food(
        id = str(food['_id']),
        name = food['name'],
        kcal_per_100 = food['kcal_per_100'],
        carbs_per_100 = food['carbs_per_100'],
        prots_per_100 = food['prots_per_100'],
        fats_per_100 = food['fats_per_100']
    )