from app.domain.event_handler_factory_dto import EventHandlerFactoryDTO
from app.domain.generation_process import GenerationProcess
from app.openai_service.event_handler.enhanced_event_handler import EnhancedExplanationEventHandler
from app.openai_service.event_handler.explanation_event_handler import ExplanationEventHandler
from app.openai_service.event_handler.feedback_event_handler import FeedbackEventHandler
from app.openai_service.event_handler.index_event_handler import IndexEventHandler


async def event_handler_factory(dto: EventHandlerFactoryDTO):
    match dto.process_stage:
        case GenerationProcess.OUTLINE_GENERATION:
            return IndexEventHandler(note_id=dto.note_id, producer=dto.kafka_producer)
        case GenerationProcess.EXPLANATION_GENERATION:
            return ExplanationEventHandler(note_id=dto.note_id, chunk_index=dto.chunk_index)
        case GenerationProcess.FEEDBACK_GENERATION:
            return FeedbackEventHandler(note_id=dto.note_id, chunk_index=dto.chunk_index, thread_id=dto.thread_id)
        case GenerationProcess.ENHANCED_EXPLANATION_GENERATION:
            return EnhancedExplanationEventHandler(
                note_id=dto.note_id,
                chunk_index=dto.chunk_index,
                outline_start_time=dto.outline_start_time,
                thread_id=dto.thread_id,
                kafka_producer=dto.kafka_producer,
            )