from typing import List

from pydantic import BaseModel

class Segment(BaseModel):
    start_time: str
    index: str
    summary: str

class IndexMessage(BaseModel):
    noteId: str
    segments: List[Segment]