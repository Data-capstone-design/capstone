from datetime import datetime

from pydantic import BaseModel

class LLMResultMessage(BaseModel):
    noteId: str
    startTime: int
    commentaryOrder: int
    content: str