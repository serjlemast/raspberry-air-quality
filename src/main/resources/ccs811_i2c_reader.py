import json
import random
import socket
import time
import yaml
import os

DEFAULT_HOST = '127.0.0.1'
DEFAULT_PORT = 5001
SEND_INTERVAL = 2  # seconds
CONFIG_FILE = 'application.yaml'


def load_config():
    try:
        with open(CONFIG_FILE, 'r') as f:
            config = yaml.safe_load(f)

        socket_config = config.get('sensor', {}).get('i2c', {}).get('socket', {})
        host = socket_config.get('host', DEFAULT_HOST)
        port = int(socket_config.get('port', DEFAULT_PORT))
        return host, port

    except FileNotFoundError:
        print(f"[WARN] Config file '{CONFIG_FILE}' not found. Using defaults.")
    except Exception as e:
        print(f"[ERROR] Failed to load config: {e}")

    return DEFAULT_HOST, DEFAULT_PORT


def generate_mock_ccs811_data():
    return {
        "tvoc": random.randint(0, 600),
        "eco2": random.randint(400, 2000)
    }


def handle_client(conn, addr):
    print(f"[INFO] Connected by {addr}")
    try:
        while True:
            data = generate_mock_ccs811_data()
            conn.sendall((json.dumps(data) + '\n').encode('utf-8'))
            time.sleep(SEND_INTERVAL)
    except (BrokenPipeError, ConnectionResetError):
        print("[WARN] Client disconnected.")
    except Exception as e:
        print(f"[ERROR] Unexpected error: {e}")
    finally:
        conn.close()


def start_server():
    host, port = load_config()
    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as server:
        server.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        server.bind((host, port))
        server.listen()
        print(f"[INFO] Server listening on {host}:{port}...")

        while True:
            conn, addr = server.accept()
            handle_client(conn, addr)


if __name__ == "__main__":
    start_server()
