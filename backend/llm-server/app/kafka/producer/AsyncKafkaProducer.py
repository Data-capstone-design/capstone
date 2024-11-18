import json

from aiokafka import AIOKafkaProducer
import asyncio
import logging


class AsyncKafkaProducer:
    def __init__(self, bootstrap_servers, loop=None):
        self.bootstrap_servers = bootstrap_servers
        self.loop = loop or asyncio.get_event_loop()
        self.producer = AIOKafkaProducer(loop=self.loop, bootstrap_servers=self.bootstrap_servers)

    async def start(self):
        """
        Kafka Producer 시작 메서드
        """
        await self.producer.start()
        logging.info("Kafka producer started.")

    async def stop(self):
        """
        Kafka Producer 종료 메서드
        """
        await self.producer.stop()
        logging.info("Kafka producer stopped.")

    async def send_message(self, topic, message, partition=None):
        """
        Kafka 메시지를 전송하는 통일된 메서드
        Args:
            topic (str): Kafka 토픽 이름
            message (Union[str, dict]): 전송할 메시지 내용
            partition (int, optional): 특정 파티션에 메시지를 전송하려면 지정
        """
        try:
            # 메시지가 dict인 경우 JSON 직렬화
            if isinstance(message, dict):
                message = json.dumps(message)
            elif not isinstance(message, str):
                raise ValueError("Message must be a string or a dictionary.")

            # 메시지를 UTF-8로 인코딩하여 전송
            await self.producer.send_and_wait(topic, message.encode("utf-8"), partition=partition)
            logging.info(f"Sent message to topic '{topic}' with message: {message}")
        except Exception as e:
            logging.error(f"Failed to send message to topic '{topic}': {e}")