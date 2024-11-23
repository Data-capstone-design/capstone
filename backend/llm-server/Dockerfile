FROM python:3.12-slim

# 필수 패키지 설치
RUN apt-get update && apt-get install -y --no-install-recommends \
    gcc \
    g++ \
    make \
    libffi-dev \
    libssl-dev \
    librdkafka-dev \
    cargo \
    rustc \
    build-essential \
    && apt-get clean && rm -rf /var/lib/apt/lists/*

# 최신 pip 및 빌드 도구 설치
RUN pip install --upgrade pip setuptools wheel

# PYTHONPATH 설정
ENV PYTHONPATH=/llm-server

# 작업 디렉토리 생성
WORKDIR /llm-server/app

# 애플리케이션 코드 복사
COPY ./app /llm-server/app

# 의존성 파일 복사 및 설치
COPY requirements.txt /app/requirements.txt
RUN pip install --no-cache-dir -r /app/requirements.txt

# 애플리케이션 실행
CMD ["uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "8001"]