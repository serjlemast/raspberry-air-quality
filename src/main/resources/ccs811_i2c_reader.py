import time
import board
import adafruit_ccs811
import json

# Initialize CCS811 sensor
i2c = board.I2C()
ccs811 = adafruit_ccs811.CCS811(i2c)

# Wait for the sensor to be ready
while not ccs811.data_ready:
    pass

def read_ccs811_data():
    try:
        eco2 = ccs811.eco2
        tvoc = ccs811.tvoc
        temperature_c = ccs811.temperature

        data = {
            "temperature_celsius": round(temperature_c, 1),
            "tvoc": tvoc,
            "eco2": eco2
        }

        return json.dumps(data)

    except RuntimeError as error:
        return json.dumps({"error": str(error)})
    except Exception as error:
        return json.dumps({"error": "Unexpected error: " + str(error)})

if __name__ == "__main__":
    result = read_ccs811_data()
    print(result)
