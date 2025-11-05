import os
import signal
import sys
import time
from typing import Optional

import orjson
from confluent_kafka import Consumer, KafkaException


def getenv(name: str, default: Optional[str] = None) -> str:
    value = os.getenv(name)
    if value is None or value.strip() == "":
        return default if default is not None else ""
    return value


def create_consumer(bootstrap_servers: str, group_id: str) -> Consumer:
    return Consumer(
        {
            "bootstrap.servers": bootstrap_servers,
            "group.id": group_id,
            "auto.offset.reset": "earliest",
            "enable.auto.commit": True,
            "session.timeout.ms": 45000,
        }
    )


def pretty_print(msg_bytes: bytes) -> None:
    try:
        data = orjson.loads(msg_bytes)
        print(orjson.dumps(data, option=orjson.OPT_INDENT_2).decode("utf-8"))
    except Exception:
        # Fallback to raw
        print(msg_bytes.decode("utf-8", errors="replace"))


def main() -> int:
    bootstrap = getenv("KAFKA_BOOTSTRAP_SERVERS", "localhost:9094")
    topic = getenv("KAFKA_TOPIC", "spring-boot-logs")
    group_id = getenv("KAFKA_GROUP_ID", "log-consumer")

    consumer = create_consumer(bootstrap, group_id)
    running = True

    def handle_sigint(_sig, _frame):
        nonlocal running
        running = False

    signal.signal(signal.SIGINT, handle_sigint)
    signal.signal(signal.SIGTERM, handle_sigint)

    try:
        consumer.subscribe([topic])
        print(f"Connected to Kafka at {bootstrap}; consuming topic '{topic}' (group '{group_id}'). Press Ctrl+C to stop.")
        while running:
            msg = consumer.poll(1.0)
            if msg is None:
                continue
            if msg.error():
                raise KafkaException(msg.error())
            pretty_print(msg.value())
    except KeyboardInterrupt:
        pass
    except Exception as exc:
        print(f"Error: {exc}", file=sys.stderr)
        return 1
    finally:
        try:
            consumer.close()
        except Exception:
            pass
    return 0


if __name__ == "__main__":
    sys.exit(main())


