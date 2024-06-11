from pydantic import BaseModel


class UserOut(BaseModel):
    id: str
    username: str
    email: str
    order_meal: list
    profile: dict


class UserIn(BaseModel):
    username: str
    email: str
    profile: dict
    password: str


class UserUpdateRequest(BaseModel):
    username: str | None = None
    email: str | None = None
    gender: int | None = None
    height: int | None = None
    weight: int | None = None
    age: int | None = None
    physical_activity_level: int | None = None
