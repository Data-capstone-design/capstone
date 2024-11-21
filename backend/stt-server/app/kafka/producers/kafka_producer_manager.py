from aiokafka import AIOKafkaProducer
from loguru import logger
import json
from pydantic import BaseModel
from loguru import logger


class AsyncProducer:
    def __init__(self, bootstrap_servers):
        """
        AsyncSTTResultProducer 초기화 시 Kafka 설정을 사용해 프로듀서를 구성.
        - 최신 aiokafka 파라미터에 맞춰 설정.
        """
        self.producer = AIOKafkaProducer(
            bootstrap_servers=bootstrap_servers,
            value_serializer=lambda v: json.dumps(v).encode('utf-8'),
        )

    async def start(self):
        """
        Kafka Producer 시작 메서드
        """
        await self.producer.start()
        logger.info("Kafka producer started.")

    async def stop(self):
        """
        Kafka Producer 종료 메서드
        """
        await self.producer.stop()
        logger.info("Kafka producer stopped.")

    async def send_message(self, message: BaseModel, topic):
        """
        Kafka로 비동기 메시지를 전송.
        """
        try:
            # 메시지 직렬화 전 로깅
            message_data = message.model_dump()
            logger.info(f"Serialized JSON message data: {message_data}")

            # 메시지 전송
            await self.producer.send_and_wait(topic, value=message_data)
            logger.info(f"Successfully sent message to topic {topic}: {message}")
        except Exception as e:
            logger.error(f"Failed to send message to topic {topic}: {e}")
            # 필요시 재시도 로직 추가







