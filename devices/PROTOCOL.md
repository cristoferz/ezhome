# Devices Communication Protocol for ezHome Project
This document provides protocol definition for devices used on ezHome Project. 
This protocol will be used on communications via serial port between devices and ezHome Controller.

## Communication Standards
Messages of communication protocol have 2 types:
1. Send messages
1. Received messages

### Send Messages
These are the messages sended from controller to device. Every sended message waits for a response, 
confirming that sended command is executed.

They have 2 main formats:
* Normal messages: are the most common messages between both parts. 
Basically are a single line command preceded by a <End-Of-Line> character. Ex.:

       runtime-id 

* Programming messages: This special type of message is designed to be used only for device programation. 
This is necessary because of nature of sended data, as they are binary and cannot respect the format of a basic message. 
These messages are better described above.

### Received Messages
These are the messages sended from device to controller. There are 2 kinds of this messages:
* Response messages: this is a response to a sended message or command. 
As the messages can have many lines, every response message have a finish string. This finish string is a line with only **EOM**.

Example of response message:

    RuntimeId=1234567890876542
    EOM
    
* Status messages: this are the messages sended from device to controller to report an event that occurs on 
device that is not a response to a sended message. An example is a port state that can vary based on a light switch.
This kind of message are always single-lined, so there's no need of a finish string.

Example of status message:

    IS=1
    


## Query Functions
Query are operations used to query informations from device. Informations can be states of addresses, 
number of ports, runtimeId, version etc.

### Information query
Information returns data about loaded firmware, protocol version and available space on device to be 
used by the program.

Example of information request:

    information
    
Example of response:

    ezHome=0.1
    Protocol Version=1.0
    Booleans=1024
    Numerics=96
    Strings=0
    Max Program Size=3750
    Current Program Size=50
    EOM

This examples is a response from an Arduino Mega device. It is composed of several parts. They are:
* ezHome: indicates the firmwareVersion of ezHome device.
* Protocol Version: are the protocol version used by this device. 
Generally this are compatible with firmwareVersion
* Booleans: Number of boolean variables that can be used on this device
* Numerics: Number of numeric variables that can be used on this device
* String: Number of string variables that can be used on this device
* Max Program Size: returns that maximum program size that can be loaded on this device. 
It depends directly on EEPROM size of device.
* Current Program Size: return the size of current program loaded on device.
* EOM: the finish string

### Device Config query
Device config query returns the device model and actual port configurations for this device. 

Example of request:

    device-config
    
Partial example of response:
    
    Device Name=Arduino
    pin2, 0, output
    pin3, 1, output
    pin4, 2, input
    ...
    pin53, 51, input
    A0, 0, analogInput
    A1, 1, analogInput
    ...
    A15, 15, analogInput
    EOM

This is a response from an Arduino Mega device. This response is composed of following parts:
* Device Name: Returns the device name
* Digital ports: Current configuration for digital ports. Are composed of 3 parts
    1. pin**X**: identifies the phisical pin on arduino
    1. address: represents the address (integer) used to access the state of this port
    1. mode: current mode defined for this port. Can be *input*, *output* or *pwm*. 
Analog ports can be *analogInput* or *analogOutput*
* EOM: finish string

### Status query
Status query function returns if the device is running or paused.
Devices can paused their engine by command.

Example of request:

    status
    
Example of response:

    Running=true
    EOM
    
### RuntimeId query
RuntimeId is the unique identification number for a device. Is used by controller to know on what 
serial port each device is connected and to control what versions can be used.
This information is also included on startup message.

Example of request:

    runtime-id
    
Example of response:

    RuntimeId=0123456789ABCDEFFEDCBA9876543210
    EOM
    
### VersionId query
VersionId is the unique identification number for a program version loaded into a device. 
This can assures that a expected version of program is loaded on device. Every program version
must create a new unique versionId.

Example of request:

    version-id
    
Example of response:

    RuntimeVersionId=0123456789ABCDEFFEDCBA9876543210
    EOM
    
### Read Boolean
Reads boolean state for given address on device. Address can be a port address or memory addresses. 

Example of request:

    read b 5
    
Example of response:

    1
    EOM
    
This response indicates that the address 5, which in this case is port 3, has a high level.

### Read Numeric
No numeric implementations are ready yet.

## Command Functions
Command functions are used to interact with device, changing states of addresses, port modes etc.

### Factory Reset
Factory reset is a master reset function for device. It cleans program and resets modes of all ports.
It also cleans RuntimeId and VersionId, so device loses its references.

Example of request:

    factory-reset
    
Example of response:
    
    Success=true
    EOM
    
### Config Reset
Config reset restore all ports to their initial states. 
Initial states of digital ports are Input mode and on analog are analogInput mode. 
Config-reset is actually the only way to change a port from output mode to input mode.

Example of request:

    config-reset
    
Example of response:

    Success=true
    EOM
    
### Config Output
Config output command defines a port as output. This configuration is writed on EEPROM, 
so it can only be changed by config-reset command.

Example of request:

    config-output 2
    
Example of response:

    Success=true
    EOM
    
This request configures phisical pin 2 on arduino to be an output port.

### Config PWM
Config PWM command defines a port as PWM Output. 

Example of request:

    config-pwm 4
    
Example of success response:

    Success=true
    EOM
    
Example of error response:
    
    Error=Not a valid PWM pin: 4
    EOM
    
As not all ports from a device can be configured as a PWM output an error can be throw if a invalid
port is given.    

### Start
Start command controls the running state of device engine. As the name suggests, it start the engine 
processing and the logics of program will be applied. Ports will respect the logics of the program.

Example of request:

    start
    
Example of response:
   
    Success=true
    EOM
    
### Stop
Stop command controls the running state of device engine. As the name suggests, it stops the engine 
processing and the logics of program will not be applied. All outputs are put on LOW state.

Example of request:

    stop
    
Example of response:
   
    Success=true
    EOM
    
### Write Boolean
Write boolean command function set a boolean value to specified address. 

Example of request:

    write b 2 1
    
Example of response:

    EOM
    
This request sets the boolean value of address 2 (in this case is pin 4 of arduino) to be at high level.     

Ps.: *This function cannot assure that this value will be preserved, just sets the current state. 
This could be overwriten by program engine on the next running cycle so realy works on 
address that are designed to be externally seted.*

### Write Numeric
No numeric implementations are ready yet.

## Program Functions
Program functions are specifically designed for program upload to device. There are 2 types of program
function:
* Download: Sends program to device, stoping engine. This function stops the engine at download beginning and starts at 
the end, so the ports can switch states during proccess
* Patch: Sends program to device, without stoping engine. This function download the new program without 
stoping the engine, so there's a risk of anomalous behaviors on big program changes.

### Program Function Steps
As the program function are more complex, their are composed of 2 pairs of request/response, as described
above:
1. Header message: informs to device the size of the program that will be send. This prepared the device
to receive full program;

Example of request:
    
    download 50
    
Response:

    Success=true
    Bytes=50
    EOM
    
This confirms that 50 bytes will be sended.

2. Body message: Transmits the program itself. As the total bytes is defined by header message the device
reads the amount of bytes specified putting directly into program memory. At the end the program is writen
on device EEPROM. 

The format used to send the program to device is based on [JSON Ladder format](JSON_LADDER.md) and its convertion to binary format is better described [here](JSON_LADDER_BINARY_CONVERSION.md).

Response:

    Success=true
    Bytes=50
    EOM
    
This confirms that the 50 bytes specified by program is received and writed on device's EEPROM.             
 