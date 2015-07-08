#include <SoftwareSerial.h>// import the serial library
//Genotronex adÄ±nda SoftwareSerial Ã¶zel komutu kullanarak yazÄ±lÄ±msal bir seri port haberleÅŸme ayarlanÄ±yor.
//RX pini 10'uncu TX pinide 11 pin olarak ayarlÄ±yorz
SoftwareSerial Genotronex(10,11); // RX, TX

//DeÄŸiÅŸkenlerinimizi tanÄ±mladÄ±k
//ArkadaÅŸlar arduino uno gibi dÃ¼ÅŸÃ¼k ram'a sahip cihazlarda programlama
//yaparken en az ram kullanÄ±mÄ± Ã¶nemli olduÄŸunda bellekte yer tutacak deÄŸiÅŸkenlerimizi 
//asgari boyutta tanÄ±mlamalÄ±yÄ±z.
boolean ileri=false;
boolean geri=false;
boolean sag=false;
boolean sol=false;
boolean dur=false;
boolean korna=false;
boolean sonDurum=false;
boolean aDur=false;
int enson;
int hiz=0;
int artiDon=0,buyukDon=0;
int dizi[30];
int i=0;
int x=0;
String stringOne;

void setup() {
    
  Serial.begin(9600);
  Genotronex.begin(9600);
  Genotronex.println("zafer");
  //pinlerin durumlarÄ±nÄ± Ã§Ä±kÄ±ÅŸ olarak atadÄ±k 
  pinMode(5,OUTPUT);
  pinMode(6,OUTPUT);
  pinMode(8,OUTPUT);
  pinMode(3,OUTPUT);
  pinMode(4,OUTPUT);
  pinMode(7,OUTPUT);
  pinMode(12, OUTPUT);
  //pinlerimizin hepsini baÅŸlangÄ±cta lojic 0 olarak ayarladÄ±k
  digitalWrite(8,0);
  digitalWrite(3,0);
  digitalWrite(4,0);
  digitalWrite(7,0);
  digitalWrite(12,0);
 
}

void loop() {

  i=0;  
  stringOne = "";
  //Seri porttan okudumuz datalarÄ± diziye attÄ±k
  //-48 yapmamÄ±zÄ±n sebebi ASCII kodlarÄ± decimal'e Ã§evirmek iÃ§in kullandÄ±k
while (Genotronex.available()){
  dizi[i]=Genotronex.read()-48;
  i++;
  
}
//Dizinin her bir elemanÄ±nÄ± string bir deÄŸiÅŸkenimizin iÃ§erisinde yan yana birleÅŸtirdik.
for(int j=0;j<i && i>0;j++){
  stringOne += dizi[j];

}
  
  
while(!Genotronex.available()){
    
     //Telefonumuz Ã¼zerinden gelen datalarÄ± filtrelemek iÃ§in  her datanÄ±n Ã¶ncesinde sÄ±rayla 8 ve 0 rakamlarÄ±nÄ± gÃ¶nderdik.
     //filtrelememizdeki amaÃ§ parazit olarak gelen tek haneli rakamlarÄ±  engellemek. 
     //DevamÄ±ndaki komutlar gelen datalara gÃ¶re rc-arabaya yÃ¶n verme komutlarÄ±dÄ±r.
     
  //   Serial.print("gelen değer:");
   //  Serial.println(stringOne);
     //delay(500);
     if((stringOne.substring(3,6).equals("803")||stringOne.substring(3,6).equals("804"))&&(!stringOne.substring(3,6).equals(stringOne.substring(0,3)))){
       Serial.println("**************************************************************************************"); 
       
       if(stringOne.substring(3,6).equals("804")){
        Serial.println(stringOne.substring(0,6));
         Serial.println(stringOne.substring(0,6));
          if(stringOne.substring(0,2).equals("70")){           
      if(stringOne.substring(2,3).toInt()<8){
             // hiz=90;
            //  hiz-=(stringOne.substring(2,3).toInt()*10);
             artiDon=255-stringOne.substring(2,3).toInt()*20;
              Serial.print("hiizArtiDonSAAAAAAAAAAAAAAAGGGGGGGG:");
              Serial.println(artiDon);
               
      digitalWrite(8,0);
      digitalWrite(3,1);
      analogWrite(5,255);
      digitalWrite(4,0);
      digitalWrite(7,1);
      analogWrite(6,artiDon);      
         }
         else if(stringOne.substring(2,3).toInt()==8){
         
           digitalWrite(8,0);
      digitalWrite(3,1);
      analogWrite(5,255);
      digitalWrite(4,0);
      digitalWrite(7,1);
      analogWrite(6,100);  
         }
          else if(stringOne.substring(2,3).toInt()==9){
         
           digitalWrite(8,0);
      digitalWrite(3,1);
      analogWrite(5,255);
      digitalWrite(4,0);
      digitalWrite(7,1);
      analogWrite(6,0);  
         }
          }
           else if(stringOne.substring(0,2).equals("90")){           
      
              hiz=90;
              hiz-=(stringOne.substring(2,3).toInt()*10);
              artiDon=90+stringOne.substring(2,3).toInt()*20;
              Serial.print("hiizArtiDonSooooool:");
              Serial.println(artiDon);
               
      digitalWrite(8,0);
      digitalWrite(3,1);
      analogWrite(5,artiDon);
      digitalWrite(4,0);
      digitalWrite(7,1);
      analogWrite(6,255);
      enson=2;
      
         
          }
        
       }
       
      else if(stringOne.substring(3,6).equals("803")){
          Serial.println(stringOne.substring(0,6));
          if(stringOne.substring(0,2).equals("70")){           
      if(stringOne.substring(2,3).toInt()<8){
          //    hiz=90;
            //  hiz-=(stringOne.substring(2,3).toInt()*10);
             artiDon=255-stringOne.substring(2,3).toInt()*20;
              Serial.print("hiizArtiDonSAAAAAAAAAAAAAAAGGGGGGGG:");
              Serial.println(artiDon);
               
      digitalWrite(8,1);
      digitalWrite(3,0);
      analogWrite(5,255);
      digitalWrite(4,1);
      digitalWrite(7,0);
      analogWrite(6,artiDon);
      enson=2;
      
         }
         else if(stringOne.substring(2,3).toInt()==8){
            digitalWrite(8,1);
      digitalWrite(3,0);
      analogWrite(5,255);
      digitalWrite(4,1);
      digitalWrite(7,0);
      analogWrite(6,100);
         }
         else if(stringOne.substring(2,3).toInt()==9){
            digitalWrite(8,1);
      digitalWrite(3,0);
      analogWrite(5,255);
      digitalWrite(4,0);
      digitalWrite(7,0);
      analogWrite(6,0);
         }
          }
           else if(stringOne.substring(0,2).equals("90")){           
      if(stringOne.substring(2,3).toInt()<8){
        // hiz=90;
           //   hiz-=(stringOne.substring(2,3).toInt()*10);
              artiDon=255-stringOne.substring(2,3).toInt()*20;
              Serial.print("hiizArtiDonSooooool:");
              Serial.println(artiDon);
               
      digitalWrite(8,1);
      digitalWrite(3,0);
      analogWrite(5,artiDon);
      digitalWrite(4,1);
      digitalWrite(7,0);
      analogWrite(6,255);
      
      }
      
       else if(stringOne.substring(2,3).toInt()==8){
        digitalWrite(8,1);
      digitalWrite(3,0);
      analogWrite(5,100);
      digitalWrite(4,1);
      digitalWrite(7,0);
      analogWrite(6,255);
      }
      else if(stringOne.substring(2,3).toInt()==9){
        digitalWrite(8,0);
      digitalWrite(3,0);
      analogWrite(5,0);
      digitalWrite(4,1);
      digitalWrite(7,0);
      analogWrite(6,255);
      }
      
             
         
          }
          
       }
     
     }
     else if((stringOne.substring(0,3).equals("805")||stringOne.substring(3,6).equals("805"))){
     
     Serial.println("STOOOOOOOOOOOOOOOOOOOOPPPPPPPPPPPPPPPPPPPP");
               
      digitalWrite(8,0);
      digitalWrite(3,0);
      analogWrite(5,0);
      digitalWrite(4,0);
      digitalWrite(7,0);
      analogWrite(6,0);
     
     }
 /*    else if(stringOne.substring(0,3).equals("803")){
                     Serial.print("hiizArtiDonSooooool:");
              Serial.println(artiDon);
               
      digitalWrite(8,1);
      digitalWrite(3,0);
      analogWrite(5,255 );
      digitalWrite(4,1);
      digitalWrite(7,0);
      analogWrite(6,255);
      
     
     }
     else if(stringOne.substring(0,3).equals("804")){
                   Serial.print("hiizArtiDonSooooool:");
              Serial.println(artiDon);
               
      digitalWrite(8,0);
      digitalWrite(3,1);
      analogWrite(5,255);
      digitalWrite(4,0);
      digitalWrite(7,1);
      analogWrite(6,255);
      
     }
     
     /*else if(stringOne.substring(0,3).equals("803") && (stringOne.length()<4) ){
      Serial.println(stringOne);
       ileri= true;
        sag=false;
        sol=false;
        geri=false;
        dur=false;
        aDur=false;
        hiz=200;
      
     }*/
     /*else if(stringOne.substring(0,3).equals("804")){
      Serial.println(stringOne);
       ileri= false;
      sag=false;
      sol=false;
      geri=true;
      dur=false;
      aDur=false;
      hiz=200;
     }
     */
  //*********************************************************************************************************************************** 
    /*
    if(stringOne=="803")
    {
      ileri= true;
      sag=false;
      sol=false;
      geri=false;
      dur=false;
      aDur=false;
      hiz=0;
    }
    else if(stringOne=="801"){
      ileri= false;
      sag=true;
      sol=false;
      geri=false;
      dur=false;
      aDur=false;
    }
    else if(stringOne=="802"){
      ileri= false;
      sag=false;
      sol=true;
      geri=false;
      dur=false;
      aDur=false;
    }
    else if(stringOne=="804"){
      ileri= false;
      sag=false;
      sol=false;
      geri=true;
      dur=false;
      aDur=false;
    }
    else if(stringOne=="805"){
      ileri= false;
      sag=false;
      sol=false;
      geri=false;
      dur=true;
      aDur=false;
    }
    else if(stringOne=="808"){
      ileri= false;
      sag=false;
      sol=false;
      geri=false;
      dur=false;
      aDur=true;
    }
    else if(stringOne=="806"){
      digitalWrite(12, !digitalRead(12));
      delay(20);
    }
    else if(stringOne.toInt()<=255)
    {
      hiz=stringOne.toInt();
     // Serial.println(hiz);
    }
    */
   /* if(ileri==true&&geri==false&&sag==false&&sol==false&&dur==false){
      
      digitalWrite(8,1);
      digitalWrite(3,0);
      analogWrite(5,hiz);
      digitalWrite(4,1);
      digitalWrite(7,0);
      analogWrite(6,hiz);
      enson=0;
      sonDurum=true;
    }
    
    else if(ileri==false&&geri==true&&sag==false&&sol==false&&dur==false){
   
      digitalWrite(8,0);
      digitalWrite(3,1);
      analogWrite(5,hiz);
      digitalWrite(4,0);
      digitalWrite(7,1);
      analogWrite(6,hiz);
      enson=1;
      sonDurum=true;
    }
    
    else if(ileri==false&&geri==false&&sag==true&&sol==false&&dur==false){
      
      digitalWrite(8,1);
      digitalWrite(3,0);
      analogWrite(5,hiz);
      digitalWrite(4,0);
      digitalWrite(7,1);
      analogWrite(6,hiz);
      enson=2;
    }
    
    else if(ileri==false&&geri==false&&sag==false&&sol==true&&dur==false){
      
      digitalWrite(8,0);
      digitalWrite(3,1);
      analogWrite(5,hiz);
      digitalWrite(4,1);
      digitalWrite(7,0);
      analogWrite(6,hiz);
      enson=3;
    }
    else if(aDur==true){
      if(enson==0&&sonDurum==true){
      digitalWrite(8,0);
      digitalWrite(3,1);
      analogWrite(5,200);
      digitalWrite(4,0);
      digitalWrite(7,1);
      analogWrite(6,200);
      delay(50);
      sonDurum=false;
      }
      else if(enson==1&&sonDurum==true){
      digitalWrite(8,1);
      digitalWrite(3,0);
      analogWrite(5,200);
      digitalWrite(4,1);
      digitalWrite(7,0);
      analogWrite(6,200);
      delay(50);
      sonDurum=false;
      }
    }
    else if(dur==true){
      //Serial.println("durdaaaa");
      digitalWrite(8,0);
      digitalWrite(3,0);
      analogWrite(5,0);
      digitalWrite(4,0);
      digitalWrite(7,0);
      analogWrite(6,0);
    }*/
  }
}

