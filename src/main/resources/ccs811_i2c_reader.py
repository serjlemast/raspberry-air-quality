import json
import random
import time

def generate_mock_ccs811_data():
    tvoc = random.randint(0, 600)
    eco2 = random.randint(400, 2000)
    return {
        "tvoc": tvoc,
        "eco2": eco2
    }

if __name__ == "__main__":
    while True:
        data = generate_mock_ccs811_data()
        print(json.dumps(data), flush=True)
        time.sleep(2)
