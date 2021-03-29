#include <ETH.h>
#include <WiFi.h>
#include "ACS712.h"

#include <FirebaseESP32.h>
//#include <FirebaseESP32HTTPClient.h>
//#include <FirebaseJson.h>
#define FIREBASE_HOST "guee-1e7ea-default-rtdb.firebaseio.com"
#define FIREBASE_AUTH "J6jjJNX7qfHSK2Zv60uEKE5SxEx5YcHIzEX3JjjW"
#define WIFI_SSID "Inspiron"
#define WIFI_PASSWORD "inspiron"
FirebaseData firebaseData;
//FirebaseJson json;
  
#define ACS712_PIN_VO	34

// Global variables and defines
const int acs712calFactor = 2822;
// object initialization
ACS712 acs712(ACS712_PIN_VO);


// define vars for testing menu
const int timeout = 10000;       //define timeout of 10 sec
char menuOption = 0;
long time0;

// Setup the essentials for your circuit to work. It runs first every time your circuit is powered with electricity.
void setup() 
{
    Serial.begin(115200);
    delay(1000);
     WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
     Serial.print("Connecting to Wi-Fi");
    while (WiFi.status() != WL_CONNECTED)
    {
      Serial.print(".");
      delay(300);
    }
    Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);
    Firebase.reconnectWiFi(true);
    Serial.println(WiFi.localIP());
    
    //Manually calibarte the ACS712 current sensor.
    //Connet the ACS to your board, but do not connect the current sensing side.
    //Follow serial monitor instructions. This needs be done one time only.
    //acs712.calibrate(acs712calFactor);
}


void loop() 
{
    float acs712Currrent  = acs712.getCurrent();
    Serial.print(acs712Currrent); Serial.println(F(" [mA]"));

    //}
      if(Firebase.set(firebaseData, "/Current_DC",acs712Currrent))
     {
        Serial.println("Success");
     }
    else
     {
        Serial.println("error");
        Serial.println(firebaseData.errorReason());
     }
    delay(1500);

}
