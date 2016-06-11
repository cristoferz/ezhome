#include "IO.h"

IO::IO(DeviceConfig* deviceConfig, Memory* memory) {
  _deviceConfig = deviceConfig;
  _memory = memory;
}

void IO::configureIO() {
  for(int pin = MIN_PIN_NUMBER; pin <= MAX_PIN_NUMBER; pin++) {
    if(_deviceConfig->isOutput(pin) || _deviceConfig->isPwm(pin)) {
      pinMode(pin, OUTPUT);
    }
    else {
      // Enable internal pull up resistor
      pinMode(pin, INPUT_PULLUP);
    }
  }
}

void IO::printIO() {
  for(int pin = MIN_PIN_NUMBER; pin <= MAX_PIN_NUMBER; pin++) {
    if(pin != STATUS_LED_PIN) {
      printPinConfig(pin);
    }
  }
  for(int analog = MIN_ANALOG_INPUT; analog <= MAX_ANALOG_INPUT; analog++) {
    int address = analog - MIN_ANALOG_INPUT;
    Serial.print(F("A"));
    Serial.print(analog);
    Serial.print(F(", "));
    Serial.print(address);
    Serial.println(F(", analogInput"));
  }
  for(int pwmPin = MIN_PIN_NUMBER; pwmPin <= MAX_PIN_NUMBER; pwmPin++) {
    if(_deviceConfig->isPwm(pwmPin)) {
      byte pwmAddress = _deviceConfig->getPwmAddress(pwmPin);
      Serial.print(F("pin"));
      Serial.print(pwmPin);
      Serial.print(F(", "));
      Serial.print(pwmAddress);
      Serial.println(F(", analogOutput"));
    }
  }
}

void IO::printPinConfig(int pin) {
  // format for each line is: name, address, type
  // Type is: input, output, analogInput, analogOutput
  
  int address = _deviceConfig->getAddress(pin);
  
  Serial.print(F("pin"));
  Serial.print(pin);
  Serial.print(F(", "));
  Serial.print(address);
  Serial.print(F(", "));
  if(_deviceConfig->isOutput(pin)) {
    Serial.print(F("output"));
  }
  else if(_deviceConfig->isInput(pin)) {
    Serial.print(F("input"));
  }
  Serial.println();
}

void IO::scanInputs() {
  for(int pin = MIN_PIN_NUMBER; pin <= MAX_PIN_NUMBER; pin++) {
    if(pin != STATUS_LED_PIN && _deviceConfig->isInput(pin)) {
      int address = _deviceConfig->getAddress(pin);
      boolean oldValue = _memory->readBoolean(address);
      if(digitalRead(pin) == HIGH) {
        _memory->writeBoolean(address, true);
        if (!oldValue) {// && address != 2 && address != 40 && address != 41 && address != 42) {
          Serial.print(F("IS=")); 
          Serial.print(address);
          Serial.println(F(":1"));
        }
      }
      else {
        _memory->writeBoolean(address, false);
        if (oldValue){// && address != 2 && address != 40 && address != 41 && address != 42) {
          Serial.print(F("IS="));
          Serial.print(address);
          Serial.println(F(":0"));
        }
        
      }
      
    }
  }
  for(int analogPin = MIN_ANALOG_INPUT; analogPin <= MAX_ANALOG_INPUT; analogPin++) {
    byte address = analogPin - MIN_ANALOG_INPUT;
    int value = analogRead(analogPin);
    NumericMemoryValue memoryValue;
    memoryValue.isFloat = false;
    memoryValue.value.longValue = value;
    _memory->writeNumeric(address, memoryValue);
  }
}

void IO::scanOutputs() {
  for(int pin = MIN_PIN_NUMBER; pin <= MAX_PIN_NUMBER; pin++) {
    if(pin != STATUS_LED_PIN) {
      if(_deviceConfig->isOutput(pin)) {
        int address = _deviceConfig->getAddress(pin);
        boolean value = _memory->readBoolean(address);
        boolean oldValue = (digitalRead(pin) == HIGH);
        if(value) {
          digitalWrite(pin, HIGH);
          if (!oldValue) {
            Serial.print(F("OS="));
            Serial.print(address);
            Serial.println(F(":1"));
          }
        }
        else {
          digitalWrite(pin, LOW);
          if (oldValue) {
            Serial.print(F("OS="));
            Serial.print(address);
            Serial.println(F(":0"));
          }
        }
      }
      else if(_deviceConfig->isPwm(pin)) {
        byte pwmAddress = _deviceConfig->getPwmAddress(pin);
        NumericMemoryValue pwmMemoryValue = _memory->readNumeric(pwmAddress);
        long outputValue;
        if(pwmMemoryValue.isFloat) {
          outputValue = (long)(pwmMemoryValue.value.floatValue);
        }
        else {
          outputValue = pwmMemoryValue.value.longValue;
        }
        byte byteOutputValue;
        if(outputValue > 255) {
          byteOutputValue = 255;
        }
        else if(outputValue < 0) {
          byteOutputValue = 0;
        }
        else {
          byteOutputValue = outputValue;
        }
        analogWrite(pin, byteOutputValue);
      }
    }
  }
}

void IO::turnOutputsOff() {
  for(int pin = MIN_PIN_NUMBER; pin <= MAX_PIN_NUMBER; pin++) {
    if(pin != STATUS_LED_PIN) {
      if(_deviceConfig->isOutput(pin)) {
        digitalWrite(pin, LOW);
      }
      else if(_deviceConfig->isPwm(pin)) {
        analogWrite(pin, 0);
      }
    }
  }
}
