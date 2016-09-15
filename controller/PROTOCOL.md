# ezHome Controller Communication Protocol #
## Activation and Login functions
### Validate activation
Returns activation state for this controller

    GET    /activation/validate

### Activate device
Activates controller with remote site

    POST   /activation/activate

### Login
Generates a token for access

    POST   /login/token              

----
## Serial ports
### List ports
Lists all serial ports available to controller

    GET    /config/serialPort?connected=<value>&portName=<value>

Params:

| Name | Required | Values | Description |
|------|----------|--------|-------------|
| connected | false | Boolean | Filter only ports on specified state |
| portName | false | String | Filter only specified port |

Response:

    {
        "serialPorts": [
            {
                "connected": true,
                "versionId": "0000000000000000",
                "currentOwner": "self",
                "portName": "/dev/ttyUSB0",
                "runtimeId": "0123456789ABCDEFFEDCBA9876543210",
                "deviceId": 1
            },
            {
                "connected": false,
                "portName": "/dev/ttyUSB0"
            }
        ]
    }

### Connect
Try to connect an ezHome device on port

    POST   /config/serialPort        

    { "portName": "/dev/ttyUSB0" }

Response:

    { "success": true }
    
### Disconnect   
Disconnect ezHome device on port, if available

    DELETE /config/serialPort        

### Scan
Makes a manual scan on serialPorts

    POST   /config/serialPort/scan    

### Enable/disable Auto-Scan
Enable/Disable autoscan on serial ports

    POST   /config/serialPort/autoScan 

----
## Device
### List devices
Lists configured devices

    GET    /config/device            

### Insert a new device
Insert a new device, creating a new UID

    POST   /config/device            

### Updates a device
Update device data, keeping UID

    PUT    /config/device            

### Delete a device
Inactivate device

    DELETE /config/device            

### Send command
Sends a command to device

    POST   /config/device/send

### Initializes device
Initializes device, burning firmware, creating a new UID for device and cleaning EEPROM.

    POST   /config/device/initialize 

### Upgrade firmware version
Upgrades firmware version keeping UID for device

    POST   /config/device/upgrade

### Upload program do device
Sends a program version to device

    POST   /config/device/upload     

### Config-reset
Sends a program version to device

    POST   /config/device/upload     

### Factory-reset
Resets all configs and program from device
    
    POST   /config/device/factory-reset

### Resets device
Resets device

    POST   /config/device/reset

### Pause
Pause the device logics and disable all outputs

    POST   /config/device/pause

### Resume
Resume the device logics

    POST   /config/device/resume     

### Information
Reads device information

    GET    /config/device/information

### Device-config
Reads device config

    GET    /config/device/device-config

### Read Boolean
Reads a boolean value from device

    GET    /config/device/readBoolean 

### List Models
Lists all available models of device

    GET    /config/device/model      

Response:

    {
      "models": [
         {
            "name": "Arduino MEGA 2560",
            "model_id": 2,
            "model_cod": "MEGA",
            "thumbnail": "/images/arduino_mega.jpg"
         },
         {
            "name": "Arduino NANO",
            "model_id": 3,
            "model_cod": "NANO",
            "thumbnail": "/images/arduino_nano.jpg"
         }
      ]
    }    

----
## Program
### List Programs
Lists program versions available and where they are applied

    GET    /config/device/program

### Insert Program
Creates a new version of program

    POST   /config/device/program

### Delete Program
Deletes a program version

    DELETE /config/device/program









## Devices ##
### List ###
List devices and serial ports available to the controller. Connected devices 
will return runtimeId and versionId, as a way to recognize the device independant 
of phisical port connected.  

#### Request ####
    GET /device/list
    
#### Response ####
    {
        "devices": [
            {
                "connected": true,
                "versionId": "0000000000000000",
                "currentOwner": "self",
                "name": "/dev/ttyUSB0",
                "runtimeId": "0123456789ABCDEFFEDCBA9876543210"
            },
            {
                "connected": false,
                "name": "/dev/ttyUSB0"
            }
        ]
    }

-------
### Connect ###
Connect to a device on specified serial port by **portName** parameter. 

#### Request ####
    POST /device/connect
    
    {
        "portName": "/dev/ttyUSB0"
    }
    
#### Response ####
On successfull connection:

    {
        "success": true
    }

On connection failure:

    {
        "success": false,
        "message": "gnu.io.NoSuchPortException"
    }
    
-------    
### Disconnect ###
Disconnect from device specified by **portName** parameter.

#### Request ####
    POST /device/disconnect
    
    {
        "portName": "/dev/ttyUSB0"
    }
    
#### Response ####
On successfull connection:

    {
        "success": true
    }

On connection failure:

    {
        "success": false,
        "message": "Invalid port"
    }   
    
-------
### Upload ###
Upload firmware for specified **portName**. If there is a valid
device on port, his firmware will be updated, otherwise is just
burned the new firmware.

Current configs loaded to device are preserved on proccess.

#### Request ####
    POST /device/upload
    
    { 
        "portName": "/dev/ttyUSB0",
        "model": "MEGA"
    }

#### Response ####
    {
        "result": "\navrdude: AVR device initialized and ready to accept instructions\navrdude: Device signature = 0x1e9801\navrdude: reading input file \"/tmp/ezHome-2103596859514401669.hex\"\navrdude: writing flash (20666 bytes):\navrdude: 20666 bytes of flash written\navrdude: verifying flash memory against /tmp/ezHome-2103596859514401669.hex:\navrdude: load data flash data from input file /tmp/ezHome-2103596859514401669.hex:\navrdude: input file /tmp/ezHome-2103596859514401669.hex contains 20666 bytes\navrdude: reading on-chip flash data:\navrdude: verifying ...\navrdude: 20666 bytes of flash verified\n\navrdude done.  Thank you.\n\n",
        "success": true
    }
    
------
### Send ###
Sends a **command** to especified **portName** and gets the result.

#### Request ####
    POST /device/send
    
    { 
        "portName": "/dev/ttyUSB0",
        "command": "runtime-id"
    }
    
#### Response ####
    {
       "success": true,
       "result": "RuntimeId=00000000000000000000000000000000"
    }
    
------    