# Python 3.9 이미지 사용
FROM python:3.12-alpine3.20

# 필수 빌드 도구 설치
RUN apk add --no-cache \
    gcc \
    g++ \
    make \
    python3-dev \
    libffi-dev \
    openssl-dev \
    librdkafka-dev

# 작업 디렉토리 생성
WORKDIR /app

# 애플리케이션 코드 복사
COPY ./app /app

# 의존성 파일 복사 및 설치
COPY requirements.txt /app
RUN pip install --no-cache-dir -r requirements.txt

# 애플리케이션 실행
CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8001"]