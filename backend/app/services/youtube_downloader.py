from pytubefix import YouTube
from pytubefix.cli import on_progress

import os
import time 

def download_video(youtube_url: str) -> str:
    yt = YouTube(youtube_url.strip(), on_progress_callback = on_progress)

    video_title = yt.title
    video_id = yt.video_id

    print(f"비디오 제목: {video_title}")
    print(f"비디오 ID: {video_id}")

    download_path: str = "downloads/mp4/"
    filename = f"{video_id}.mp4"
    file_path = os.path.join(download_path, filename)
    
    ys = yt.streams.get_highest_resolution()   

    if os.path.exists(file_path):
        print(f"비디오가 이미 다운로드 폴더에 존재: {video_title}")
    else:
        print("비디오 다운로드 시작")

        start_time = time.time()
        ys.download(output_path= download_path, filename= filename)
        end_time = time.time()

        elapsed_time = end_time - start_time
        print("비디오 다운로드 완료")
        print(f"다운로드 실행 시간: {elapsed_time:.2f} 초")
       
    return video_id