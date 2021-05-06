  #include <LiquidCrystal.h>
  
  LiquidCrystal LCD(7, 6, 5, 4, 3, 2);
  double AcsOffset=2.5;
  double sensor=0.066;
  double current_motor=0;
  double tension_motor=0;
  double current_led=0;
  double tension_led=0;

  void setup() {
  Serial.begin(9600);
  LCD.begin(16,2);
  }

  void loop() {

  double value_motor=analogRead(A0);
  tension_motor=(value_motor*5.0/1023);
  current_motor=(tension_motor-AcsOffset)/sensor;
  LCD.setCursor(0,0);
  LCD.print("Motor plug=");
  LCD.print(current_motor);
  LCD.print("A");
  delay(500);

  double value_led=analogRead(A1);
  tension_led=(value_led*5.0/1023);
  current_led=(tension_led-AcsOffset)/sensor;
  LCD.setCursor(0,1);
  LCD.print("LED plug=");
  LCD.print(current_led);
  LCD.print("A");
  delay(500);
  }
