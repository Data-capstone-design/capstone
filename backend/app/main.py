from fastapi import FastAPI, HTTPException, Request
from starlette.middleware.cors import CORSMiddleware
import logging
import uvicorn
from fastapi.responses import JSONResponse
import logging
from app.routes import video_routes

app = FastAPI(
    title="YouTube Video Processing Service",
    description="API 서버",
    version="1.0.0",
    debug=True
)

origins = [
    "http://127.0.0.1:8080",
]

app.add_middleware(
    CORSMiddleware,
    allow_origins=origins,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

@app.get("/video")
def hello():
    return {"message": "테스트"}

app.include_router(video_routes.router, prefix="/api", tags=["Video Processing"])


# HTTPException에 대한 예외 처리 핸들러
@app.exception_handler(HTTPException)
async def custom_http_exception_handler(request: Request, exc: HTTPException):
    return JSONResponse(
        status_code=exc.status_code,
        content={"message": exc.detail},
    )


# 전역 예외 처리 핸들러
@app.exception_handler(Exception)
async def global_exception_handler(request: Request, exc: Exception):
    logging.error(f"Exception: {exc}")  # 로깅 추가 (선택 사항)
    return JSONResponse(
        status_code=500,
        content={"message": "Internal Server Error", "detail": str(exc)},
    )


if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=8000)

 