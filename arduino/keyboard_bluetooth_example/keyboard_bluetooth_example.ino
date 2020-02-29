#include <Keyboard.h>
const int LED_RX = 17;

uint32_t SecretKey = -1;

uint32_t createPrivateKey(uint32_t prime){
  randomSeed(analogRead(2));
  return random(1, prime);
}


//code to compute the remainder of two numbers multiplied together.
uint32_t mul_mod(uint32_t a, uint32_t b, uint32_t m){


  uint32_t result = 0; //variable to store the result
  uint32_t runningCount = b % m; //holds the value of b*2^i

  for(int i = 0 ; i < 32 ; i++){

    if(i > 0) runningCount = (runningCount << 1) % m;
    if(bitRead(a,i)){
      result = (result%m + runningCount%m) % m; 

    } 

  }
  return result;
}

//The pow_mod function to compute (b^e) % m that was given in the class files  
uint32_t pow_mod(uint32_t b, uint32_t e, uint32_t m)
{
  uint32_t r;  // result of this function

  uint32_t pow;
  uint32_t e_i = e;
  // current bit position being processed of e, not used except for debugging
  uint8_t i;

  // if b = 0 or m = 0 then result is always 0
  if ( b == 0 || m == 0 ) { 
    return 0; 
  }

  // if e = 0 then result is 1
  if ( e == 0 ) { 
    return 1; 
  }

  // reduce b mod m 
  b = b % m;

  // initialize pow, it satisfies
  //    pow = (b ** (2 ** i)) % m
  pow = b;

  r = 1;

  // stop the moment no bits left in e to be processed
  while ( e_i ) {
    // At this point pow = (b ** (2 ** i)) % m

    // and we need to ensure that  r = (b ** e_[i..0] ) % m
    // is the current bit of e set?
    if ( e_i & 1 ) {
      // this will overflow if numbits(b) + numbits(pow) > 32
      r= mul_mod(r,pow,m);//(r * pow) % m; 
    }

    // now square and move to next bit of e
    // this will overflow if 2 * numbits(pow) > 32
    pow = mul_mod(pow,pow,m);//(pow * pow) % m;

    e_i = e_i >> 1;
    i++;
  }

  // at this point r = (b ** e) % m, provided no overflow occurred
  return r;
}

void setup() {
  Serial1.begin(9600); // hardware Serail Port
  Serial.begin(9600); // Software Serial Port to usb
  pinMode(LED_RX, OUTPUT); // LED
}

void loop() {
  delay(100);
  if (Serial1.available() > 0) {
    auto myData = Serial1.readString();
    if (myData.startsWith("dhs")) { // dhs p={} g={} A={}
      SecretKey = 0
      uint32_t p = 0;
      uint32_t g = 0;
      uint32_t A = 0;

      Serial.println(myData);
      Serial.println(myData.length());
      char charBuf[myData.length() + 2];
      myData.toCharArray(charBuf, myData.length()+1);
      sscanf(charBuf, "dhs p=%lu g=%lu A=%lu\n", &p, &g, &A);

      Serial.print("p=");
      Serial.println(p);
      
      Serial.print("g=");
      Serial.println(g);

      Serial.print("A=");
      Serial.println(A);
      if (p != 0 && g != 0 && A != 0) {
          uint32_t b = createPrivateKey(p);
          uint32_t B = pow_mod(g, b, p);
          SecretKey = pow_mod(A, b, p);
          
          Serial.print("SecretKey=");
          Serial.println(SecretKey);

          Serial.print("B=");
          Serial.println(B);
          
          Serial1.print(B);
          
      }
    } else {
       if (SecretKey >=0) { //secret channel
        // decode mydata
       }
      Keyboard.print(myData);
    }
    
  }

   if (Serial.available() > 0) {
    auto myData = Serial.readString();
    Serial1.println(myData);
  }
}
