from pydantic import BaseModel
from typing import Optional
from app.domain.generation_process import GenerationProcess


class EventHandlerFactoryDTO(BaseModel):
    process_stage: GenerationProcess  # 필수, 현재 GenerationProcess 단계
    note_id: Optional[str] = None  # 노트 ID
    chunk_index: Optional[int] = None  # 청크 인덱스
    thread_id: Optional[str] = None  # 스레드 ID
    outline_start_time: Optional[float] = None  # 아웃라인 시작 시간
    kafka_producer: Optional[object] = None  # Kafka 프로듀서