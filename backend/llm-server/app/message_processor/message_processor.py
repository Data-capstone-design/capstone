import asyncio

from app.domain.kafka_message.llm_request_message import LlmRequestMessage
from app.openai_service.event_handler.enhanced_event_handler import EnhancedExplanationEventHandler

from app.openai_service.event_handler.explanation_event_handler import ExplanationEventHandler
from app.openai_service.event_handler.feedback_event_handler import FeedbackEventHandler
from app.openai_service.event_handler.index_event_handler import IndexEventHandler
from app.openai_service.assistant_api_utils import *
from app.message_processor.instructions.instructions import *
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
        self.total_chunks = None
        self.index_assistant = assistants.index_assistant
        self.explanation_assistant = assistants.explanation_assistant
        self.explanation_level = request_message.explanationLevel
        self.producer = producer

    async def create_indices(self, thread):
        logger.info(f"목차 생성 시작 | Thread ID: {thread.id}")
        text = await read_text_file("capstone_storage/test_request_123/original_test_request_123.txt")
        instruction = load_prompt(
            explanation_level=self.explanation_level,
            stage="create_indices",
            full_original_text=text
        )
        run = await run_stream(self.index_assistant.id, thread.id, event_handler=IndexEventHandler(self.video_id, producer=self.producer), instructions=instruction)

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
                self.create_chunk_explanation(os.path.join(dir_path, chunk_file_name), idx, video_id=self.video_id)
            )
            for idx, chunk_file_name in enumerate(chunk_files, start=1)
        ]

        results = await asyncio.gather(*tasks)
        logger.info("모든 설명문 생성 작업이 완료되었습니다.")
        return results
    # 병렬적으로 수행하기 위해 create explanation으로 asyncio로 thread를 개수만큼 바로 생성하고 요구를  보내도록 수정하기
    async def create_chunk_explanation(self, chunk_file_path, chunk_index, video_id):
        thread = await create_thread()
        event_handler = ExplanationEventHandler(
             video_id=self.video_id, chunk_index=chunk_index
        )

        logger.info(f"설명문 생성 시작 | 파일: {chunk_file_path} | Thread ID: {thread.id} | 청크: {chunk_index + 1}/{self.total_chunks}")
        chunk_text = await read_text_file(
            f"capstone_storage/{video_id}/transcription_chunks/{video_id}_{chunk_index}.txt")
        instruction = load_prompt(
            explanation_level = self.explanation_level,
            stage="create_explanation",
            chunk_original_text=chunk_text
        )

        await run_stream(self.explanation_assistant.id, thread.id, event_handler=event_handler, instructions=instruction)

        logger.info(f"설명문 생성 완료 | Thread ID: {thread.id} | 청크: {chunk_index}/{self.total_chunks}")
        return thread

    async def create_feedbacks_for_explanations(self, results):
        logger.info("피드백 텍스트 병렬 생성 시작")
        tasks = [self.create_chunk_feedback(thread, chunk_index=idx, video_id=self.video_id) for idx, thread in
                 enumerate(results, start=1)]
        await asyncio.gather(*tasks)
        logger.info("모든 피드백 생성 작업이 완료되었습니다.")

    async def create_chunk_feedback(self, thread, chunk_index, video_id):
        feedback_event_handler = FeedbackEventHandler(
            thread_id=thread.id,  video_id=self.video_id, chunk_index=chunk_index
        )

        logger.info(f"피드백 생성 시작 | Thread ID: {thread.id} | 청크: {chunk_index}/{self.total_chunks}")
        chunk_explanation_text = await read_text_file(
            f"capstone_storage/{video_id}/explanation/explanation_{video_id}_{chunk_index}.txt")
        chunk_text = await read_text_file(
            f"capstone_storage/{video_id}/transcription_chunks/{video_id}_{chunk_index}.txt")
        instruction = load_prompt(
            explanation_level = self.explanation_level,
            stage = "create_feedback",
            chunk_original_text=chunk_text,
            chunk_explanation=chunk_explanation_text
        )

        logger.info("instruction 생성완료")
        await run_stream(self.explanation_assistant.id, thread.id, event_handler=feedback_event_handler, instructions=instruction)

        logger.info(f"피드백 생성 완료 | Thread ID: {thread.id} | 청크: {chunk_index}/{self.total_chunks}")

    async def create_enhanced_explanations(self, results):
        logger.info("피드백 반영 설명문 텍스트 병렬 생성 시작")
        tasks = [self.create_enhanced_chunk_explanation(thread, idx) for idx, thread in
                 enumerate(results,start=1)]

        await asyncio.gather(*tasks)
        logger.info("모든 피드백 반영 설명문 생성 작업이 완료되었습니다.")

    async def create_enhanced_chunk_explanation(self, thread, chunk_index):
        enhanced_explanation_event_handler = EnhancedExplanationEventHandler(
            thread_id=thread.id, video_id=self.video_id, chunk_index=chunk_index, kafka_producer=self.producer
        )

        logger.info(f"피드백 반영 설명문 생성 시작 | Thread ID: {thread.id} | 청크: {chunk_index}/{self.total_chunks}")
        chunk_explanation_text = await read_text_file(
            f"capstone_storage/{self.video_id}/explanation/explanation_{self.video_id}_{chunk_index}.txt")
        chunk_explanation_feedback_text = await read_text_file(
            f"capstone_storage/{self.video_id}/feedback/feedback_{self.video_id}_{chunk_index}.txt")
        instruction = load_prompt(
            explanation_level = self.explanation_level,
            stage="create_enhanced_explanation",
            chunk_explanation=chunk_explanation_text,
            chunk_feedback=chunk_explanation_feedback_text
        )

        await run_stream(self.explanation_assistant.id, thread.id, event_handler=enhanced_explanation_event_handler, instructions=instruction)


    async def process_transcript(self, message: LlmRequestMessage):
        index_thread = await create_thread()
        logger.info(f"thread : {index_thread.id} created")

        # 목차 생성
        await self.create_indices(index_thread)
        # 목차로 텍스트 파일 분할
        index_path = f"capstone_storage/{message.videoId}/index/index.txt"
        logger.info(index_path)
        transcription_chunks_path = TextUtils.split_by_toc(video_id=message.videoId, toc_filepath=index_path)
        # 목차별 설명문 생성
        chunk_resource_list = await self.create_explanations(transcription_chunks_path)
        # 목차별 설명문 피드백 수행
        await self.create_feedbacks_for_explanations(chunk_resource_list)
        # 목차별 피드백 반영 설명문 생성
        await self.create_enhanced_explanations(chunk_resource_list)
