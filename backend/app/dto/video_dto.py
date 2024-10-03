from pydantic import BaseModel

class VideoProcessRequest(BaseModel):
    youtube_url: str