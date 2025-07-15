import os
from dotenv import load_dotenv

load_dotenv()

class Config:
    KAFKA_BOOTSTRAP_SERVERS = os.getenv('KAFKA_BOOTSTRAP_SERVERS', 'localhost:9092')
    KAFKA_TOPIC = os.getenv('KAFKA_TOPIC', 'app-logs')
    KAFKA_GROUP_ID = os.getenv('KAFKA_GROUP_ID', 'log-consumer-group')
    KAFKA_AUTO_OFFSET_RESET = os.getenv('KAFKA_AUTO_OFFSET_RESET', 'earliest')
    KAFKA_ENABLE_AUTO_COMMIT = os.getenv('KAFKA_ENABLE_AUTO_COMMIT', 'true').lower() == 'true'
    KAFKA_CONSUMER_TIMEOUT_MS = int(os.getenv('KAFKA_CONSUMER_TIMEOUT_MS', '1000')) 