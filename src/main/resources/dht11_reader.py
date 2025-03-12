import time
import board
import adafruit_dht
import json

# Инициализация DHT11 датчика
dhtDevice = adafruit_dht.DHT11(board.D4, use_pulseio=False)

def read_dht11_data():
    try:
        # Чтение температуры и влажности
        temperature_c = dhtDevice.temperature
        temperature_f = temperature_c * (9 / 5) + 32
        humidity = dhtDevice.humidity

        # Формирование данных в формате JSON
        data = {
            "temperature_celsius": temperature_c,
            "temperature_fahrenheit": temperature_f,
            "humidity": humidity
        }

        return json.dumps(data)  # Возвращаем данные в формате JSON

    except RuntimeError as error:
        # Ошибки чтения
        return json.dumps({"error": str(error)})
    except Exception as error:
        dhtDevice.exit()
        return json.dumps({"error": "Unexpected error: " + str(error)})

# Вводим цикл для получения данных
if __name__ == "__main__":
    result = read_dht11_data()
    print(result)  # Этот результат будет захвачен и передан в Java
