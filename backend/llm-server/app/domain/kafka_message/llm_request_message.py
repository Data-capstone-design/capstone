from pydantic import BaseModel

class LlmRequestMessage(BaseModel):
    videoId: str
    explanationLevel: str
    transcriptionText: str