#include <Keypad.h>
#include <SimpleTimer.h>
//#include <ESP8266.h>
#include <MyESP8266Mini.h>

//wifi connection
const char *SSID     = "YYYY";
const char *PASSWORD = "XXXX";
SoftwareSerial mySerial(10, 12); //SoftwareSerial pins for MEGA/Uno. For other boards see: https://www.arduino.cc/en/Reference/SoftwareSerial
//ESP8266 wifi(mySerial);
MyESP8266Mini wifi(mySerial);
const char *reqPart1 = "GET /alarmws-lite-server/";
const char *reqPart2 = " HTTP/1.1\r\nHost: 192.168.0.102\r\nConnection: close\r\n\r\n";
const char *requestPattern =  "GET /alarmws-lite-server/%s HTTP/1.1\r\nHost: 192.168.0.102\r\nConnection: close\r\n\r\n";
char requestStringBuffer[120]; //buffer string for
const char *serverName = "192.168.0.102";
const int  port = 8080;

const long ACTIVATION_TIMER_INTERVAL = 35000; //35 sec.
//const long ACTIVATION_TIMER_INTERVAL = 3000; //35 sec.
const long COUNTDOWN_ALARM_TIMER_INTERVAL = 30000; //30 sec.
const long RED_LED_OFF_TIMER_INTERVAL = 60000; //60 sec.
const long GREEN_LED_OFF_TIMER_INTERVAL = 65000; //60 sec.
const long HEART_BEAT_TIMER_INTERVAL = 36000;// 1 hour heartbeat
//password
const String CONST_PASSWORD = "A123";

byte redledpin = A4;
byte greenledpin = A5;

// the timer object
SimpleTimer simpleTimer;

//timer ids
int countdownTimerId;
int activationDelayTimerId;
int switchOffGreenLedTimerId;
int switchOffRedLedTimerId;
int heartBeatTimerId;

//movement detector vars.
byte pirPin = 11; // Input for HC-S501, dtektor pohybu
byte pirValue; // Place to store read PIR Value

const byte buzzer = 13; //buzzer

/**
 * Variables, constants for keypad
*/
const byte ROWS = 4; //four rows
const byte COLS = 4; //four columns
char keys[ROWS][COLS] = {
  {'1','2','3','A'},
  {'4','5','6','B'},
  {'7','8','9','C'},
  {'*','0','#','D'}
};
byte rowPins[ROWS] = {5, 4, 3, 2}; //connect to the row pinouts of the keypad
byte colPins[COLS] = {9, 8, 7, 6}; //connect to the column pinouts of the keypad
Keypad keypad = Keypad( makeKeymap(keys), rowPins, colPins, ROWS, COLS );

String rightPassword = CONST_PASSWORD;
String inputPassword;

boolean isDetectorOn = false; //if alarm detector is switched on.
boolean isRedLedOn = false; // if red diod must be on.
boolean isGreenLedOn = false; // if green diod must be on.
boolean isGreenLedBlinking = false; // if green diod must be on.

boolean isOk = false;


void setup() {
  Serial.begin(57600);

  pinMode(buzzer, OUTPUT);
    
  pinMode(pirPin, INPUT);//movement detector

  pinMode (redledpin, OUTPUT);
  pinMode (greenledpin, OUTPUT);

  //connect to the wifi  
  while ( isOk != true) {
    if (!wifi.init(SSID, PASSWORD))
    {
      Serial.println(F("Wifi Init failed. Check configuration. Alarm not started!!!"));
      //TODO bzucak bzuci neustale!!!!
      delay(3000);
    } else {
      isOk = true;
    }  
  }

}

void loop() {
  simpleTimer.run();
  
  if (isRedLedOn) {
      switchOnRedLed();
  }
  if (isGreenLedOn) {
      switchOnGreenLed();
  }
  if (isDetectorOn) {
      pirValue = digitalRead(pirPin);
      //check pressed key and add to the password
      char key = keypad.getKey();  
      if (key) {
        inputPassword += key;
      }
      
      //start timer countdown by movement detector.
      if (pirValue == 1) {
        isRedLedOn = true;
        setGreenLedOff();
        if (!simpleTimer.isEnabled(countdownTimerId)) {
            countdownTimerId = simpleTimer.setTimeout(COUNTDOWN_ALARM_TIMER_INTERVAL, runAlarm);
            Serial.println();Serial.print(F("Timer countdownTimerId ("));Serial.print(countdownTimerId);Serial.print(F(") started at "));Serial.print(millis()/1000);Serial.print(F(" sec."));
            pirValue = "0";
        } else {
            Serial.println();Serial.print(F("Timer countdownTimerId ("));Serial.print(countdownTimerId);Serial.print(F(") has been already started before."));
        }
      }
      
      //stop countdown - deactivation
      if (rightPassword == inputPassword) {
        simpleTimer.disable(countdownTimerId);
        Serial.println();Serial.print(F("DEACTIVATION: "));Serial.print(F(" Timer countdownTimerId (")); Serial.print(countdownTimerId); Serial.print(F(") has been disabled by given correct password at ")); Serial.print(millis()/1000);Serial.print(F(" sec."));
        
        inputPassword = "x"; //workaround in order to not execute the disabling all the time
        isDetectorOn = false;
        isGreenLedOn = true;

        switchOffGreenLedTimerId = simpleTimer.setTimeout(GREEN_LED_OFF_TIMER_INTERVAL, switchOffGreenLed); //set ot 60 sec.
        Serial.println();Serial.print(F("Timer switchOffLedTimerId ("));Serial.print(switchOffGreenLedTimerId);Serial.print(F(") started at "));;Serial.print(millis()/1000);Serial.print(F(" sec."));

        isRedLedOn = false;

        //stop alarm server monitoring
        httpGetAlarmStop();        
        
        //stop sending heartbeats
        simpleTimer.deleteTimer(heartBeatTimerId);
      }
      if (key == '#') {
        //reset given password
        inputPassword = "";
      }
      //Serial.println(inputPassword);
      switchOffRedLed();
  } else {
      //check if the user set correct values from the keypad to activate detector...
      char key = keypad.getKey();
      //activate detector by pressing of the button asterisk '*'
      if (key == '*') {
          if (!simpleTimer.isEnabled(activationDelayTimerId)) {
              activationDelayTimerId = simpleTimer.setTimeout(ACTIVATION_TIMER_INTERVAL, activateDetector);
              Serial.println();Serial.print(F("Timer activationDelayTimerId ("));Serial.print(activationDelayTimerId);Serial.print(F(") started at "));Serial.print(millis()/1000);Serial.print(F(" sec."));
              isGreenLedOn = true;
              isGreenLedBlinking = true;
              setRedLedOff();  
          } else {
              Serial.println();Serial.print(F("Timer activationDelayTimerId ("));Serial.print(activationDelayTimerId);Serial.print(F(") has been already started before."));
          }
          
      }
  }

  if (isGreenLedOn && isGreenLedBlinking) {
    switchOffGreenLed();
  }
  
  delay(50);
}

/**
 * Runs alarm:
 *  - start beeping
 *  - send alarm warning to the alarm server
 */
void runAlarm() {
    //Serial.println();
    //Serial.print("Alarm starting...");
    //Serial.print("r Uptime (s): "); 
    //Serial.print(millis() / 1000);
    //Serial.println(millis());

    //inform alarm server to run alarm warning due to the movement detected!!!
    httpGetAlarmMovementDetected();
    
    switchOnRedLed();
    //run for 10 seconds
    for (int i = 0; i < 10; i++) {
      tone(buzzer, 1000); // Send 1KHz sound signal...
      delay(1000);        // ...for 1 sec
      noTone(buzzer);     // Stop sound...
      delay(1000);        // ...for 1sec
    }
    isDetectorOn = false;

    //switch off red led after some time
    switchOffRedLedTimerId = simpleTimer.setTimeout(RED_LED_OFF_TIMER_INTERVAL, setRedLedOff); //set ot 60 sec.
    Serial.println();Serial.print(F("Timer switchOffRedLedTimerId ("));Serial.print(switchOffRedLedTimerId);Serial.print(F(") started at "));;Serial.print(millis()/1000);Serial.print(F(" sec."));
}

/**
 * Activate alarm machine
 */
void activateDetector() {
    //Serial.println();
    //Serial.print("Activated Alarm starting by user at ");
    //Serial.print(millis()/1000);
    //Serial.println(" sec. Any movement being caught by movement detector will trigger alarm.");
    isDetectorOn = true;
    isGreenLedBlinking = false;

    //sent info to start alarm server
    //Serial.println("Sending request to start Alarm server");
    httpGetAlarmStart();

    heartBeatTimerId = simpleTimer.setInterval(HEART_BEAT_TIMER_INTERVAL, sendHeartBeat); //set to 1 hour interval;
}

/**
 * Switch on Led diod  with red color
 */
void switchOnRedLed() {
  digitalWrite(redledpin, HIGH);
  delay(100);
}

/**
 * Switch off Led diod  with red color
 */
void switchOffRedLed() {
  digitalWrite(redledpin, LOW);
  delay(50);
}
/**
 * Switch on Led diod  with green color
 */
void switchOnGreenLed() {
  digitalWrite(greenledpin, HIGH);
  delay(100);
}

/**
 * Switch off Led diod  with green color
 */
void switchOffGreenLed() {
  digitalWrite(greenledpin, LOW);
  delay(50);
}

/**
 * set boolean red dion to false
 */
void setRedLedOff() {
  isRedLedOn = false;
  switchOffRedLed();
}

void setGreenLedOff() {
  isGreenLedOn = false;
  isGreenLedBlinking = false;
  switchOffGreenLed();
}

/**
 * sends heart beats to the alarm server
 */
void sendHeartBeat() {
  //TODO
  httpGetAlarmHeartBeat();
}

/***************************************************************
**** HTTP functions calls alarm server etc.
*****************************************************************/
/**
 * http get alarm heart beat
 */
void httpGetAlarmHeartBeat() {
  //char* request =  "GET /alarmws-lite-server/alarmHeartBeatRest HTTP/1.1\r\nHost: 192.168.0.102\r\nConnection: close\r\n\r\n";
  char *operation =  "alarmHeartBeatRest";
  requestStringBuffer[0] = 0;
  sprintf(requestStringBuffer, requestPattern, operation);
  wifi.httpGet(requestStringBuffer, serverName, port);
}

/**
 * http get alarm start
 */
void httpGetAlarmStart() {  
  //char* request =  "GET /alarmws-lite-server/alarmEnableRest HTTP/1.1\r\nHost: 192.168.0.102\r\nConnection: close\r\n\r\n";
  char *operation =  "alarmEnableRest";
  requestStringBuffer[0] = 0;
  sprintf(requestStringBuffer, requestPattern, operation);
  wifi.httpGet(requestStringBuffer, serverName, port);
}
/**
 * http get alarm stop
 */
void httpGetAlarmStop() {
  //char* request =  "GET /alarmws-lite-server/alarmDisableRest HTTP/1.1\r\nHost: 192.168.0.102\r\nConnection: close\r\n\r\n";
  char *operation =  "alarmDisableRest";
  requestStringBuffer[0] = 0; 
  sprintf(requestStringBuffer, requestPattern, operation);
  wifi.httpGet(requestStringBuffer, serverName, port);
}
/**
 * http get alarm movement detected
 */
void httpGetAlarmMovementDetected() {
  //char* request =  "GET /alarmws-lite-server/alarmMovementDetectedRest HTTP/1.1\r\nHost: 192.168.0.102\r\nConnection: close\r\n\r\n";
  char *operation =  "alarmMovementDetectedRest";
  requestStringBuffer[0] = 0;
  sprintf(requestStringBuffer, requestPattern, operation);
  wifi.httpGet(requestStringBuffer, serverName, port);
}


