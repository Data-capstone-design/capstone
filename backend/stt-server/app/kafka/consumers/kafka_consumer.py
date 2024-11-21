from loguru import logger

from app.domain.kafka_message.stt_request_message import SttRequestMessage


# async def consume():
#     # Kafka consumer 초기화
#     consumer = AIOKafkaConsumer(
#         VIDEO_REQUEST_TOPIC,
#         bootstrap_servers=KAFKA_BOOTSTRAP_SERVERS,
#         auto_offset_reset='earliest',
#         enable_auto_commit=True,
#         group_id='transcription-group',
#         value_deserializer=lambda x: json.loads(x.decode('utf-8'))
#     )
#
#     producer = AsyncProducer(STT_RESULT_TOPIC)
#
#     # Orchestrator 인스턴스 초기화 (필요한 종속성 포함)
#     audio_downloader = AudioDownloader()
#     transcription_service = TranscriptionService(
#         model_path="path/to/whisper_model",  # 실제 모델 경로로 수정
#         language="en",
#         use_gpu=False,
#         max_concurrent_tasks=2,
#         quantize=False
#     )
#
#     orchestrator = Orchestrator(
#         audio_downloader=audio_downloader,
#         transcription_service=transcription_service,
#         kafka_producer=producer
#     )
#
#     await consumer.start()
#     await producer.start()
#
#     try:
#         async for message in consumer:
#             # 메시지 수신 후 Orchestrator에서 처리
#             logger.info(f"Received message: {message.value}")
#             await orchestrator.process_message(message=message.value)
#
#     except Exception as e:
#         logger.error(f"Processing failed with error: {e}")
#
#     finally:
#         await consumer.stop()
#         logger.info("Kafka consumer stopped.")


async def consume_request(consumer, processor):
    logger.info("STT요청을 소비하기 시작합니다.")
    try:
        async for msg in consumer:
            # STTChunkResultMessage 클래스를 사용하여 메시지 파싱
            logger.info(f"request message consume: {msg.value}")

            request = SttRequestMessage.model_validate(msg.value)
            await processor.process_message(request)


    except Exception as e:
        logger.error(f"STT 결과물 소비 중 오류 발생: {e}")
    finally:
        logger.info("STT 결과물 소비 종료.")