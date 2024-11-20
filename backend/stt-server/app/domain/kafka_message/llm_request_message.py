from pydantic import BaseModel

from app.domain.explanationLevel import ExplanationLevel


class LlmRequestMessage(BaseModel):
    videoId: str
    explanationLevel: ExplanationLevel
    noteId: str
    transcriptionText: str
