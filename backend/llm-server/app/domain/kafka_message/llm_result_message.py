from datetime import datetime

from pydantic import BaseModel

class LLMResultMessage(BaseModel):
    noteId: str
    startTime: int
    content: str