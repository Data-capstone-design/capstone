import asyncio
import json
import re
import time

from app.domain.event_handler_factory_dto import EventHandlerFactoryDTO
from app.domain.generation_process import GenerationProcess
from app.domain.kafka_message.llm_request_message import LlmRequestMessage
from app.domain.kafka_message.llm_result_message import LLMResultMessage
from app.kafka.kafka_config import LLM_COMMENTARY_EVENTS
from app.message_processor.instructions.instructions import *
from app.openai_service.assistant_api_utils import *
from app.openai_service.event_handler.event_handler_factory import event_handler_factory
from app.text_utils.text_utils import TextUtils


async def read_text_file(file_path: str) -> str:
    """
    주어진 경로의 텍스트 파일을 읽어 내용을 문자열로 반환.

    Parameters:
        file_path (str): 읽을 텍스트 파일의 경로

    Returns:
        str: 파일의 내용
    """
    try:
        with open(file_path, 'r', encoding='utf-8') as file:
            return file.read()
    except FileNotFoundError:
        print(f"파일을 찾을 수 없습니다: {file_path}")
        return ""
    except Exception as e:
        print(f"파일을 읽는 중 오류가 발생했습니다: {e}")
        return ""

class MessageProcessor:
    def __init__(self,request_message:LlmRequestMessage ,assistants, producer):
        self.video_id = request_message.videoId
        self.note_id = request_message.noteId
        self.total_chunks = None
        self.index_assistant = assistants.index_assistant
        self.explanation_assistant = assistants.explanation_assistant
        self.explanation_level = request_message.explanationLevel.value
        self.producer = producer
        self.start_times = []

    async def create_indices(self, thread):
        logger.info(f"목차 생성 시작 | Thread ID: {thread.id}")
        text = await read_text_file(f"capstone_storage/{self.note_id}/original_{self.note_id}.txt")
        instruction = load_prompt(
            explanation_level=self.explanation_level,
            stage="create_indices",
            full_original_text=text
        )
        handler_dto = EventHandlerFactoryDTO(
            process_stage = GenerationProcess.OUTLINE_GENERATION,
            note_id=self.note_id,
            kafka_producer=self.producer
        )
        await run_stream_with_backoff(self.index_assistant.id, thread.id, event_handler_factory=event_handler_factory, instructions=instruction, event_handler_factory_dto=handler_dto)

    async def create_explanations(self, dir_path):
        logger.info("설명문 생성 시작 - 분할된 텍스트 파일들 병렬 처리")

        chunk_files = os.listdir(dir_path)
        self.total_chunks = len(chunk_files)
        """
        tasks = [
            self.create_chunk_explanation(os.path.join(dir_path, chunk_file_name), idx, video_id=self.video_id)
            for idx, chunk_file_name in enumerate(chunk_files, start=1)
        ]
        """
        tasks = [
            asyncio.create_task(
                self.create_chunk_explanation(os.path.join(dir_path, chunk_file_name), idx)
            )
            for idx, chunk_file_name in enumerate(chunk_files, start=1)
        ]

        results = await asyncio.gather(*tasks)
        logger.info("모든 설명문 생성 작업이 완료되었습니다.")
        complete_message = LLMResultMessage(
            noteId=self.note_id,
            startTime=0,
            commentaryOrder=-1,
            content="EXPLANATION END"
        )
        await self.producer.send_message(topic=LLM_COMMENTARY_EVENTS, message = complete_message.model_dump_json())
        return results
    # 병렬적으로 수행하기 위해 create explanation으로 asyncio로 thread를 개수만큼 바로 생성하고 요구를  보내도록 수정하기
    async def create_chunk_explanation(self, chunk_file_path, chunk_index):
        thread = await create_thread()

        logger.info(f"설명문 생성 시작 | 파일: {chunk_file_path} | Thread ID: {thread.id} | 청크: {chunk_index + 1}/{self.total_chunks}")
        chunk_text = await read_text_file(
            f"capstone_storage/{self.note_id}/transcription_chunks/{self.note_id}_{chunk_index}.txt")
        instruction = load_prompt(
            explanation_level = self.explanation_level,
            stage="create_explanation",
            chunk_original_text=chunk_text
        )
        handler_dto = EventHandlerFactoryDTO(
            process_stage=GenerationProcess.EXPLANATION_GENERATION,
            note_id=self.note_id,
            chunk_index=chunk_index
        )
        await run_stream_with_backoff(self.explanation_assistant.id, thread.id, event_handler_factory=event_handler_factory, instructions=instruction, event_handler_factory_dto=handler_dto)

        logger.info(f"설명문 생성 완료 | Thread ID: {thread.id} | 청크: {chunk_index}/{self.total_chunks}")
        return thread

    async def create_feedbacks_for_explanations(self, results, text_list):
        logger.info("피드백 텍스트 병렬 생성 시작")
        tasks = [self.create_chunk_feedback(thread, chunk_index=idx, text_list=text_list) for idx, thread in
                 enumerate(results, start=1)]
        await asyncio.gather(*tasks)
        logger.info("모든 피드백 생성 작업이 완료되었습니다.")
        complete_message = LLMResultMessage(
            noteId=self.note_id,
            startTime=0,
            commentaryOrder=-1,
            content="FEEDBACK END"
        )
        await self.producer.send_message(topic=LLM_COMMENTARY_EVENTS, message=complete_message.model_dump_json())

    async def create_chunk_feedback(self, thread, chunk_index, text_list):
        logger.info(f"피드백 생성 시작 | Thread ID: {thread.id} | 청크: {chunk_index}/{self.total_chunks}")
        instruction = load_prompt(
            explanation_level = self.explanation_level,
            stage = "create_feedback",
            text_list = text_list
        )
        handler_dto = EventHandlerFactoryDTO(
            process_stage=GenerationProcess.FEEDBACK_GENERATION,
            note_id=self.note_id,
            chunk_index=chunk_index,
            thread_id=thread.id
        )
        logger.info("instruction 생성완료")
        await run_stream_with_backoff(self.explanation_assistant.id, thread.id, event_handler_factory=event_handler_factory, instructions=instruction, event_handler_factory_dto=handler_dto)

        logger.info(f"피드백 생성 완료 | Thread ID: {thread.id} | 청크: {chunk_index}/{self.total_chunks}")

    async def create_enhanced_explanations(self, results, outline_start_times):
        logger.info("피드백 반영 설명문 텍스트 병렬 생성 시작")
        tasks = [self.create_enhanced_chunk_explanation(thread, idx, outline_start_times[idx-1]) for idx, thread in
                 enumerate(results,start=1)]

        await asyncio.gather(*tasks)
        logger.info("모든 피드백 반영 설명문 생성 작업이 완료되었습니다.")
        complete_message = LLMResultMessage(
            noteId=self.note_id,
            startTime=0,
            commentaryOrder=-1,
            content="END"
        )
        await self.producer.send_message(topic=LLM_COMMENTARY_EVENTS, message=complete_message.model_dump_json())
        logger.info("완료 메시지를 전송했습니다.")


    async def create_enhanced_chunk_explanation(self, thread, chunk_index, outline_start_time):
        logger.info(f"피드백 반영 설명문 생성 시작 | Thread ID: {thread.id} | 청크: {chunk_index}/{self.total_chunks}")
        instruction = load_prompt(
            explanation_level = self.explanation_level,
            stage="create_enhanced_explanation",
        )
        handler_dto = EventHandlerFactoryDTO(
            process_stage=GenerationProcess.ENHANCED_EXPLANATION_GENERATION,
            note_id=self.note_id,
            chunk_index=chunk_index,
            outline_start_time=outline_start_time,
            thread_id=thread.id,
            kafka_producer=self.producer
        )
        await run_stream_with_backoff(self.explanation_assistant.id, thread.id, event_handler_factory=event_handler_factory, instructions=instruction, event_handler_factory_dto=handler_dto)


    async def process_transcript(self, message: LlmRequestMessage):
        index_thread = await create_thread()
        logger.info(f"thread : {index_thread.id} created")

        # 목차 생성
        await self.create_indices(index_thread)
        # 목차로 텍스트 파일 분할
        index_path = f"capstone_storage/{message.noteId}/index/index.txt"
        logger.info(index_path)
        index_file = await read_text_file(index_path)
        index_json = json.loads(re.sub(r"```json|```", "", index_file))
        outline_start_time_list = [outline['startTime'] for outline in index_json ]
        logger.info(outline_start_time_list)
        transcription_chunks_path = TextUtils.split_by_toc(note_id=message.noteId, toc_filepath=index_path)
        # 목차별 설명문 생성
        chunk_resource_list = await self.create_explanations(transcription_chunks_path)
        time.sleep(3)
        # 목차별 설명문 피드백 수행
        text_list_path = "openai_service/cs_term.txt"
        text_list = await read_text_file(text_list_path)
        await self.create_feedbacks_for_explanations(chunk_resource_list, text_list)
        time.sleep(3)
        # 목차별 피드백 반영 설명문 생성
        await self.create_enhanced_explanations(chunk_resource_list, outline_start_time_list)
