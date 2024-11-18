from contextlib import asynccontextmanager

from app.kafka.consumer.kafka_consumer import *
from app.kafka.kafka_config import *
from app.openai_service.assistant_manager import initialize_assistants


@asynccontextmanager
async def lifespan(app):
    logger.info("Starting FastAPI application with Kafka consumers and producer.")

    # Kafka 컨슈머 및 프로듀서 초기화
    try:
        stt_consumer, producer = await initialize_kafka()
        logger.info("Kafka consumers and producer initialized successfully.")
    except Exception as e:
        logger.error(f"Failed to initialize Kafka: {e}")
        raise RuntimeError("Kafka initialization failed") from e

    # Assistants 초기화
    try:
        assistants = await initialize_assistants()
        logger.info("Assistants initialized successfully.")
    except Exception as e:
        logger.error(f"Failed to initialize assistants: {e}")
        raise RuntimeError("Assistant initialization failed") from e

    # Kafka 메시지 소비 Task 시작
    stt_consumer_task = asyncio.create_task(
        consume_stt_result(consumer=stt_consumer, assistants=assistants, producer=producer)
    )

    try:
        yield  # FastAPI 앱 실행
    except Exception as e:
        logger.error(f"Unexpected error during lifespan: {e}")
        raise
    finally:
        logger.info("Shutting down Kafka consumers and producer.")

        # Kafka Task 종료
        stt_consumer_task.cancel()
        try:
            await stt_consumer_task
        except asyncio.CancelledError:
            logger.info("Kafka consumer task successfully cancelled.")
        except Exception as e:
            logger.error(f"Error while cancelling Kafka consumer task: {e}")

        # Kafka 컨슈머 및 프로듀서 정지
        await asyncio.gather(stt_consumer.stop(), producer.stop(), return_exceptions=True)
        logger.info("Kafka consumers and producer shut down successfully.")