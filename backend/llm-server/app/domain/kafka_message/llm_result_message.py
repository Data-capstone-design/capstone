from datetime import datetime

from pydantic import BaseModel

class LLMResultMessage(BaseModel):
    noteId: str
    indexId: int
    content: str