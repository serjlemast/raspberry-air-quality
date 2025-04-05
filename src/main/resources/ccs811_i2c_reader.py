import json
import random

def generate_mock_ccs811_data():
    try:
        tvoc = random.randint(0, 600)
        eco2 = random.randint(400, 2000)

        data = {
            "tvoc": tvoc,
            "eco2": eco2
        }

        return json.dumps(data)
    except Exception as error:
        return json.dumps({"error": "Unexpected error: " + str(error)})

if __name__ == "__main__":
    result = generate_mock_ccs811_data()
    print(result)