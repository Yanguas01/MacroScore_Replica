from pydantic import BaseModel

class UserOut(BaseModel):
    username: str
    email: str

class UserIn(UserOut):
    password: str