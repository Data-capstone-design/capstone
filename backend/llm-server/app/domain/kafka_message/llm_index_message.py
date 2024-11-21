from pydantic import BaseModel

class IndexMessage(BaseModel):
    noteId: str
    segments: str