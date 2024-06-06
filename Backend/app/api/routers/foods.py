from bson import Regex
from fastapi import APIRouter, Depends, Query, Request, status
from motor.motor_asyncio import AsyncIOMotorDatabase

from app.api.dependencies import get_db
from app.crud.crud_foods import get_foods_by_pattern, get_total_count

router: APIRouter = APIRouter(prefix='/foods')


@router.get('', status_code=status.HTTP_200_OK)
async def search_foods(
    request: Request,
    pattern: str = Query(None, min_length=3),
    skip: int = Query(0),
    limit: int = Query(10),
    db: AsyncIOMotorDatabase = Depends(get_db)
) -> dict:
    regex: Regex = Regex(f'.*{pattern}.*', 'i')
    foods: list = await get_foods_by_pattern(
        db=db,
        regex=regex,
        skip=skip,
        limit=limit
    )
    total: int = await get_total_count(
        db=db,
        regex=regex
    )
    next_skip: int = skip + limit
    next_page_url: str = str(request.url.include_query_params(
        skip=next_skip,
        limit=limit))
    return {
        'results': foods,
        'pagination': {
            'total': total,
            'skip': skip,
            'limit': limit,
            'next_url': next_page_url if next_skip < total else None
        }
    }
