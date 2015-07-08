  #include <SoftwareSerial.h>
String stringOne;
SoftwareSerial Genotronex(10, 11); // RX | TX
int i=0;
int dizi[30];
void setup()
{
  pinMode(9, OUTPUT);  // this pin will pull the HC-05 pin 34 (key pin) HIGH to switch module to AT mode
  digitalWrite(9, HIGH);
    Serial.begin(9600);
  Serial.println("Enter AT commands:");
  Genotronex.begin(9600);  // HC-05 default speed in AT command more
}

void loop()
{
    i=0;
  stringOne = "";
 
  // Keep reading from HC-05 and send to Arduino Serial Monitor
  if (Genotronex.available()){
    while (Genotronex.available()){
  dizi[i]=Genotronex.read()-48;
  i++;
  
  }
  for(int j=0;j<i && i>0;j++){
  stringOne += dizi[j];
    

}
   
  // Keep reading from Arduino Serial Monitor and send to HC-05
  
}
while(!Genotronex.available()){
     Serial.println(stringOne.substring(0,6));
     }
}
