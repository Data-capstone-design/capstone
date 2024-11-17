
import asyncio
import aiofiles
import re
from loguru import logger

from app.domain.kafka_message.initialize_llm_request_message import InitiateRequestMessage

from app.domain.kafka_message.stt_request_message import SttRequestMessage

from app.domain.kafka_message.stt_message import LlmRequestMessage
from app.kafka.kafka_config import STT_RESULT_TOPIC, LLM_INITIALIZATION_TOPIC, LLm_REQUEST_EVENTS
from app.transcription_service.youtube_caption_downloader import *
from app.domain.kafka_message.chunk_transcription_result import TranscriptionResultMessage

from app.transcription_service.youtube_caption_downloader import download_transcript


class MessageProcessor:
    def __init__(self, producer):
        """
        MessageProcessor 클래스 초기화

        :param producer: Kafka 프로듀서 인스턴스
        """
        self.producer = producer

    def generate_youtube_url(self, video_id: str) -> str:
        """
        Video ID를 사용하여 YouTube URL을 생성하는 메서드

        :param video_id: YouTube Video ID
        :return: 생성된 YouTube URL
        :raises ValueError: Video ID가 유효하지 않을 경우
        """
        if not video_id or not video_id.strip():
            logger.error("Invalid Video ID: {}", video_id)
            raise ValueError(f"Invalid Video ID: {video_id}")

        # YouTube URL 생성
        youtube_url = f"https://www.youtube.com/watch?v={video_id}"
        return youtube_url

    async def process_message(self, message: SttRequestMessage):
            video_id = message.videoId
            explanation_level = message.explanationLevel

            logger.info("Processing message: {}", message)

            if not video_id:
                logger.error("No 'youtube_url' found in message: {}", message)
                return

            try:
                # YouTube URL에서 Video ID 추출
                youtube_url = self.generate_youtube_url(video_id)
                logger.info("generated youtube url from Video ID: {}", video_id)

                # 자막 다운로드
                caption_file_path = await download_transcript(video_id, language_code='en')
                if not caption_file_path:
                    logger.error("Failed to download transcript for Video ID: {}", video_id)
                    return

                # 자막 파일 읽기 (비동기적으로 읽기)
                async with aiofiles.open(caption_file_path, 'r', encoding='utf-8') as f:
                    caption_text = await f.read()

                if not caption_text:
                    logger.error("Downloaded transcript is empty for Video ID: {}", video_id)
                    return

                llm_request_message = LlmRequestMessage(
                    videoId=video_id,
                    explanationLevel = explanation_level,
                    transcriptionText = caption_text,
                )

                await self.producer.send_message(llm_request_message, topic=LLm_REQUEST_EVENTS)
            except ValueError as ve:
                logger.error("ValueError: {}", ve)
            except Exception as e:
                logger.exception("An unexpected error occurred while processing the message: {}", e)

