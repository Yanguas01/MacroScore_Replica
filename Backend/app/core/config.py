import os

from dotenv import load_dotenv
from pydantic_settings import BaseSettings

load_dotenv()

"""
class Settings(BaseSettings):
    database_url: str = os.getenv('DATABASE_URL')
    secret_key: str = os.getenv('SECRET_KEY')
    algorithm: str = os.getenv('ALGORITHM')
    access_token_expire_minutes: int = os.getenv('ACCESS_TOKEN_EXPIRE_MINUTES')
    refresh_token_expire_minutes: int = os.getenv(
        'REFRESH_TOKEN_EXPIRE_MINUTES')


    class Config:
        env_file = '../.env'


settings = Settings()
"""

class Settings(BaseSettings):
    database_url: str
    secret_key: str
    algorithm: str
    access_token_expire_minutes: int
    refresh_token_expire_minutes: int

settings = Settings()