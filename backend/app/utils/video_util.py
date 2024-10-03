class VideoUtil:
    download_path = 'downloads'

    @classmethod
    def get_mp4_path(cls,video_id):
        return  f"{cls.download_path}/mp4/{video_id}.mp4"
    
    @classmethod
    def get_mp3_path(cls,video_id):
        return f"{cls.download_path}/mp3/{video_id}.mp3"
    
    