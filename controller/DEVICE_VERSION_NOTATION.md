# Device Version Notation
This document describes the programming language for ezHome devices, to be compiled and loaded on each device. 

# Modules
The main part of Device Version are the modules. Modules defines the electrical behavior and interactions between each
of his ports. All definition of a module has to be compatible with module configuration definition.

A version are a set of modules that have connections between then. 
An example of a module definition follows:

```
{
   "class": "br.com.ezhome.standard.DigitalInputModule",
   "name": "Kitchen outside door switch",
   "ports": [
      {
         "portType": "input",
         "phisicalAddress": 1
      },
      {
         "portType": "output",
         "connectionIndex": 1
      }
   ]
}
```

Above is a description of each parameter of the module.

## Class
The class attribute of a module is used by compiler to identify what module he are working on. All definition are loaded
based on class of a module.

The class is the full name of the class that defines him, as the example above:

```
"class": "br.com.ezhome.standard.modules.DigitalInputModule",
```

If the class specified on parameter is not registered a ClassNotFoundException will throws.

## Name
Name is the description of a module. This attribute is just for users to identify what module we are talking about.
The name can be any string and have no effect among the behavior of the module. An example follows:

```
"name": "Kitchen outside door switch",
```

## Ports
Ports are the inputs and outputs of a module. 

```
{
   "portType": "input",
   "phisicalAddress": 1
}
```

Ports are based on Port Types from Module Definition and can be of 2 types.

### PhisicalAddress
Are the ports connected to a phisical address of a device. The address specifies the phisical port of device. The definition
is simple and follows

```
"phisicalAddress": 1
```

Where "1" is the desired address.

### Connection
Connections, as the name says, are used to connect a module to anothers. On a module the connection is specified just by an
integer index. This is enough to create a new connection. 

```
"connectionIndex": 1
```

All module ports that use the same connectionIndex is considered connected with each other. But there's one restriction on this.

A connection can have only one input, from connection optics. From module optics, a connection INPUT is a module OUTPUT. 
This is necessary to preserve the logics of connections. If it was possible to connect two or more inputs on a single connection
the behavior of this connection will depend on the sequence of device logic cycle, something that is not controlled by the user. 

# Example
Here we have a example of a lightning controller.

```
{ 
   "modules": [ 
      {
         "class": "br.com.ezhome.standard.modules.DigitalInputModule",
         "name": "Kitchen switch",
         "ports": [
            {
               "portType": "input",
               "phisicalAddress": 0
            },
            {
               "portType": "output",
               "connectionIndex": 1
            }
         ]
      },
      {
         "class": "br.com.ezhome.standard.modules.DigitalOutputModule",
         "name": "Kitchen Light",
         "ports": [
            {
               "portType": "input",
               "connectionIndex": 3
            },
            {
               "portType": "output",
               "phisicalAddress": 3 
            },
         ]
      },
      {
         "class": "br.com.ezhome.standard.modules.LightningModule",
         "name": "Kitchen Light Controller",
         "ports": [
            {
               "portType": "input",
               "connectionIndex": 1
            },
            {
               "portType": "output",
               "connectionIndex": 3
            }
         ]
      }   
   ]
}
```