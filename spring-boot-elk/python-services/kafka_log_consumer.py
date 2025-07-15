import logging
import time
from confluent_kafka import Consumer, KafkaException
from consumer_config import Config
import json
from typing import Any


def wait_for_kafka(bootstrap_servers, group_id, timeout=60):
    start = time.time()
    while True:
        try:
            consumer = Consumer({
                'bootstrap.servers': bootstrap_servers,
                'group.id': group_id,
                'session.timeout.ms': 6000
            })
            consumer.close()
            print("Kafka is available.")
            break
        except KafkaException as e:
            if time.time() - start > timeout:
                raise
            print(f"Waiting for Kafka to be available... Error: {e}")
            time.sleep(3)

class LogHandler:
    """Abstracts log handling logic for extensibility."""
    def handle(self, log: Any) -> None:
        raise NotImplementedError

class ConsoleLogHandler(LogHandler):
    def handle(self, log: Any) -> None:
        print(log)

class ConfluentKafkaLogConsumer:
    def __init__(self, config: Config, handler: LogHandler) -> None:
        self.config = config
        self.handler = handler
        self.consumer = Consumer({
            'bootstrap.servers': self.config.KAFKA_BOOTSTRAP_SERVERS,
            'group.id': self.config.KAFKA_GROUP_ID,
            'auto.offset.reset': self.config.KAFKA_AUTO_OFFSET_RESET,
            'enable.auto.commit': self.config.KAFKA_ENABLE_AUTO_COMMIT,
        })
        logging.info(f"Confluent Kafka consumer initialized for topic: {self.config.KAFKA_TOPIC}")

    def consume(self) -> None:
        logging.info("Starting Confluent Kafka log consumption loop...")
        self.consumer.subscribe([self.config.KAFKA_TOPIC])
        try:
            while True:
                msg = self.consumer.poll(1.0)
                if msg is None:
                    continue
                if msg.error():
                    logging.error(f"Consumer error: {msg.error()}")
                    continue
                try:
                    value = json.loads(msg.value().decode('utf-8'))
                except Exception as e:
                    logging.error(f"Failed to deserialize message: {e}")
                    value = msg.value()
                self.handler.handle(value)
        except KeyboardInterrupt:
            logging.info("Consumer interrupted by user.")
        except KafkaException as e:
            logging.error(f"Kafka exception: {e}")
        finally:
            self.consumer.close()
            logging.info("Confluent Kafka consumer closed.")

if __name__ == "__main__":
    logging.basicConfig(level=logging.INFO, format='%(asctime)s %(levelname)s %(message)s')
    config = Config()
    wait_for_kafka(config.KAFKA_BOOTSTRAP_SERVERS, config.KAFKA_GROUP_ID)
    handler = ConsoleLogHandler()
    consumer = ConfluentKafkaLogConsumer(config, handler)
    consumer.consume() 