from datetime import datetime

from passlib.context import CryptContext

pwd_context = CryptContext(schemes=['bcrypt'], deprecated='auto')


def verify_password(plain_password: str, hashed_password: str) -> bool:
    return pwd_context.verify(plain_password, hashed_password)


def get_password_hash(password: str) -> str:
    return pwd_context.hash(password)


def convert_date(target_date: str) -> datetime:
    try:
        date: datetime = datetime.strptime(target_date, "%Y-%m-%d")
        return date
    except ValueError:
        raise ValueError('Invalid date format. Expected format: YYYY-MM-DD')
