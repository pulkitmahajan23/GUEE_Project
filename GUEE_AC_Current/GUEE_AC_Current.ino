#include <ETH.h>
#include <WiFi.h>


#include <FirebaseESP32.h>
//#include <FirebaseESP32HTTPClient.h>
//#include <FirebaseJson.h>


#include <FilterDerivative.h>
#include <FilterOnePole.h>
#include <Filters.h>
#include <FilterTwoPole.h>
//#include <FloatDefine.h>
#include <RunningStatistics.h>
#define FIREBASE_HOST "guee-1e7ea-default-rtdb.firebaseio.com/"
#define FIREBASE_AUTH "J6jjJNX7qfHSK2Zv60uEKE5SxEx5YcHIzEX3JjjW"
#define WIFI_SSID "Mishal"
#define WIFI_PASSWORD "anita1971"
FirebaseData firebaseData;
//FirebaseJson json;

  const int gpio_pin=34;
  
  float testFrequency = 50;                     // test signal frequency (Hz)
  float windowLength = 20.0/testFrequency;     // how long to average the signal, for statistist
  float sensorValue = 0;
  float intercept = -0.1129; // to be adjusted based on calibration testing
  float slope = 0.0405; // to be adjusted based on calibration testing
  float current_amps; // estimated actual current in amps
  //String current;
  
  const int RELAY_PIN = 33;  // the Arduino pin, which connects to the IN pin of relay
  int threshold_max=10;

  float Power_AC;
  double Energy_AC;
  
  unsigned long printPeriod = 1000; // in milliseconds
  // Track time in milliseconds since last reading 
  unsigned long previousMillis = 0;

void setup() {
  Serial.begin(115200);   // start the serial port
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  Serial.print("Connecting to Wi-Fi");
  while (WiFi.status() != WL_CONNECTED)
  {
    Serial.print(".");
    delay(300);
  }
  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);
  Firebase.reconnectWiFi(true);
  //Firebase.setReadTimeout(firebaseData, 1000 * 60);
  //Firebase.setwriteSizeLimit(firebaseData, "tiny");
  pinMode(RELAY_PIN, OUTPUT);
  digitalWrite(RELAY_PIN, HIGH);

  if (Firebase.getDouble(firebaseData, "/GUEE/Energy")) {
      Energy_AC=firebaseData.floatData();
  } else {
    Serial.println(firebaseData.errorReason());
  }
}

void loop() {
  RunningStatistics inputStats; // create statistics to look at the raw test signal
  inputStats.setWindowSecs( windowLength );
   
  //while( true ) {   
    sensorValue = analogRead(gpio_pin);  // read the analog in value:
    inputStats.input(sensorValue);  // log to Stats function
        
    if((unsigned long)(millis() - previousMillis) >= printPeriod) {
      previousMillis = millis();   // update time
      
      // display current values to the screen
      Serial.print( "\n" );
      // output sigma or variation values associated with the inputValue itsel
      Serial.print( "\tsigma: " );
      double sigma_val=inputStats.sigma();
      Serial.print( sigma_val );
      // convert signal sigma value to current in amps
      current_amps = intercept + slope * sigma_val;
      Serial.print( "\tamps: " ); 
      Serial.print( current_amps );
      Power_AC=current_amps*230*0.97;
      Energy_AC=Energy_AC+(Power_AC*5);
      Serial.println(Power_AC);
      Serial.println(Energy_AC);

      //current=String(current_amps);
      //String current=""+current_amps;

      /*if (sensorValue>threshold_max){
        digitalWrite(RELAY_PIN, LOW);
        delay(500);
      }
      else if (Serial.available()){
        delay(3000);
        Serial.read();
        digitalWrite(RELAY_PIN, LOW);
        delay(500); 
      }
      else{*/
        if(Firebase.set(firebaseData, "/GUEE/Current_Ac",current_amps))
            {
              Serial.println("Success_Current_AC");
            }
            else
            {
              Serial.println("error_Current_AC");
              Serial.println(firebaseData.errorReason());
            }
         if(Firebase.set(firebaseData, "/GUEE/Power",Power_AC))
            {
              Serial.println("Success_Power_AC");
            }
            else
            {
              Serial.println("error_Power_AC");
              Serial.println(firebaseData.errorReason());
            }
         if(Firebase.set(firebaseData, "/GUEE/Energy",Energy_AC))
            {
              Serial.println("Success_Energy_AC");
            }
            else
            {
              Serial.println("error_Energy_AC");
              Serial.println(firebaseData.errorReason());
            }
      //}
      
       //json.set("/data", current);
  //Firebase.updateNode(firebaseData,"/Sensor",json);
      
    }
  //}
}
