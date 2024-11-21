import datetime
from pydantic import BaseModel

from app.domain.explanation_level import ExplanationLevel


class SttRequestMessage(BaseModel):
    videoId: str
    userLevel: ExplanationLevel
    noteId: str