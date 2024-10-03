from fastapi import APIRouter, HTTPException
from app.workflows.process_video import process_video
from app.dto.video_dto import VideoProcessRequest
from starlette.responses import JSONResponse

router = APIRouter()

@router.post("/process-video")
async def process_video_endpoint(request: VideoProcessRequest):
    try:
        result = process_video(request.youtube_url)

        return JSONResponse(status_code=200, content={"message": "비디오가 처리되었습니다."})
    
    except Exception as e:
        print("에러남")
        print(e)
        raise HTTPException(status_code=500, detail=str(e))
