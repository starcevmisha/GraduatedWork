#include <Keyboard.h>
#include <Crypto.h>
#include <AES.h>
#include <string.h>

const int LED_RX = 17;
byte AES_KEY[] = { 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F };

AES128 aes128;
byte buffer[16] = {};

void setup()
{
    Serial1.begin(9600); // hardware Serail Port
    Serial.begin(9600); // Software Serial Port to usb
    pinMode(LED_RX, OUTPUT); // LED
    aes128.setKey(AES_KEY, aes128.keySize());
}

void loop()
{
    delay(500);
    if (Serial1.available() > 0) {
        auto data_size = Serial1.available();
        byte byteData[data_size] = {};
        Serial1.readBytes(byteData, data_size);

        char result[data_size] = {};

        for (int i = 0; i < data_size; i += 16) {
            aes128.decryptBlock(buffer, byteData);
            int copy_len = 16;
            if (buffer[15] < 16) {
                copy_len -= buffer[15];
            }
            memcpy(result + i, buffer, copy_len);
        }

        String resultString(result);
        Keyboard.print(resultString);
    }


//  if (Serial.available() > 0) {
//      auto myData = Serial.readString();
//      Serial1.println(myData);
//  }
}
