import asyncio
from app.openai_service.event_handler.enhanced_event_handler import EnhancedExplanationEventHandler

from app.openai_service.event_handler.explanation_event_handler import ExplanationEventHandler
from app.openai_service.event_handler.feedback_event_handler import FeedbackEventHandler
from app.openai_service.event_handler.index_event_handler import IndexEventHandler
from app.openai_service.assistant_api_utils import *
from app.message_processor.instructions.instructions import *


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
    def __init__(self, request_id, assistant, explanation_level, producer):
        self.request_id = request_id
        self.total_chunks = None
        self.assistant = assistant
        self.explanation_level = explanation_level
        self.producer = producer


    async def initiate(self, file_path: str):

        vector_store = await create_vector_store()
        logger.info(f"Vector store 생성: {vector_store.id}")
        thread = await create_thread()
        logger.info(f"Thread 생성: {thread.id}")
        return thread, vector_store

    async def create_indices(self, thread):
        logger.info(f"목차 생성 시작 | Thread ID: {thread.id}")
        text = await read_text_file("capstone_storage/test_request_123/original_test_request_123.txt")
        instruction = load_prompt(
            explanation_level=self.explanation_level,
            stage="create_indices",
            full_original_text=text
        )
        run = await run_stream(self.assistant.id, thread.id, event_handler=IndexEventHandler(self.request_id, producer=self.producer), instructions=instruction)

    async def create_explanations(self, dir_path):
        logger.info("설명문 생성 시작 - 분할된 텍스트 파일들 병렬 처리")

        chunk_files = os.listdir(dir_path)
        self.total_chunks = len(chunk_files)
        """
        tasks = [
            self.create_chunk_explanation(os.path.join(dir_path, chunk_file_name), idx, request_id=self.request_id)
            for idx, chunk_file_name in enumerate(chunk_files, start=1)
        ]
        """
        tasks = [
            asyncio.create_task(
                self.create_chunk_explanation(os.path.join(dir_path, chunk_file_name), idx, request_id=self.request_id)
            )
            for idx, chunk_file_name in enumerate(chunk_files, start=1)
        ]

        results = await asyncio.gather(*tasks)
        logger.info("모든 설명문 생성 작업이 완료되었습니다.")
        return results
    # 병렬적으로 수행하기 위해 create explanation으로 asyncio로 thread를 개수만큼 바로 생성하고 요구를  보내도록 수정하기
    async def create_chunk_explanation(self, chunk_file_path, chunk_index, request_id):
        #thread, vector_store = await self.initiate(chunk_file_path)
        thread = await create_thread()
        event_handler = ExplanationEventHandler(
             request_id=self.request_id, chunk_index=chunk_index
        )

        logger.info(f"설명문 생성 시작 | 파일: {chunk_file_path} | Thread ID: {thread.id} | 청크: {chunk_index + 1}/{self.total_chunks}")
        chunk_text = await read_text_file(
            f"capstone_storage/{request_id}/transcription_chunks/{request_id}_{chunk_index}.txt")
        instruction = load_prompt(
            explanation_level = self.explanation_level,
            stage="create_explanation",
            chunk_original_text=chunk_text
        )

        await run_stream(self.assistant.id, thread.id, event_handler=event_handler, instructions=instruction
        )

        logger.info(f"설명문 생성 완료 | Thread ID: {thread.id} | 청크: {chunk_index}/{self.total_chunks}")
        return thread

    async def create_feedbacks_for_explanations(self, results):
        logger.info("피드백 텍스트 병렬 생성 시작")
        tasks = [self.create_chunk_feedback(thread, chunk_index=idx, request_id=self.request_id) for idx, thread in
                 enumerate(results, start=1)]
        await asyncio.gather(*tasks)
        logger.info("모든 피드백 생성 작업이 완료되었습니다.")

    async def create_chunk_feedback(self, thread, chunk_index, request_id):
        feedback_event_handler = FeedbackEventHandler(
            thread_id=thread.id,  request_id=self.request_id, chunk_index=chunk_index
        )

        logger.info(f"피드백 생성 시작 | Thread ID: {thread.id} | 청크: {chunk_index}/{self.total_chunks}")
        chunk_explanation_text = await read_text_file(
            f"capstone_storage/{request_id}/explanation/explanation_{request_id}_{chunk_index}.txt")
        chunk_text = await read_text_file(
            f"capstone_storage/{request_id}/transcription_chunks/{request_id}_{chunk_index}.txt")
        instruction = load_prompt(
            explanation_level = self.explanation_level,
            stage = "create_feedback",
            chunk_original_text=chunk_text,
            chunk_explanation=chunk_explanation_text
        )

        logger.info("instruction 생성완료")
        await run_stream(self.assistant.id, thread.id, event_handler=feedback_event_handler, instructions=instruction)

        logger.info(f"피드백 생성 완료 | Thread ID: {thread.id} | 청크: {chunk_index}/{self.total_chunks}")

    async def create_enhanced_explanations(self, results):
        logger.info("피드백 반영 설명문 텍스트 병렬 생성 시작")
        tasks = [self.create_enhanced_chunk_explanation(thread, idx) for idx, thread in
                 enumerate(results,start=1)]

        await asyncio.gather(*tasks)
        logger.info("모든 피드백 반영 설명문 생성 작업이 완료되었습니다.")

    async def create_enhanced_chunk_explanation(self, thread, chunk_index):
        enhanced_explanation_event_handler = EnhancedExplanationEventHandler(
            thread_id=thread.id, request_id=self.request_id, chunk_index=chunk_index, kafka_producer=self.producer
        )

        logger.info(f"피드백 반영 설명문 생성 시작 | Thread ID: {thread.id} | 청크: {chunk_index}/{self.total_chunks}")
        chunk_explanation_text = await read_text_file(
            f"capstone_storage/{self.request_id}/explanation/explanation_{self.request_id}_{chunk_index}.txt")
        chunk_explanation_feedback_text = await read_text_file(
            f"capstone_storage/{self.request_id}/feedback/feedback_{self.request_id}_{chunk_index}.txt")
        instruction = load_prompt(
            explanation_level = self.explanation_level,
            stage="create_enhanced_explanation",
            chunk_explanation=chunk_explanation_text,
            chunk_feedback=chunk_explanation_feedback_text
        )

        await run_stream(self.assistant.id, thread.id, event_handler=enhanced_explanation_event_handler, instructions=instruction)

        logger.info(f"피드백 반영 설명문 생성 완료 | Thread ID: {thread.id} | 청크: {chunk_index}/{self.total_chunks}")
