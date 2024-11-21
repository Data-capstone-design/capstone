from pydantic import BaseModel

from app.domain.explanation_level import ExplanationLevel


class LlmRequestMessage(BaseModel):
    videoId: str
    explanationLevel: ExplanationLevel
    noteId: str
    transcriptionText: str