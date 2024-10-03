import whisper
import ffmpeg
from app.utils.video_util import VideoUtil
import time 

def extract_audio_from_video(video_path: str, audio_path: str):
    ffmpeg.input(video_path).output(audio_path).run()
    print(f"오디오가 {audio_path}에 저장되었습니다.")
    return audio_path

def extract_speech_to_text_with_time(audio_path: str):
    # Whisper 모델 로드 (기본 모델 사용)
    model = whisper.load_model("base")

    # 오디오에서 텍스트 및 타임스탬프 추출
    result = model.transcribe(audio_path)
    segments = result['segments']  # 발화 시간 정보와 텍스트가 포함됨

    # 결과 출력
    for segment in segments:
        start_time = segment['start']
        end_time = segment['end']
        text = segment['text']
        print(f"{start_time:.2f} - {end_time:.2f}: {text}")

    return segments

def extract_speech(video_id: str):
    video_path = VideoUtil.get_mp4_path(video_id)
    audio_path = VideoUtil.get_mp3_path(video_id)
    
    # 오디오 추출
    start_time = time.time()
    print("오디오 추출 시작")
    extract_audio_from_video(video_path, audio_path)
    end_time = time.time()
    elapsed_time = end_time - start_time
    print("오디오 추출 완료")
    print(f"오디오 추출 실행 시간: {elapsed_time:.2f} 초")
    
    # Wisper 텍스트 추출
    start_time = time.time()
    print("Wisper 텍스트 추출 시작")
    segments = extract_speech_to_text_with_time(audio_path)
    end_time = time.time()
    elapsed_time = end_time - start_time
    print("Wisper 텍스트 추출 완료")
    print(f"Wisper 텍스트 추출 실행 시간: {elapsed_time:.2f} 초")