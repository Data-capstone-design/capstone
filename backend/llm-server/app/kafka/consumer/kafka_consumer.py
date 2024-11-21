import traceback

from app.message_processor.message_processor import *
from app.text_utils.text_utils import TextUtils


async def consume_stt_result(consumer, assistants, producer):
    async def process_message(message: LlmRequestMessage):
        try:
            # STTChunkResultMessage로 메시지 파싱
            logger.info(f"chunk message consume: {message.value}")
            stt_result = LlmRequestMessage.model_validate_json(message.value)

            message_processor = MessageProcessor(
                request_message=stt_result,
                assistants = assistants,
                producer=producer
            )

            #transcript 저장
            TextUtils.save_text(stt_result.transcriptionText, stt_result.noteId)

            await message_processor.process_transcript(stt_result)
        except Exception as e:
            error_details = traceback.format_exc()
            logger.error(f"STT 결과물 소비 중 오류 발생: {e}\n세부 정보:\n{error_details}")
        finally:
            logger.info("STT 결과물 소비 종료.")


    async for msg in consumer:
        asyncio.create_task(process_message(msg))