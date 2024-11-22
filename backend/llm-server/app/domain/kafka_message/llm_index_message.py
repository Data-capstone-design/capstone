from typing import List

from pydantic import BaseModel

class Segment(BaseModel):
    startTime: int
    title: str
    summary: str

class IndexMessage(BaseModel):
    noteId: str
    segments: List[Segment]