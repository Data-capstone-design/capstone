import os

metrics_url: str = os.environ.get('METRICS_URL')
logs_url: str = os.environ.get('LOGS_URL')
metrics_username: str = os.environ.get('METRICS_USERNAME')
logs_username: str = os.environ.get('LOGS_USERNAME')
access_token: str = str(os.environ.get('GRAFANA_TOKEN'))
disable_content: bool = False
environment: str = "default"