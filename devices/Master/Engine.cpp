#include "Engine.h"
#include <EEPROM.h>
#include <OneWire.h>
#include <DallasTemperature.h>


Engine::Engine(int eepromAddress, Program* program, Memory* memory, DeviceConfig* deviceConfig) {
  _eepromAddress = eepromAddress;
  _program = program;
  _memory = memory;
  _deviceConfig = deviceConfig;
  _errorCode = ERROR_NONE;
  _running = false;
  _preScan = false;
  _time = 0;
  _elapsed = 0;
}

boolean Engine::startEngine() {
  if(!_running) {
    preScan();
  }
  _running = true;
  writeToEeprom();
  return true;
}

boolean Engine::stopEngine() {
  _running = false;
  writeToEeprom();
  return true;
}

boolean Engine::getStatus() {
  return _running;
}

byte Engine::getErrorCode() {
  return _errorCode;
}

void Engine::preScan() {
  _time = millis();
  _elapsed = 0;
  processProgram(true);
}

void Engine::solveLogic() {
  unsigned long newTime = millis();
  if(newTime >= _time) {
    _elapsed = newTime - _time;
  }
  else {
    // rollover
    _elapsed = newTime;
  }
  _time = newTime;
  
  if(!_running) {
    return;
  }
  processProgram(false);
}

void Engine::processProgram(boolean asPreScan) {
  _preScan = asPreScan;
  if(_program->getProgramSize() <= OPCODES_INDEX) {
    // no program loaded
    return;
  }
  // solve the ladder logic, updating memory
  _bitsPerBooleanAddress = _program->getByte(BITS_PER_BOOL_ADDRESS_INDEX);
  _bitsPerNumericAddress = _program->getByte(BITS_PER_NUMERIC_ADDRESS_INDEX);
  _instructionPointer = OPCODES_INDEX;
  _loadedByte = 0;
  _bitsInLoadedByte = 0;
  _programDone = false;
  
  _rungCondition = true;
  while(!_programDone) { 
    if(processNextInstruction() == INSTRUCTION_SERIES_END) {
      _rungCondition = true; // start of new rung
    }
  }
}

byte Engine::processNextInstruction() { // returns instruction
  byte instruction = 0;

  if(_instructionPointer-OPCODES_INDEX > _program->getProgramSize()) {
    setError(ERROR_CODE_NEXT_INSTRUCTION_FAILED);
    Serial.println("Exced program size");
    _rungCondition = false;
    return instruction;
  }
  
  // start by getting 3 bits
  instruction = loadBitsIntoInstruction(instruction, 3);

  switch(instruction) {
    case 0x00: // Coil   000
      coil();
      return instruction; 
    case 0x01: // NO     001
      contactNO();
      return instruction; 
    case 0x02: // NC     010
      contactNC();
      return instruction; 
    case INSTRUCTION_SERIES_END: // SeriesEnd 011
      return instruction; 
  }
  
  // otherwise load another 2 bits (total of 5)
  instruction = loadBitsIntoInstruction(instruction, 2);
  
  switch(instruction) {
    case 0x10: // RisingEdge 10000
      risingEdge();
      return instruction;
    case 0x11: // FallingEdge 10001
      fallingEdge();
      return instruction;
    case 0x12: // SetReset   10010
      setReset();
      return instruction;
    case 0x13: // TmrON      10011
      tmrOn();
      return instruction;
    case 0x14: // TmrOFF     10100
      tmrOff();
      return instruction;
    case 0x15: // ParallelStart
      parallelStart(); // recursive call (parallelStart() will call processInstruction()) 10101
      return instruction; 
    case 0x16: // ParallelEnd 101110
      return instruction;
    case 0x17: // CntUP or CntDN (need to look at next bit) 10111 (101110 or 101111)
      instruction = loadBitsIntoInstruction(instruction, 1);
      if(instruction == 0x2E) {
        cntUp();
      }
      else {
        cntDn();
      }
      return instruction;
    case 0x18: // Comparison == 11000
      compare(equalTo);
      return instruction;
    case 0x19: // Comparison > 11001
      compare(greaterThan);
      return instruction;
    case 0x1A: // Comparison >= 11010
      compare(greaterThanOrEqual);
      return instruction;
    case 0x1B: // Comparison <  11011
      compare(lessThan);
      return instruction;
    case 0x1C: // Comparison <= 11100
      compare(lessThanOrEqual);
      return instruction;
    case 0x1D: // Comparison != 11101
      compare(notEqual);
      return instruction;
  }
  
  // otherwise load another 2 bits (total of 7)
  instruction = loadBitsIntoInstruction(instruction, 2);
  
  switch(instruction) {
    case 0x78: // Math + 1111000
      mathAdd();
      return instruction;
    case 0x79: // Math - 1111001
      mathSubtract();
      return instruction;
    case 0x7A: // Math * 1111010
      mathMultiply();
      return instruction;
    case 0x7B: // Math / 1111011
      mathDivide();
      return instruction;
    case 0x7C: // Math Choose # 1111100
      mathChooseNumber();
      return instruction;
    case 0x7D:
      // Monitor/Memory         11111101
      break;
    case 0x7E:
      // External devices       11111110
      int typ = loadBitsIntoInstruction(0, 3);
      switch(typ) {
        case 0x00:
          // DS18B20 sensor         000
          temperatureRead();
          return instruction;
        case 0x01:
          // Servo motors           001
          // not implemented yet
          break;
      }
      break;
    //case 0x7F:
      // not used (can conflict with final)
    //break;
      
      
  }
  
  // if we get this far, then the only valid possibility left is an end-of-program instruction
  instruction = loadBitsIntoInstruction(instruction, 1);

  if(instruction == 0xFF) {
    _programDone = true;
    return instruction;
  }

  setError(ERROR_CODE_NEXT_INSTRUCTION_FAILED);
  _rungCondition = false;
  return instruction;
}

byte Engine::loadBitsIntoInstruction(byte instruction, byte bitsToLoad) {
  byte result = instruction;
  for(int i = 0; i < bitsToLoad; i++) {
    result = result << 1;
    result = result | getNextBit();
  }
  return result;
}

byte Engine::getNextBit() { // returns 0 or 1
  if(_bitsInLoadedByte == 0) {
    _loadedByte = _program->getByte(_instructionPointer);
    _instructionPointer++;
    _bitsInLoadedByte = 8;
  }
  byte result = 0;
  if(_loadedByte & 128) {
    result = 1;
  }
  _loadedByte = _loadedByte << 1;
  _bitsInLoadedByte--;
  return result;
}

void Engine::setError(byte errorCode) {
  _errorCode = errorCode;
  _errorInstructionPointer = _instructionPointer;
  _programDone = true;
}

void Engine::coil() {
  int address = getBooleanAddress();
  boolean newValue = _rungCondition && !_preScan;
  byte monitor = getNextBit();
  boolean oldValue = _memory->readBoolean(address);
  _memory->writeBoolean(address, newValue);
  if (monitor == 1) {
    if (oldValue != newValue) {
      Serial.print(F("AS="));
      Serial.print(address);
      if (newValue) {
        Serial.println(F(":1"));
      } else {
        Serial.println(F(":0"));
      }
    }
  }
  byte eeprom = getNextBit();
  if (eeprom == 1) {
    int eepromAddress = loadBitsIntoInstruction(0, 8);
    if (oldValue != newValue) {
      // TODO Gravar na EEPROM no endereço
    }
  }
}

void Engine::contactNO() {
  boolean contactValue = getBooleanValue();
  _rungCondition = _rungCondition && contactValue;
}

void Engine::contactNC() {
  boolean contactValue = getBooleanValue();
  _rungCondition = _rungCondition && !contactValue;
}

void Engine::risingEdge() {
  int address = getBooleanAddress();
  boolean lastValue = _memory->readBoolean(address);
  boolean newValue = _rungCondition || _preScan; // set to prevent output triggering on first scan
  _memory->writeBoolean(address, newValue);
  _rungCondition = !lastValue && _rungCondition && !_preScan;
}

void Engine::fallingEdge() {
  int address = getBooleanAddress();
  boolean lastValue = _memory->readBoolean(address);
  boolean newValue = _rungCondition && !_preScan; // clear to prevent output triggering on first scan
  _memory->writeBoolean(address, newValue);
  _rungCondition = lastValue && !_rungCondition && !_preScan;
}

void Engine::setReset() {
  boolean reset = getBooleanValue();
  int address = getBooleanAddress();
  if(_preScan) {
    return; // don't want to modify memory
  }
  if(_rungCondition) 
  if(reset) {
    _memory->writeBoolean(address, false);
  }
  else {
    _memory->writeBoolean(address, true);
  }
}

void Engine::tmrOn() {
  tmr(true);
}

void Engine::tmrOff() {
  tmr(false);
}

void Engine::tmr(boolean onDelay) {  
  NumericMemoryValue setpointValue = getNumericValue();
  int doneAddress = getBooleanAddress();
  byte elapsedAddress = getNumericAddress();
  boolean done = _memory->readBoolean(doneAddress);
  
  boolean runCondition;
  boolean resetCondition;
  if(onDelay) {
    runCondition = !done && _rungCondition;
    resetCondition = !_rungCondition;
  }
  else {
    runCondition = done && !_rungCondition;
    resetCondition = _rungCondition;
  }
  
  if(_preScan) { // reset all timers during pre-scan
    runCondition = false;
    resetCondition = true;
  }
  
  NumericMemoryValue elapsedValue;
  if(runCondition) {
    // run timer
    elapsedValue = _memory->readNumeric(elapsedAddress);
    if(elapsedValue.isFloat) {
      elapsedValue.isFloat = false;
      elapsedValue.value.longValue = 0;
    }
    elapsedValue.value.longValue += (long)_elapsed; 
    _memory->writeNumeric(elapsedAddress, elapsedValue);
    if(!setpointValue.isFloat && elapsedValue.value.longValue >= setpointValue.value.longValue
        || setpointValue.isFloat && elapsedValue.value.longValue >= setpointValue.value.floatValue) {
      done = onDelay; // done: true if onDelay, false if offDelay
      _memory->writeBoolean(doneAddress, done); 
    }
  }
  else if(resetCondition) {
    // reset timer
    done = !onDelay; // write false if onDelay, true if offDelay
    _memory->writeBoolean(doneAddress, done); 
    elapsedValue.isFloat = false;
    elapsedValue.value.longValue = 0;
    _memory->writeNumeric(elapsedAddress, elapsedValue);
  }
  _rungCondition = done;
}

void Engine::temperatureRead() {  
  // Gets bus port
  int busPort = _deviceConfig->getPin(getBooleanAddress());
  // Reads Device Address
  
  DeviceAddress sensor;
  for (int i=0;i<8;i++) {
    sensor[i] = loadBitsIntoInstruction(0, 8);
  }
  // Gets delay between reads
  NumericMemoryValue delayValue = getNumericValue();
  // Gets elapsed time address
  byte elapsedAddress = getNumericAddress();
  // Get output value
  byte outputAddress = getNumericAddress();
  // Endereco para indicação de falha
  int faultPort = getBooleanAddress();
  
  boolean runCondition = _rungCondition;
  
  if(_preScan) { // reset all timers during pre-scan
    runCondition = false;
  }

  NumericMemoryValue elapsedValue;
  // validates delay
  elapsedValue = _memory->readNumeric(elapsedAddress);
  if(elapsedValue.isFloat) {
    elapsedValue.isFloat = false;
    elapsedValue.value.longValue = 0;
  }
  elapsedValue.value.longValue += (long)_elapsed; 
  _memory->writeNumeric(elapsedAddress, elapsedValue);
  if(runCondition) {
    _rungCondition = false;
    if(!delayValue.isFloat && elapsedValue.value.longValue >= delayValue.value.longValue
        || delayValue.isFloat && elapsedValue.value.longValue >= delayValue.value.floatValue) {
      // Perform a read
      OneWire oneWire(busPort);
      DallasTemperature sensors(&oneWire);
      sensors.begin();

      if (sensors.requestTemperaturesByAddress(sensor)) {
        float result = sensors.getTempC(sensor);
        NumericMemoryValue resultValue;
        resultValue.isFloat = true;
        resultValue.value.floatValue = result;
        _memory->writeNumeric(outputAddress, resultValue);
        _memory->writeBoolean(faultPort, false);
        _rungCondition = true;
      } else {
        // Not connected
        _memory->writeBoolean(faultPort, true);
      }
    }
  }
  //_rungCondition = done;
  
}

void Engine::parallelStart() {
  // save current rung condition
  boolean originalRungCondition = _rungCondition;
  boolean finalRungCondition = false;
  
  boolean branchEnd = false;
  while(!_programDone && !branchEnd) { 
    byte instruction = processNextInstruction();
    if(instruction == INSTRUCTION_SERIES_END) {
      finalRungCondition = finalRungCondition || _rungCondition;
      _rungCondition = originalRungCondition; // start of new rung
    }
    else if(instruction == INSTRUCTION_PARALLEL_END) {
      branchEnd = true;
    }
  }
  _rungCondition = finalRungCondition;
}

void Engine::cntUp() {
  cnt(true);
}

void Engine::cntDn() {
  cnt(false);
}

void Engine::cnt(boolean up) {
  NumericMemoryValue setpointValue = getNumericValue();
  long setpoint;
  if(setpointValue.isFloat) {
    setpoint = (long)(setpointValue.value.floatValue);
  }
  else {
    setpoint = setpointValue.value.longValue;
  }
  boolean reset = getBooleanValue();
  int doneAddress = getBooleanAddress();
  byte countAddress = getNumericAddress();
  int oneshotStateAddress = getBooleanAddress();
  boolean done = _memory->readBoolean(doneAddress);
  NumericMemoryValue countValue = _memory->readNumeric(countAddress);
  boolean oneshotState = _memory->readBoolean(oneshotStateAddress);
  
  if(_preScan) {
    _memory->writeBoolean(oneshotStateAddress, true); // want to avoid trigger on first scan if rung-in is true
    return;
  }
  
  boolean newRungOutCondition = done;
  countValue.isFloat = false;
  if(reset) {
    if(up) {
      countValue.value.longValue = 0;
    }
    else {
      countValue.value.longValue = setpoint;
    }
    newRungOutCondition = false;
  }
  else if(done) {
    // don't touch the count value
    // newRungOutCondition = done; // redundant
  }
  else if(_rungCondition && !oneshotState) { // rising edge
    if(up) {
      countValue.value.longValue += 1;
      if(countValue.value.longValue >= setpoint) {
        newRungOutCondition = true;
        countValue.value.longValue = setpoint;
      }
    }
    else {
      countValue.value.longValue -= 1;
      if(countValue.value.longValue <= 0) {
        newRungOutCondition = true;
        countValue.value.longValue = 0;
      }
    }
  }
  
  _memory->writeBoolean(oneshotStateAddress, _rungCondition); 
  _rungCondition = newRungOutCondition;
  _memory->writeBoolean(doneAddress, newRungOutCondition); 
  _memory->writeNumeric(countAddress, countValue);
}

void Engine::compare(comparison c) {
  NumericMemoryValue operand1Value = getNumericValue();
  NumericMemoryValue operand2Value = getNumericValue();
  if(!operand1Value.isFloat && !operand2Value.isFloat) {
    switch(c) {
      case equalTo:
        _rungCondition = operand1Value.value.longValue == operand2Value.value.longValue;
        break;
      case greaterThan:
        _rungCondition = operand1Value.value.longValue > operand2Value.value.longValue;
        break;
      case greaterThanOrEqual:
        _rungCondition = operand1Value.value.longValue >= operand2Value.value.longValue;
        break;
      case lessThan:
        _rungCondition = operand1Value.value.longValue < operand2Value.value.longValue;
        break;
      case lessThanOrEqual:
        _rungCondition = operand1Value.value.longValue <= operand2Value.value.longValue;
        break;
      case notEqual:
        _rungCondition = operand1Value.value.longValue != operand2Value.value.longValue;
        break;
    }
  }
  else if(operand1Value.isFloat && operand2Value.isFloat) {
    switch(c) {
      case equalTo:
        _rungCondition = operand1Value.value.floatValue == operand2Value.value.floatValue;
        break;
      case greaterThan:
        _rungCondition = operand1Value.value.floatValue > operand2Value.value.floatValue;
        break;
      case greaterThanOrEqual:
        _rungCondition = operand1Value.value.floatValue >= operand2Value.value.floatValue;
        break;
      case lessThan:
        _rungCondition = operand1Value.value.floatValue < operand2Value.value.floatValue;
        break;
      case lessThanOrEqual:
        _rungCondition = operand1Value.value.floatValue <= operand2Value.value.floatValue;
        break;
      case notEqual:
        _rungCondition = operand1Value.value.floatValue != operand2Value.value.floatValue;
        break;
    }
  }
  else if(operand1Value.isFloat && !operand2Value.isFloat) {
    switch(c) {
      case equalTo:
        _rungCondition = operand1Value.value.floatValue == operand2Value.value.longValue;
        break;
      case greaterThan:
        _rungCondition = operand1Value.value.floatValue > operand2Value.value.longValue;
        break;
      case greaterThanOrEqual:
        _rungCondition = operand1Value.value.floatValue >= operand2Value.value.longValue;
        break;
      case lessThan:
        _rungCondition = operand1Value.value.floatValue < operand2Value.value.longValue;
        break;
      case lessThanOrEqual:
        _rungCondition = operand1Value.value.floatValue <= operand2Value.value.longValue;
        break;
      case notEqual:
        _rungCondition = operand1Value.value.floatValue != operand2Value.value.longValue;
        break;
    }
  }
  else { // !operand1Value.isFloat && operand2Value.isFloat
    switch(c) {
      case equalTo:
        _rungCondition = operand1Value.value.longValue == operand2Value.value.floatValue;
        break;
      case greaterThan:
        _rungCondition = operand1Value.value.longValue > operand2Value.value.floatValue;
        break;
      case greaterThanOrEqual:
        _rungCondition = operand1Value.value.longValue >= operand2Value.value.floatValue;
        break;
      case lessThan:
        _rungCondition = operand1Value.value.longValue < operand2Value.value.floatValue;
        break;
      case lessThanOrEqual:
        _rungCondition = operand1Value.value.longValue <= operand2Value.value.floatValue;
        break;
      case notEqual:
        _rungCondition = operand1Value.value.longValue != operand2Value.value.floatValue;
        break;
    }
  }
}

void Engine::mathAdd() {
  NumericMemoryValue operand1Value = getNumericValue();
  NumericMemoryValue operand2Value = getNumericValue();
  NumericMemoryValue result;
  byte resultAddress = getNumericAddress();
  if(!_rungCondition) {
    return;
  }
  if(!operand1Value.isFloat && !operand2Value.isFloat) {
    result.isFloat = false;
    result.value.longValue = operand1Value.value.longValue + operand2Value.value.longValue;
  }
  else {
    result.isFloat = true;
    if(operand1Value.isFloat && operand2Value.isFloat) {
      result.value.floatValue = operand1Value.value.floatValue + operand2Value.value.floatValue;
    }
    else if(!operand1Value.isFloat && operand2Value.isFloat) {
      result.value.floatValue = operand1Value.value.longValue + operand2Value.value.floatValue;
    }
    else { // operand1Value.isFloat && !operand2Value.isFloat
      result.value.floatValue = operand1Value.value.floatValue + operand2Value.value.longValue;
    }
  }
  _memory->writeNumeric(resultAddress, result);
}

void Engine::mathSubtract() {
  NumericMemoryValue operand1Value = getNumericValue();
  NumericMemoryValue operand2Value = getNumericValue();
  NumericMemoryValue result;
  byte resultAddress = getNumericAddress();
  if(!_rungCondition) {
    return;
  }
  if(!operand1Value.isFloat && !operand2Value.isFloat) {
    result.isFloat = false;
    result.value.longValue = operand1Value.value.longValue - operand2Value.value.longValue;
  }
  else {
    result.isFloat = true;
    if(operand1Value.isFloat && operand2Value.isFloat) {
      result.value.floatValue = operand1Value.value.floatValue - operand2Value.value.floatValue;
    }
    else if(!operand1Value.isFloat && operand2Value.isFloat) {
      result.value.floatValue = operand1Value.value.longValue - operand2Value.value.floatValue;
    }
    else { // operand1Value.isFloat && !operand2Value.isFloat
      result.value.floatValue = operand1Value.value.floatValue - operand2Value.value.longValue;
    }
  }
  _memory->writeNumeric(resultAddress, result);
}

void Engine::mathMultiply() {
  NumericMemoryValue operand1Value = getNumericValue();
  NumericMemoryValue operand2Value = getNumericValue();
  NumericMemoryValue result;
  byte resultAddress = getNumericAddress();
  if(!_rungCondition) {
    return;
  }
  if(!operand1Value.isFloat && !operand2Value.isFloat) {
    result.isFloat = false;
    result.value.longValue = operand1Value.value.longValue * operand2Value.value.longValue;
  }
  else {
    result.isFloat = true;
    if(operand1Value.isFloat && operand2Value.isFloat) {
      result.value.floatValue = operand1Value.value.floatValue * operand2Value.value.floatValue;
    }
    else if(!operand1Value.isFloat && operand2Value.isFloat) {
      result.value.floatValue = operand1Value.value.longValue * operand2Value.value.floatValue;
    }
    else { // operand1Value.isFloat && !operand2Value.isFloat
      result.value.floatValue = operand1Value.value.floatValue * operand2Value.value.longValue;
    }
  }
  _memory->writeNumeric(resultAddress, result);
}

void Engine::mathDivide() {
  NumericMemoryValue operand1Value = getNumericValue();
  NumericMemoryValue operand2Value = getNumericValue();
  byte resultAddress = getNumericAddress();
  if(!_rungCondition) {
    return;
  }
  boolean divided = false;
  float temp = 0;
  if(!operand1Value.isFloat && !operand2Value.isFloat && operand2Value.value.longValue != 0) {
    temp = (float)(operand1Value.value.longValue) / (float)(operand2Value.value.longValue);
    divided = true;
  }
  else if(operand1Value.isFloat && operand2Value.isFloat && operand2Value.value.floatValue != 0) {
    temp = operand1Value.value.floatValue / operand2Value.value.floatValue;
    divided = true;
  }
  else if(!operand1Value.isFloat && operand2Value.isFloat && operand2Value.value.floatValue != 0) {
    temp = (float)(operand1Value.value.longValue) / operand2Value.value.floatValue;
    divided = true;
  }
  else if(operand1Value.isFloat && !operand2Value.isFloat && operand2Value.value.longValue != 0) {
    temp = operand1Value.value.floatValue / (float)(operand2Value.value.longValue);
    divided = true;
  }
  
  if(divided) {
    NumericMemoryValue result;
    if(temp == floor(temp)) { // convert back to integer format
      result.isFloat = false;
      result.value.longValue = temp;
    }
    else {
      result.isFloat = true;
      result.value.floatValue = temp;
    }
    _memory->writeNumeric(resultAddress, result);
  }
}

void Engine::mathChooseNumber() {
  NumericMemoryValue operand1Value = getNumericValue();
  NumericMemoryValue operand2Value = getNumericValue();
  byte resultAddress = getNumericAddress();
  if(_rungCondition) {
    _memory->writeNumeric(resultAddress, operand1Value);
  }
  else {
    _memory->writeNumeric(resultAddress, operand2Value);
  }
}

void printSensorAddress(DeviceAddress deviceAddress) {
  for (uint8_t i = 0; i < 8; i++) {
    // add zeros if necessary
    if (deviceAddress[i] < 16) Serial.print("0");
    Serial.print(deviceAddress[i], HEX);
  }
}


void Engine::scanTempSens(int port) {
  OneWire oneWire(port);
  DallasTemperature sensors(&oneWire);
  sensors.begin();
  Serial.print("Searching temperature sensors on port ");
  Serial.print(port);
  Serial.print(": ");
  Serial.print(sensors.getDeviceCount(), DEC);
  Serial.println(" found");
  for (int i=0; i < sensors.getDeviceCount(); i++) {
    DeviceAddress t;
    sensors.getAddress(t, i);
    printSensorAddress(t);
    Serial.println();
  }
  
}

boolean Engine::getBooleanValue() {
  byte flag = getNextBit();
  if(flag == 0) {
    // it's a constant
    return getNextBit() == 1;
  }
  else {
    // it's a signal, so get the address
    int address = getBooleanAddress();
    return _memory->readBoolean(address);
  }
}

int Engine::getBooleanAddress() {
  int result = 0;
  for(int i = 0; i < _bitsPerBooleanAddress; i++) {
    result = result << 1;
    result = result | getNextBit();
  }
  return result;
}

NumericMemoryValue Engine::getNumericValue() {
  byte flag = getNextBit();
  if(flag == 0) {
    // it's a constant
    NumericMemoryValue result;
    byte length = 0;
    if(getNextBit() == 1) {
      result.isFloat = true;
      length = 4;
    }
    else {
      result.isFloat = false;
      length = loadBitsIntoInstruction(0, 2) + 1;
    }
    result.value = loadBitsIntoNumericMemoryLocation(length * 8);
    return result;
  }
  else {
    // it's a signal, so get the address
    byte address = getNumericAddress();
    return _memory->readNumeric(address);
  }
}

byte Engine::getNumericAddress() {
  return loadBitsIntoInstruction(0, _bitsPerNumericAddress);
}

NumericMemoryLocation Engine::loadBitsIntoNumericMemoryLocation(byte bitsToLoad) {
  NumericMemoryLocation result;
  result.longValue = 0;
  for(int i = 0; i < bitsToLoad; i++) {
    result.longValue = result.longValue << 1;
    result.longValue = result.longValue | getNextBit();
  }
  return result;
}

void Engine::readFromEeprom() {
  int address = _eepromAddress;
  byte byteA = EEPROM.read(address++);
  byte byteB = EEPROM.read(address++);
  if(byteA != 0) {
    _running = false;
    return; // probably not initialized
  }
  if(byteB == 1) {
    _running = true;
  }
  else {
    _running = false;
  }
}

void Engine::writeToEeprom() {
  int address = _eepromAddress;
  EEPROM.write(address++, 0);
  if(_running) {
    EEPROM.write(address++, 1);
  }
  else {
    EEPROM.write(address++, 0);
  }
}
