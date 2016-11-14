## Parts of device versions

### Version
Version is a set of modules and connections that defines the behavior of a device. Also a version is specific for a device type,
such as Arduino MEGA or UNO. This is needed because of phisical differences between then. 

### Modules
Modules is basically a group of inputs and output that defines a logic of interactions between then. This inputs and outputs
can be phisical or a connection with another module as specified above.

### Connections
As the name says, connections is the connection between different modules or different port of the same module. As a rule, 
every connection receives one, and only one, input that is always an output from a module. A connection can have multiple outputs,
that is always used as input from a module, either another or the same as the input of connection.

### Types of inputs/outputs (I/O)
There is three types of inputs/outputs that can be used in ezHome Devices:
* DIGITAL: Accepts only 2 possible values high and low, or translating to logics, is a boolean I/O;
* ANALOGIC: Is a numeric I/O, oscilating their voltage phisically and numeric value logically;
* PWM: Phisically is also a digital port, but logically is a numeric I/O. This specific type can only be used in phisical ports, 
and not on connections because they are processed by devices.

Connections also have the same type as I/Os. This is necessary because only ports of the same type can be connected. Only a 
module can "transform" an I/O from one type to another.