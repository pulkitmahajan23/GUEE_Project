#include <FirebaseESP32.h>
#define FIREBASE_HOST "guee-1e7ea-default-rtdb.firebaseio.com/"
#define FIREBASE_AUTH "J6jjJNX7qfHSK2Zv60uEKE5SxEx5YcHIzEX3JjjW"
#define WIFI_SSID "Mishal"
#define WIFI_PASSWORD "anita1971"
FirebaseData firebaseData;

const int freq=5000;
const int ledChannel=0;
const int resolution=8;

const int Sensor_Pin = 34;
unsigned int Sensitivity = 66;   
float Vpp = 0; // peak-peak voltage 
float Vrms = 0; // rms voltage
float Irms = 0; // rms current
float Supply_Voltage = 230.0;           // reading from DMM
float Vcc = 3.3;         // ADC reference voltage 
float power = 0;         // power in watt              
float Wh =0 ;             // Energy in kWh
int status1=0;
unsigned long last_time =0;
unsigned long current_time =0;
unsigned int pF = 95;           // Power Factor default 95
float bill_amount = 0;   // 30 day cost as present energy usage incl approx PF
unsigned int baseTariff=550.0;   //Rs 110 per KVA per month, assuming 5 KVA
unsigned int energyTariff = 5.5; // Energy cost in INR per unit (kWh)

const int RELAY_PIN = 35;  
int threshold_max=16;

void getACS712() {  // for AC

  
  if (Firebase.getInt(firebaseData, "/GUEE/Status")) {
    status1=firebaseData.intData();
  } 
   else {
    Serial.println(firebaseData.errorReason());
  }
  if (status1==1){
    ledcWrite(ledChannel,HIGH);
  }
  else{
    ledcWrite(ledChannel,LOW);
  }

  
    Vpp = getVPP();
    Vrms = (Vpp/2.0) *0.707; 
    Irms = (Vrms * Sensitivity)*1000;
    if((Irms > -0.015) && (Irms < 0.008)){  // remove low end chatter
      Irms = 0.0;
    }

    if (Irms>threshold_max){
    ledcWrite(ledChannel,HIGH);
    }
    else{
    ledcWrite(ledChannel,LOW);
    }
    
    
    power= (Supply_Voltage * Irms) * (pF / 100.0); 
    last_time = current_time;
    current_time = millis();    
    Wh = Wh+  power *(( current_time -last_time) /3600000.0) ; // calculating energy in Watt-Hour
    bill_amount = baseTariff+(Wh * (energyTariff/1000));
    Serial.print("Irms:  "); 
    Serial.print(String(Irms, 3));
    Serial.println(" A");
    Serial.print("Power: ");   
    Serial.print(String(power, 3)); 
    Serial.println(" W"); 
    Serial.print("  Bill Amount: INR"); 
    Serial.println(String(bill_amount, 2));
    Serial.print("Energy: ");
    Serial.println(Wh);

    if(Firebase.set(firebaseData, "/GUEE/Current_Ac",Irms))
    {
      //Serial.println("Success_Current_AC");
    }
    else
    {
      Serial.println("error_Current_AC");
      Serial.println(firebaseData.errorReason());
    }
    if(Firebase.set(firebaseData, "/GUEE/Power",power))
    {
      //Serial.println("Success_Power_AC");
    }
    else
    {
      Serial.println("error_Power_AC");
      Serial.println(firebaseData.errorReason());
    }
    if(Firebase.set(firebaseData, "/GUEE/Energy",Wh))
    {
      //Serial.println("Success_Energy_AC");
    }
    else
    {
      Serial.println("error_Energy_AC");
      Serial.println(firebaseData.errorReason());
    }
    if(Firebase.set(firebaseData, "/GUEE/Tariff",bill_amount))
    {
      //Serial.println("Success_Tariff");
    }
    else
    {
      Serial.println("error_Tariff");
      Serial.println(firebaseData.errorReason());
    }
}

float getVPP()
{
  float result; 
  int readValue;                
  int maxValue = 0;             
  int minValue = 1024;          
  uint32_t start_time = millis();
  while((millis()-start_time) < 950) //read every 0.95 Sec
  {
     readValue = analogRead(Sensor_Pin);    
     if (readValue > maxValue) 
     {         
         maxValue = readValue; 
     }
     if (readValue < minValue) 
     {          
         minValue = readValue;
     }
  } 
   result = ((maxValue - minValue) * Vcc) / 1024.0;  
   return result;
 }

 void setup() {
  Serial.begin(115200);
  pinMode(Sensor_Pin,OUTPUT);
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
      Wh=firebaseData.floatData();
      Serial.print("first value");
      Serial.println(Wh);
  } else {
    Serial.println(firebaseData.errorReason());
  }
  ledcSetup(ledChannel,freq,resolution);
  ledcAttachPin(RELAY_PIN,ledChannel);
}


 void loop(){
  getACS712();
  delay(500);
 }
