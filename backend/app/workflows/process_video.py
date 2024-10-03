from app.services.youtube_downloader import download_video
from app.services.speech_extractor import extract_speech
# 비디오에서 텍스트 처리 과정을 담당하는 메서드
def process_video(youtube_url: str) -> dict:
    video_id = download_video(youtube_url)
    extract_speech(video_id)
  