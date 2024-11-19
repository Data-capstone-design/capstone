import datetime
from pydantic import BaseModel

class SttRequestMessage(BaseModel):
    videoId: str
    explanationLevel: str