from typing import List

from app.models.domain import Food, FoodItem, Meal, User


def user_mongo_to_domain(user: dict) -> User:
    return User(
        id=str(user['_id']),
        username=user['username'],
        email=user['email'],
        hashed_password=user['hashed_password'],
        order_meal=user['order_meal'],
        profile=user['profile']
    )


def food_item_mongo_to_domain(food: dict) -> FoodItem:
    return FoodItem(
        id=str(food['id']),
        weight=food['weight'],
        name=food['name'],
        kcal_per_100=food['kcal_per_100'],
        carbs_per_100=food['carbs_per_100'],
        prots_per_100=food['prots_per_100'],
        fats_per_100=food['fats_per_100']
    )


def food_items_mongo_to_domain(foods: list) -> List[FoodItem]:
    return [food_item_mongo_to_domain(food) for food in foods]


def meal_mongo_to_domain(meal: dict) -> Meal:
    return Meal(
        id=str(meal['_id']),
        user_id=meal['user_id'],
        date=meal['date'],
        name=meal['name'],
        index=meal['index'],
        items=food_items_mongo_to_domain(meal['items'])
    )


def meals_mongo_to_domain(meals: list) -> List[Meal]:
    return [meal_mongo_to_domain(meal) for meal in meals]


def food_mongo_to_domain(food: dict) -> Food:
    return Food(
        id=str(food['_id']),
        name=food['name'],
        kcal_per_100=food['kcal_per_100'],
        carbs_per_100=food['carbs_per_100'],
        prots_per_100=food['prots_per_100'],
        fats_per_100=food['fats_per_100']
    )


def foods_mongo_to_domain(foods: list) -> List[Food]:
    return [food_mongo_to_domain(food) for food in foods]
