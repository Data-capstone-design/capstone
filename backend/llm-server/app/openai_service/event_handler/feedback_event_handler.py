import os

import asyncio
from typing import override

from openai import AssistantEventHandler, AsyncAssistantEventHandler
from loguru import logger
from openai.types.beta.threads import Text

from app.openai_service.assistant_api_utils import add_file_to_vector_store


class FeedbackEventHandler(AsyncAssistantEventHandler):
    def __init__(self, thread_id, chunk_index, video_id):
        super().__init__()
        self.video_id = video_id
        self.thread_id = thread_id  # 피드백이 속하는 thread ID
        self.chunk_index = chunk_index  # 목차 인덱스, 피드백 대상 설명문 구분
        self.feedback_data = ""  # 생성된 피드백 데이터를 저장할 변수

    @override
    async def on_text_created(self, text: Text) -> None:
        logger.info(f"feedback generation started for videoid: {self.video_id}- chunk_index: {self.chunk_index}")

    @override
    async def on_text_delta(self, delta, snapshot):
        # 스트리밍된 피드백 데이터를 누적
        self.feedback_data += delta.value

    @override
    async def on_message_done(self, message):
        # 피드백 생성 완료 시 호출
        logger.info(f"{self.thread_id} (목차 {self.chunk_index})에 대한 피드백 생성 완료")

        # 디렉터리가 존재하지 않을 경우 생성
        feedback_dir = f"capstone_storage/{self.video_id}/feedback"
        os.makedirs(feedback_dir, exist_ok=True)

        # 생성된 피드백을 파일로 저장
        feedback_file_path = f"{feedback_dir}/feedback_{self.video_id}_{self.chunk_index}.txt"
        try:
            with open(feedback_file_path, "w") as f:
                f.write(f"목차 {self.chunk_index}에 대한 피드백:\n" + self.feedback_data)
                logger.info(f"피드백이 {feedback_file_path}에 저장되었습니다.")
        except Exception as e:
                logger.error(f"피드백을 저장하는 데 실패했습니다: {e}")