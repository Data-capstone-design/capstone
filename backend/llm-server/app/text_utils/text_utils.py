import json
import sys

from loguru import logger
import re
import os

class TextUtils:
    """
    텍스트 병합과 목차에 따른 파일 분할 기능을 제공하는 클래스.
    """

    @staticmethod
    def initialize_file(note_id):
        # 디렉토리 경로와 파일 경로 설정
        dir_path = f"capstone_storage/{note_id}"
        file_path = f"{dir_path}/original_{note_id}.txt"

        # 디렉토리가 없으면 생성
        os.makedirs(dir_path, exist_ok=True)

        # 파일 생성
        with open(file_path, "w") as f:
            pass  # 빈 파일 생성

        logger.info(f"Initialized text file at {file_path} for note_id '{note_id}'.")

    @staticmethod
    def save_text(text: str, note_id: str):
        try:
            # 디렉토리 경로와 파일 경로 설정
            dir_path = f"capstone_storage/{note_id}"
            file_path = f"{dir_path}/original_{note_id}.txt"

            # 디렉토리가 없으면 생성
            if not os.path.exists(dir_path):
                os.makedirs(dir_path, exist_ok=True)
                logger.info(f"Directory created: {dir_path}")

            # 파일이 없으면 생성
            if not os.path.exists(file_path):
                with open(file_path, "w") as f:
                    pass  # 빈 파일 생성
                logger.info(f"File created: {file_path}")

            # 텍스트 저장
            with open(file_path, "w", encoding="utf-8") as f:
                f.write(text)
                logger.info(f"Text saved to: {file_path}")

            return file_path

        except OSError as e:
            logger.exception(f"File operation failed for note_id '{note_id}': {e}")
            raise RuntimeError(f"Failed to ensure or save file for note_id '{note_id}'") from e

        except Exception as e:
            logger.exception(f"Unexpected error while handling file for note_id '{note_id}': {e}")
            raise

    @staticmethod
    def split_by_toc(note_id, toc_filepath):
        logger.info(toc_filepath)
        output_folder = f"capstone_storage/{note_id}/transcription_chunks"
        os.makedirs(output_folder, exist_ok=True)
        logger.info(f"Output folder '{output_folder}' created or already exists.")

        # TOC 파일 확인 및 로드
        if not os.path.exists(toc_filepath) or os.path.getsize(toc_filepath) == 0:
            logger.error(f"TOC file '{toc_filepath}' does not exist or is empty.")
            return None

        # TOC 파일 로드 및 불필요한 문자 제거 후 파싱
        try:
            with open(toc_filepath, 'r', encoding='utf-8') as toc_file:
                toc_text = toc_file.read()
                toc_text = re.sub(r"```json|```", "", toc_text)
                if not toc_text:
                    logger.error(f"TOC file '{toc_filepath}' has no valid JSON content after cleanup.")
                    return None
                toc_data = json.loads(toc_text)
                logger.info(f"TOC JSON file '{toc_filepath}' loaded successfully.")
        except json.JSONDecodeError as e:
            logger.error(f"Error decoding TOC JSON file '{toc_filepath}': {e}")
            return None

        # 분할할 원본 텍스트 데이터 로드 (이전 방식 유지)
        text_data_path = f"capstone_storage/{note_id}/original_{note_id}.txt"
        json_objects = []

        with open(text_data_path, 'r', encoding='utf-8') as file:
            content = file.read()

        pattern = r'\{[^{}]*\}'

        matches = re.findall(pattern, content)

        # 각 매칭된 JSON 객체를 파싱
        for match in matches:
            try:
                json_object = json.loads(match)
                json_objects.append(json_object)
            except json.JSONDecodeError as e:
                print(f"JSON decoding error in match: {match}, error: {e}")

        # TOC 항목에 따른 텍스트 분할 및 저장
        for i, toc_item in enumerate(toc_data):
            start_time = float(toc_item['startTime'])
            end_time = float(toc_data[i + 1]['startTime']) if i + 1 < len(toc_data) else sys.float_info.max
            title = toc_item['title']
            summary = toc_item.get('summary', "")
            index_num = i + 1

            segment_content = []

            for text_item in json_objects:
                text_start = float(text_item.get('start'))
                if start_time<= text_start < end_time:
                    logger.debug(f"TOC range: {start_time} to {end_time}, checking text start: {text_start}")
                    segment_content.append(text_item['text'])

            if segment_content:
                output_file = os.path.join(output_folder, f"{note_id}_{index_num}.txt")
                try:
                    with open(output_file, "w", encoding="utf-8") as outfile:
                        outfile.write(
                            f"Title: {title}\nStart: {start_time}\nSummary: {summary}\nContent:\n" + "\n".join(
                                segment_content))
                    logger.info(f"Saved segment '{title}' in file {output_file}")
                except IOError as e:
                    logger.error(f"Failed to write file {output_file}: {e}")
            else:
                logger.warning(f"No content found for TOC item '{title}' with start time {start_time}")

        logger.info("Text split by TOC and saved to separate files.")
        return output_folder