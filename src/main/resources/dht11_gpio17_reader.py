import time
import board
import adafruit_dht
import json

dhtDevice = adafruit_dht.DHT11(board.D17, use_pulseio=False)

def read_dht11_data():
    try:

        temperature_c = dhtDevice.temperature
        temperature_f = temperature_c * (9 / 5) + 32
        humidity = dhtDevice.humidity

        data = {
            "temperature_celsius": temperature_c,
            "temperature_fahrenheit": temperature_f,
            "humidity": humidity
        }

        return json.dumps(data)

    except RuntimeError as error:

        return json.dumps({"error": str(error)})
    except Exception as error:
        dhtDevice.exit()
        return json.dumps({"error": "Unexpected error: " + str(error)})

if __name__ == "__main__":
    result = read_dht11_data()
    print(result)
