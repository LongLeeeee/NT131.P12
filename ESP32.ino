#include <HTTPClient.h>
#include <Wire.h>
#include "MAX30105.h"
#include "heartRate.h"
#include "spo2_algorithm.h"
#include <LiquidCrystal_I2C.h>
#include <Adafruit_MLX90614.h>
#include <WiFi.h>

const char *ssid = "LongLee";
const char *password = "nenecamon";

const char *apiAddress = "https://b26c-58-186-197-9.ngrok-free.app/data";
MAX30105 particleSensor;
LiquidCrystal_I2C lcd(0x27, 16, 2);
Adafruit_MLX90614 mlx;
TwoWire WireHeart = TwoWire(1);
const byte RATE_SIZE = 4;
byte rates[RATE_SIZE];
byte rateSpot = 0;
long lastBeat = 0;

float beatsPerMinute;
int beatAvg;

uint32_t irBuffer[100];
uint32_t redBuffer[100];
int32_t spo2;
int8_t spo2Valid;
int32_t heartRate;
int8_t heartRateValid;

long sharedIRValue = 0;
SemaphoreHandle_t dataMutex;

struct HeartRateData {
  float bpm;
  int avgBpm;
  long irValue;

} heartRateData;

struct SpO2Data {
  float spo2;
} spo2Data;


struct TemperatureData {
  float temperature;
} temperatureData;

void setup() {
  Serial.begin(115200);
  Serial.println("Initializing...");

  lcd.init();
  lcd.backlight();
  lcd.setCursor(0, 0);
  lcd.print("Initializing...");

  WiFi.begin(ssid, password);

  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }

  Serial.println("\nĐã kết nối thành công!");
  Serial.print("Địa chỉ IP: ");
  Serial.println(WiFi.localIP());
  Serial.println(apiAddress);


  WireHeart.begin(16, 17);
  if (!particleSensor.begin(WireHeart, I2C_SPEED_FAST)) {
    lcd.clear();
    lcd.setCursor(0, 0);
    lcd.print("MAX30102 not found");
    while (1)
      particleSensor.setPulseAmplitudeRed(0x0A);
    particleSensor.setPulseAmplitudeGreen(0);
  }
  particleSensor.setup();
  Wire.begin(21, 22);
  if (!mlx.begin()) {
    lcd.clear();
    lcd.setCursor(0, 0);
    lcd.print("MLX90614 not found");
    while (1)
      ;
  }
  dataMutex = xSemaphoreCreateMutex();
  if (dataMutex == NULL) {
    Serial.println("Failed to create mutex");
    while (1)
      ;
  }


  xTaskCreate(taskReadHeartRate, "ReadHeartRate", 4096, NULL, 1, NULL);
  xTaskCreate(taskReadTemperature, "ReadTemperature", 4096, NULL, 1, NULL);
  xTaskCreate(taskReadSpO2, "ReadSpO2", 4096, NULL, 1, NULL);
  xTaskCreate(taskUpdateLCD, "UpdateLCD", 4096, NULL, 0, NULL);
  xTaskCreate(taskHTTP, "HTTPpost", 4096, NULL, 1, NULL);


  lcd.clear();
  Serial.println("Place your finger on the sensor.");
}

void loop() {
  vTaskDelay(portMAX_DELAY);
}

void taskReadSpO2(void *pvParameters) {
  while (1) {
    for (int i = 0; i < 100; i++) {
      irBuffer[i] = particleSensor.getIR();
      redBuffer[i] = particleSensor.getRed();
      if (xSemaphoreTake(dataMutex, portMAX_DELAY) == pdTRUE) {
        sharedIRValue = irBuffer[i];
        xSemaphoreGive(dataMutex);
      }
      vTaskDelay(10 / portTICK_PERIOD_MS);
    }


    maxim_heart_rate_and_oxygen_saturation(
      irBuffer, 100, redBuffer,
      &spo2, &spo2Valid,
      &heartRate, &heartRateValid);

    if (xSemaphoreTake(dataMutex, portMAX_DELAY) == pdTRUE) {
      spo2Data.spo2 = spo2Valid ? spo2 : 0;
      xSemaphoreGive(dataMutex);
    }

    vTaskDelay(100 / portTICK_PERIOD_MS);
  }
}

// Task HTTP POST
void taskHTTP(void *pvParameters) {
  while (1) {
    if (WiFi.status() == WL_CONNECTED) {
      HTTPClient http;
      http.begin(apiAddress);
      http.addHeader("Content-Type", "application/json");
      String payload;
      if (xSemaphoreTake(dataMutex, portMAX_DELAY) == pdTRUE) {
        payload = "{\"bpm\": " + String(heartRateData.avgBpm) + ",\"spo2\": " + String(spo2Data.spo2) + ", \"temperature\": " + String(temperatureData.temperature, 1) + "}";
        xSemaphoreGive(dataMutex);
      }

      int httpResponseCode = http.POST(payload);

      if (httpResponseCode > 0) {
        Serial.printf("POST Response code: %d\n", httpResponseCode);
        String response = http.getString();
        Serial.println("Response: " + response);
      } else {
        Serial.printf("POST Failed, error: %s\n", http.errorToString(httpResponseCode).c_str());
      }

      http.end();
    } else {
      Serial.println("WiFi disconnected, retrying...");
    }

    vTaskDelay(1000 / portTICK_PERIOD_MS);
  }
}
void taskReadHeartRate(void *pvParameters) {
  while (1) {
    long irValue = 0;
    if (xSemaphoreTake(dataMutex, portMAX_DELAY) == pdTRUE) {
      irValue = sharedIRValue;
      xSemaphoreGive(dataMutex);
    }
    if (irValue < 50000) {
      beatsPerMinute = 0;
      beatAvg = 0;
    } else {
      if (checkForBeat(irValue) == true) {
        long delta = millis() - lastBeat;
        lastBeat = millis();

        beatsPerMinute = 60 / (delta / 1000.0);
        if (beatsPerMinute < 255 && beatsPerMinute > 20) {
          rates[rateSpot++] = (byte)beatsPerMinute;
          rateSpot %= RATE_SIZE;

          beatAvg = 0;
          for (byte x = 0; x < RATE_SIZE; x++) {
            beatAvg += rates[x];
          }
          beatAvg /= RATE_SIZE;
        }
      }
    }
    if (xSemaphoreTake(dataMutex, portMAX_DELAY) == pdTRUE) {
      heartRateData.bpm = beatsPerMinute;
      heartRateData.avgBpm = beatAvg;
      heartRateData.irValue = irValue;
      xSemaphoreGive(dataMutex);
    }

    vTaskDelay(25 / portTICK_PERIOD_MS);
  }
}

void taskReadTemperature(void *pvParameters) {
  while (1) {
    float temperature = mlx.readObjectTempC();
    if (xSemaphoreTake(dataMutex, portMAX_DELAY) == pdTRUE) {
      temperatureData.temperature = temperature - 1.0;
      xSemaphoreGive(dataMutex);
    }

    vTaskDelay(25 / portTICK_PERIOD_MS);
  }
}

void taskUpdateLCD(void *pvParameters) {
  while (1) {
    if (xSemaphoreTake(dataMutex, portMAX_DELAY) == pdTRUE) {
      lcd.setCursor(0, 0);
      lcd.print("BPM:");
      lcd.print(heartRateData.avgBpm, 1);
      lcd.print("   ");

      lcd.setCursor(8, 0);
      lcd.print("SpO2:");
      lcd.print(spo2Data.spo2, 0);
      lcd.print(" %");

      lcd.setCursor(0, 1);
      lcd.print("Temp:");
      lcd.print(temperatureData.temperature, 1);
      lcd.print("C");

      xSemaphoreGive(dataMutex);
    }

    vTaskDelay(500 / portTICK_PERIOD_MS);
  }
}
