import asyncio
from contextlib import asynccontextmanager

from app.kafka.consumers.kafka_consumer import consume_request
from app.orchestrator.message_processor import MessageProcessor
from dotenv import load_dotenv
from fastapi import FastAPI
from loguru import logger

from app.kafka.kafka_config import *

# 환경 변수 로드
load_dotenv()

@asynccontextmanager
async def lifespan(app: FastAPI):
    logger.info("서버를 시작합니다.")

    try:
        consumer, producer = await initialize_kafka()
        logger.info("Kafka consumers and producer initialized successfully.")
    except Exception as e:
        logger.error(f"Failed to initialize Kafka: {e}")
        raise RuntimeError("Kafka initialization failed") from e

    message_processor = MessageProcessor(producer)

    request_consumer_task = asyncio.create_task(consume_request(consumer,message_processor))

    try:
        yield
    finally:
        # 애플리케이션 종료 시 Kafka 소비자 중지
        logger.info("Shutting down FastAPI application. Cancelling Kafka consumer task.")
        request_consumer_task.cancel()
        await producer.stop()
        await consumer.stop()
        try:
            await request_consumer_task
        except asyncio.CancelledError:
            logger.info("Kafka consumer task cancelled successfully.")