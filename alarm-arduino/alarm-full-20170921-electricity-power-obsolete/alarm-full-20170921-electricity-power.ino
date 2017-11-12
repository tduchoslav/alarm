#include <Keypad.h>
#include <SimpleTimer.h>
#include <SoftwareSerial.h>

/*****************************************************************/
/***START OF SETTINGS CREDENTIALS ********************************/
/**Settings username, password to WIFI **/
//wifi connection

const String SSID_STR = "wifi-name";
const String PASSWORD_STR = "password";

const uint32_t BAUD_RATE = 9600;
//const uint32_t BAUD_RATE = 19200;
//const uint32_t BAUD_RATE = 38400;
//const uint32_t BAUD_RATE = 57600;
//const uint32_t BAUD_RATE = 74880;
//const uint32_t BAUD_RATE = 115200;
//const uint32_t BAUD_RATE = 230400;


/**Password for alarm ***/
const String CONST_PASSWORD = "A123";
/***END OF SETTINGS CREDENTIALS ********************************/
/***************************************************************/

String rightPassword = CONST_PASSWORD;
String inputPassword;


SoftwareSerial myEspSerial(10, 12);

const char* requestPattern =  "GET /alarm-server/%1s HTTP/1.1\r\nHost:%2s\r\nConnection:close\r\n\r\n";

char requestStringBuffer[90]; //buffer string for whole request
const int* port = 8080;

char* lastIp = "192.168.0.102";
char* possibleLastIpArray[6] = {"192.168.0.101", "192.168.0.102", "192.168.0.103", "192.168.0.104", "192.168.0.105", "192.168.0.106"}; //all potential last number of the IP address



const long ACTIVATION_TIMER_INTERVAL = 35000; //35 sec.
const long COUNTDOWN_ALARM_TIMER_INTERVAL = 30000; //30 sec.
const long RED_LED_OFF_TIMER_INTERVAL = 60000; //60 sec.
const long GREEN_LED_OFF_TIMER_INTERVAL = 65000; //60 sec.
//const long HEART_BEAT_TIMER_INTERVAL = 3600000;// 1 hour heartbeat
const long HEART_BEAT_TIMER_INTERVAL = 1200000;// 20 min. heartbeat
const long BLINK_GREEN_LED_TIMER_INTERVAL = 3600000; //1 hour.
const long ALARM_SERVER_STATUS_CHECKER_TIMER_INTERVAL = 1800000;// 30 min

byte redledpin = A4;
byte greenledpin = A5;

// the timer object
SimpleTimer simpleTimer;

//timer ids
int countdownTimerId;
bool isCountdownTimerRunning = false;
int activationDelayTimerId;
bool isActivationDelayTimerRunning = false;
int switchOffGreenLedTimerId;
int switchOffRedLedTimerId;
int heartBeatTimerId;
int blinkGreeLedTimerId;  //blink with green led. It is considered only when the alarm is activated.
int alarmServerStatusCheckerTimerId; // checks the server if it is not running.
bool isAlarmServerStatusCheckerTimerRunning = false;

//movement detector vars.
byte pirPin = 13; // Input for HC-S501, dtektor pohybu
byte pirValue; // Place to store read PIR Value

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

boolean isDetectorOn = false; //if alarm detector is switched on.
boolean isRedLedOn = false; // if red diod must be on.
boolean isGreenLedOn = false; // if green diod must be on.
boolean isGreenLedBlinking = false; // if green diod must be on.

boolean isMovementDetectedInfoSent = false; //to ensure that the detected movement info request is sent just once

boolean isOk = false;

void setup() {
  Serial.begin(BAUD_RATE);
  myEspSerial.begin(BAUD_RATE);
  
  Serial.println(F("Arduino setup starting..."));
    
  pinMode(pirPin, INPUT);//movement detector

  pinMode (redledpin, OUTPUT);
  pinMode (greenledpin, OUTPUT);

  //fixEsp();
  

  boolean isSent = generalHttpGetRequestForOperationRobust("test");
  if (!isSent) {
    //esp does not working, switch on red led, and finish with the program. Restart is necessary.
    switchOnRedLed();
    delay(10000);
    switchOffRedLed();
    delay(100);
    switchOnRedLed();
    delay(100);
    switchOffRedLed();
    delay(100);
    switchOnRedLed();
    delay(100);
    switchOffRedLed();
  } else {
    Serial.println(F("Wifi connection check successful!!!"));
    //blik led green diod
    switchOnGreenLed();
    switchOffGreenLed();
  }

  //check status of the alarm server
  delay(1000);
  
  if (httpGetRequestIsServerAlarmStatusOn()) {
      //ALARM is already running on the server, start alarm arduino immediately
      startAlarmRoutines();
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
        //strcat(inputPassword, key);
        Serial.print(F("input password: "));
        Serial.println(inputPassword);
      }
      
      //start timer countdown by movement detector.
      if (pirValue == 1) {
        isRedLedOn = true;
        setGreenLedOff();
        if (!isCountdownTimerRunning) {
            countdownTimerId = simpleTimer.setTimeout(COUNTDOWN_ALARM_TIMER_INTERVAL, runAlarm);
            Serial.println();Serial.print(F("Timer countdownTimerId ("));Serial.print(countdownTimerId);Serial.print(F(") started at "));Serial.print(millis()/1000);Serial.print(F(" sec."));
            pirValue = "0";
        } else {
            Serial.println();Serial.print(F("Timer countdownTimerId ("));Serial.print(countdownTimerId);Serial.print(F(") has been already started before."));
        }
        if (!isMovementDetectedInfoSent) {
          httpGetAlarmMovementDetectedImmediate();
          isMovementDetectedInfoSent = true;
        }
      }
      
      //stop countdown - deactivation
      if (rightPassword == inputPassword) {
      //if (strcmp(rightPassword, inputPassword) == 0) {
        Serial.print(F("inpuPassword equals to the correct password: "));
        Serial.println(inputPassword);
        simpleTimer.disable(countdownTimerId);
        Serial.println();Serial.print(F("DEACTIVATION: "));Serial.print(F(" Timer countdownTimerId (")); Serial.print(countdownTimerId); Serial.print(F(") has been disabled by given correct password at ")); Serial.print(millis()/1000);Serial.print(F(" sec."));
        
        inputPassword = "x"; //workaround in order to not execute the disabling all the time
        //inputPassword[0] = 0;
        isDetectorOn = false;
        isGreenLedOn = true;

        switchOffGreenLedTimerId = simpleTimer.setTimeout(GREEN_LED_OFF_TIMER_INTERVAL, setGreenLedOff); //set ot 60 sec.
        Serial.println();Serial.print(F("Timer switchOffLedTimerId ("));Serial.print(switchOffGreenLedTimerId);Serial.print(F(") started at "));;Serial.print(millis()/1000);Serial.print(F(" sec."));

        isRedLedOn = false;

        isMovementDetectedInfoSent = false;

        //stop alarm server monitoring
        httpGetAlarmStop();        
        
        //stop sending heartbeats
        simpleTimer.deleteTimer(heartBeatTimerId);
        simpleTimer.deleteTimer(blinkGreeLedTimerId);
      }
      if (key == '#') {
        //reset given password
        inputPassword = "";
        //inputPassword[0] = 0;
        Serial.println(F("inpuPassword reseted"));
      }
      //Serial.println(inputPassword);
      switchOffRedLed();
  } else {
      //check if the user set correct values from the keypad to activate detector...
      char key = keypad.getKey();
      //activate detector by pressing of the button asterisk '*'
      if (key == '*') {
        startAlarmRoutines();  
      }

      /*
       * start timer which calls httpGetRequestIsServerAlarmStatusOn() every 30 mins if the arduino alarm is off.
       * checks if server is not on already (e.g. could be started by manually on the server)
       */
      if (!isAlarmServerStatusCheckerTimerRunning) {
            alarmServerStatusCheckerTimerId = simpleTimer.setInterval(ALARM_SERVER_STATUS_CHECKER_TIMER_INTERVAL, checkAlarmServerStatus);
            Serial.println();Serial.print(F("Timer alarmServerStatusCheckerTimerId ("));Serial.print(alarmServerStatusCheckerTimerId);Serial.print(F(") started at "));Serial.print(millis()/1000);Serial.print(F(" sec."));
            isAlarmServerStatusCheckerTimerRunning = true;
      }
  }

  if (isGreenLedOn && isGreenLedBlinking) {
    switchOffGreenLed();
  }
  
  delay(50);
  
}

/**
 * Alarm has been activated, run timers etc.
 * It could be activated by presing '*', or if the arduino checks from the server that it must be running.
 */
void startAlarmRoutines() {
  if (!isActivationDelayTimerRunning) {
    activationDelayTimerId = simpleTimer.setTimeout(ACTIVATION_TIMER_INTERVAL, activateDetector);
    Serial.println();Serial.print(F("Timer activationDelayTimerId ("));Serial.print(activationDelayTimerId);Serial.print(F(") started at "));Serial.print(millis()/1000);Serial.print(F(" sec."));
    isActivationDelayTimerRunning = true;
    isGreenLedOn = true;
    isGreenLedBlinking = true;
    setRedLedOff();  
  } else {
    Serial.println();Serial.print(F("Timer activationDelayTimerId ("));Serial.print(activationDelayTimerId);Serial.print(F(") has been already started before."));
  }
}

/**
 * Runs alarm:
 *  - start beeping
 *  - send alarm warning to the alarm server
 */
void runAlarm() {
    Serial.println();
    Serial.print(F("Alarm starting..."));
    Serial.print(F("r Uptime (s): ")); 
    Serial.print(millis() / 1000);
    //Serial.println(millis());

    //inform alarm server to run alarm warning due to the movement detected!!!
    httpGetAlarmMovementDetected();
    
    switchOnRedLed();
    isDetectorOn = false;

    //switch off red led after some time
    switchOffRedLedTimerId = simpleTimer.setTimeout(RED_LED_OFF_TIMER_INTERVAL, setRedLedOff); //set ot 60 sec.
    Serial.println();Serial.print(F("Timer switchOffRedLedTimerId ("));Serial.print(switchOffRedLedTimerId);Serial.print(F(") started at "));;Serial.print(millis()/1000);Serial.print(F(" sec."));

    //stop sending heartbeats
    simpleTimer.deleteTimer(heartBeatTimerId);
    simpleTimer.deleteTimer(blinkGreeLedTimerId);

    isCountdownTimerRunning = false;
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
    isGreenLedOn = false;

    //sent info to start alarm server
    //Serial.println("Sending request to start Alarm server");
    httpGetAlarmStart();

    heartBeatTimerId = simpleTimer.setInterval(HEART_BEAT_TIMER_INTERVAL, sendHeartBeat); //set to 1 hour interval;
    blinkGreeLedTimerId = simpleTimer.setInterval(BLINK_GREEN_LED_TIMER_INTERVAL, setGreenLedOnOff); //set to 60 sec. interval;

    isActivationDelayTimerRunning = false;
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

void setGreenLedOnOff() {
  digitalWrite(greenledpin, HIGH);
  delay(500);
  digitalWrite(greenledpin, LOW);
}

/**
 * sends heart beats to the alarm server
 */
void sendHeartBeat() {
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
  //char *operation =  "alarmHeartBeatRest";
  char *operation =  "heartBeat";
  bool isSentHeartBeat = generalHttpGetRequestForOperationRobust(operation);
  if (!isSentHeartBeat) {
    //sent again, it must be sent!!! Try 2 times
    for (int i = 0; i < 2; i++) {
      delay(1000);
      isSentHeartBeat = generalHttpGetRequestForOperationRobust(operation);
      if (isSentHeartBeat) {
        //good job.
        break;
      }
    }
  }
}

/**
 * http get alarm status
 */
boolean httpGetRequestIsServerAlarmStatusOn() {
  bool returnedVal = false;
  delay(500);  
  String serverName = lastIp;
  char requestBuffer[90];
  requestBuffer[0] = 0; 
  sprintf(requestBuffer, requestPattern, "status", lastIp);  
  myEspSerial.println("AT+CIPSTART=\"TCP\",\"" + String(lastIp) + "\", 8080"); //start a TCP connection.
  if( myEspSerial.find("OK")) {
    Serial.println(F("TCP connection ready"));
  } 
  delay(100);
//  String sendCmd = "AT+CIPSEND=";//determine the number of characters to be sent.
  myEspSerial.print("AT+CIPSEND=");
  myEspSerial.println(strlen(requestBuffer));
  delay(500);
  if(myEspSerial.find(">")) { 
    Serial.println(F("Sending..")); 
    //myEspSerial.print(req);
    myEspSerial.print(requestBuffer);
    //if(myEspSerial.find("SEND OK")) { 
    if(myEspSerial.find("SEND OK") && myEspSerial.find("ALARM_ON")) { 
      returnedVal = true;
      Serial.println(F("Alarm server is already ON."));
    } else {
      //returndVal = true;//delete this
      Serial.println(F("Alarm server is OFF."));
    }
  } else {
    Serial.println(F("There is some problem, could not send request."));
  }
  myEspSerial.println("AT+CIPCLOSE");
  delay(100);
  Serial.println(F("Closed connection"));
  return returnedVal;
}

/**
 * Checks the status of the alarm and performs related actions if it is ON.
 */
void checkAlarmServerStatus() {
  if (httpGetRequestIsServerAlarmStatusOn()) {
      //switch off the timer alarmServerStatusCheckerTimerId, since the alarm is ON
      simpleTimer.deleteTimer(alarmServerStatusCheckerTimerId);  
      isAlarmServerStatusCheckerTimerRunning = false;
      startAlarmRoutines();
  }
}

/**
 * http get alarm start
 */
void httpGetAlarmStart() {  
  //char* request =  "GET /alarmws-lite-server/alarmEnableRest HTTP/1.1\r\nHost: 192.168.0.102\r\nConnection: close\r\n\r\n";
  //char *operation =  "alarmEnableRest";
  char *operation =  "enable";
  generalHttpGetRequestForOperationRobust(operation);
}
/**
 * http get alarm stop
 */
void httpGetAlarmStop() {
  //char* request =  "GET /alarmws-lite-server/alarmDisableRest HTTP/1.1\r\nHost: 192.168.0.102\r\nConnection: close\r\n\r\n";
  //char *operation =  "alarmDisableRest";
  char *operation =  "disable";
  generalHttpGetRequestForOperationRobust(operation);
}
/**
 * http get alarm movement detected.
 * This is used when the detector detected movement and the alarm has not been properly deactivated.
 */
void httpGetAlarmMovementDetected() {
  //char* request =  "GET /alarmws-lite-server/alarmMovementDetectedRest HTTP/1.1\r\nHost: 192.168.0.102\r\nConnection: close\r\n\r\n";
  //char *operation =  "alarmMovementDetectedRest";
  char *operation =  "movement";
  bool isSent = generalHttpGetRequestForOperationRobust(operation);
  if (!isSent) {
    //sent again, it must be sent!!! Try 5 times
    for (int i = 0; i < 5; i++) {
      delay(1000);
      isSent = generalHttpGetRequestForOperationRobust(operation);
      if (isSent) {
        //good job.
        break;
      }
    }
  }
}

/**
 * http get alarm movement detected immediately after the movement is detected.
 */
void httpGetAlarmMovementDetectedImmediate() {
  //char *operation =  "alarmMovementDetectedInfoRest";
  char *operation =  "movementInfo";
  generalHttpGetRequestForOperationRobust(operation);
}
/*
boolean generalHttpGetRequestForOperation(char* operation) {
  requestStringBuffer[0] = 0; 
  sprintf(requestStringBuffer, requestPattern, operation, lastIp);  
  Serial.print("prelozeny request:");
  Serial.println(requestStringBuffer);
  return sendHttpGetRequest(requestStringBuffer, lastIp, port);   
}*/

boolean generalHttpGetRequestForOperationRobust(char* operation) {
  requestStringBuffer[0] = 0; 
  sprintf(requestStringBuffer, requestPattern, operation, lastIp);  
  boolean isSent = sendHttpGetRequest(requestStringBuffer, lastIp, port);   
  if (!isSent) {
    //try to send to another IP
    for (int i = 0; i < sizeof(possibleLastIpArray)/2 ; i++) {
      //String newVal = possibleLastIpArray[i];
      requestStringBuffer[0] = 0;
      sprintf(requestStringBuffer, requestPattern, operation, possibleLastIpArray[i]);  
      /*    
      Serial.print(F("new request:"));
      Serial.println(requestStringBuffer);
      Serial.println(F("-----------------START--------------"));
      Serial.println(requestStringBuffer);
      Serial.println(newVal);
      //sprintf(currentServerIpStringBuffer, serverIpPattern, possibleLastIpArray[i]);
      //Serial.println(currentServerIpStringBuffer);
      Serial.println(port);
      Serial.println(F("-----------------END--------------"));
      */
      isSent = sendHttpGetRequest(requestStringBuffer, possibleLastIpArray[i], port);
      if (isSent) {
        lastIp = possibleLastIpArray[i];
        Serial.print(F("new server ID:"));
        Serial.println(lastIp);
        break;
      }
    }
    return isSent;
  }
}

/**
 * New method sends only to the one IP.
 * 1. connect to the wifi
 * 2. send request
 */
boolean sendHttpGetRequest(char* request, String serverIp, int port) {
  delay(500);

  //send request
  bool isSent = sendHttpGet(request, serverIp, port);

  //try to send once more
  if (!isSent) {
    isSent = sendHttpGet(request, serverIp, port);
  }
  
  if (isSent) {
    digitalWrite(greenledpin, HIGH);
    delay(1000);
    digitalWrite(greenledpin, LOW);
  } else {  
    digitalWrite(redledpin, HIGH);
    delay(1000);
    digitalWrite(redledpin, LOW);
  }
  delay(100);

  return isSent;
}

////set wifi mode
//void setWifiMode() {
//  myEspSerial.println("AT+CWMODE=1");
//  delay(100);
//  myEspSerial.println("AT+CIPMUX=0");
//  delay(100);
//}

//reset the esp8266 module
//void reset() {
//  myEspSerial.println("AT+RST");
//  delay(1000);
//  if(myEspSerial.find("OK")) {
//    Serial.println(F("Module Reset - OK"));
//  } else {
//    Serial.println(F("Module could not be reset!"));
//  }
//}

//connect to wifi network
//void connectWifi() {  
//  Serial.println(F("connecting to wifi..."));
//  String cmd = "AT+CWJAP=\"" + SSID_STR +"\",\"" + PASSWORD_STR + "\"";
//  myEspSerial.println(cmd);
//  delay(4000);//wait 4 sec.
//  /*
//  if(myEspSerial.find("OK")) {
//    Serial.println(F("Wifi Connected."));
//  } else {
//    Serial.println(F("Cannot connect to wifi!")); 
//  }
//  */
//}


/**
 * send http send
 */
boolean sendHttpGet (char* request, String serverName, int port) {
  Serial.println("!!!!!!!!!!START!!!!!!!!!!!!");
  Serial.print(F("request:"));
  Serial.println(request);  
  Serial.print(F("serverName:"));
  Serial.println(serverName);  
  Serial.print(F("port:"));
  Serial.println(port);  
  Serial.println("!!!!!!!!!!END!!!!!!!!!!!!");
  String cipStart="AT+CIPSTART=\"TCP\",\"" + serverName + "\"," + port;
  myEspSerial.println(cipStart);//start a TCP connection.
  delay(50);
  if( myEspSerial.find("OK")) {
    Serial.println(F("TCP connection ready"));
  } 
  delay(100);
  String sendCmd = "AT+CIPSEND=";//determine the number of caracters to be sent.
  myEspSerial.print(sendCmd);
  int requestLen = strlen(request);
  Serial.println(requestLen);
  myEspSerial.println(requestLen);
  delay(50);
  if(myEspSerial.find(">")) { 
    Serial.println(F("Sending..")); 
    myEspSerial.print(request);
    //delay(100);
    if(myEspSerial.find("SEND OK")) { 
      Serial.println(F("Packet sent"));
      Serial.println(F("The request successfully sent."));
      return true;
    }
  }
  // close the connection
  myEspSerial.println("AT+CIPCLOSE");
  Serial.println(F("The request could not be sent!"));
  return false;
}

