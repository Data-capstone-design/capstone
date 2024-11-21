from pydantic import BaseModel

from app.domain.explanation_level import ExplanationLevel


class LlmRequestMessage(BaseModel):
    videoId: str
    explanationLevel: ExplanationLevel
    noteId: str
    transcriptionText: str
    class Config:
        use_enum_values = True  # Enum을 자동으로 문자열로 변환