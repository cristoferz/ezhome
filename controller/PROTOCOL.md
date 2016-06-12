# ezHome Controller Communication Protocol #

## Devices ##
### List ###
List devices and serial ports available to the controller. Connected devices 
will return runtimeId and versionId, as a way recognize the device independant 
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