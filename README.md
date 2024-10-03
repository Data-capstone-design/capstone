## 기술 스택

- fastapi
- pytube (pytubefix)
- python-ffmpeg
- Wisper
- Nuxt3
- Pinia
- Quasar

## Frontend

### 애플리케이션 실행

```bash
npm run dev
```

## Backend/FastAPI

### 가상환경 설정

```bash
# venv 모듈을 이용해 venv 이름으로 가상환경 생성
python3 -m venv venv

# 가상환경 진입
source vnev/bin/activate

# pip 버전 업데이트 (필요시)
python -m pip install --upgrade pip

# 패키지 설치
pip install -r requirements.txt
```

### 애플리케이션 실행

```bash
uvicorn app.main:app --reload
```
