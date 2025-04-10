import socket

HOST = '127.0.0.1'
PORT = 5001

with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
    s.connect((HOST, PORT))
    print("Connected to server!")
    while True:
        data = s.recv(1024)
        if not data:
            break
        print("Received:", data.decode().strip())
