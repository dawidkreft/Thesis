
// ---------------------------- Library ----------------------------------------------
#include <MPU6050.h>    
#include <I2Cdev.h>     

// ---------------------------- Viariables  ------------------------------------
MPU6050 accelgyro;        
int16_t ax, ay, az , deltaAz , prevAz ;   
int16_t gx, gy, gz;   
int8_t threshold, count;                                                                
bool zero_detect; 
bool TurnOnZI = false;
#define OUTPUT_READABLE_ACCELGYRO


void setup() {
  
   #if I2CDEV_IMPLEMENTATION == I2CDEV_ARDUINO_WIRE     
        Wire.begin();
   #elif I2CDEV_IMPLEMENTATION == I2CDEV_BUILTIN_FASTWIRE
        Fastwire::setup(400, true);
   #endif
   
   Serial.begin(9600);     
                               
    accelgyro.initialize(); 
    accelgyro.setAccelerometerPowerOnDelay(3);
    accelgyro.setIntZeroMotionEnabled(TurnOnZI);
    accelgyro.setDHPFMode(1);
    accelgyro.setMotionDetectionThreshold(2);
    accelgyro.setZeroMotionDetectionThreshold(2);
    accelgyro.setMotionDetectionDuration(40);
    accelgyro.setZeroMotionDetectionDuration(1);  

   deltaAz = 0;
   prevAz = 0;
}

void loop() {
   if(accelgyro.testConnection()){       // check connection with MPU6050
      zero_detect = accelgyro.getIntMotionStatus();
      threshold = accelgyro.getZeroMotionDetectionThreshold(); 
      
      int result = 0;               
      for(int i = 0 ; i <10 ; i++){  
        accelgyro.getMotion6(&ax, &ay, &az, &gx, &gy, &gz); 
        result = result + az;
        delay(20);
      }
      result = result/10;        
      result = result/1000;      
    
      if(prevAz != result){                        
        deltaAz = (prevAz-result);  
        Serial.println(deltaAz);   
        prevAz=result;
      }
      else{
        Serial.println(prevAz);     
     }
     delay(100);
  }
}
