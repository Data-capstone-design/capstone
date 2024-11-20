import uvicorn
from fastapi import FastAPI

from app.lifespan import lifespan

app = FastAPI(lifespan=lifespan)

if __name__ == "__main__":
    uvicorn.run("main:app", host="0.0.0.0", port=8001, reload=True)

@app.get("/")
def read_root():
    return {"message": "STT Server is running."}