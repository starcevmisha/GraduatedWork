#include <Keyboard.h>
const int LED_RX = 17;
void setup() {
  Serial1.begin(9600); // hardware Serail Port
  Serial.begin(9600); // Software Serial Port to usb
  pinMode(LED_RX, OUTPUT); // LED
}

void loop() {
  delay(100);
  if (Serial1.available() > 0) {
    auto myData = Serial1.readString();
    if (myData == "1"){
      digitalWrite(LED_RX, LOW);
    } else if (myData == "0"){
      digitalWrite(LED_RX, HIGH);
    }
    Serial.println(myData);
    Keyboard.print(myData);
    
  }

   if (Serial.available() > 0) {
    auto myData = Serial.readString();
    Serial1.println(myData);
  }
}
