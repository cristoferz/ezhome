# JSON Ladder
JSON Ladder is the base language used to program ezHome Project compatible devices 
with the logics implemented on ezHome Project. Is basically a translation of the 
Ladder Language to a JSON format that is used for store configurations on local and remote databases.
Above is a basic example of implementation:

    { "program":
        [ { "serie": [
                { "Parallel": [
                { "serie": [
                    { "NO": 10 },
                    { "Parallel": [
                        { "serie": [
                            { "FallingEdge": 100 }
                        ] },
                        { "serie": [
                            { "RisingEdge": 101 }
                        ] }
                    ] }
                ] }
                ] },
                { "SetReset": { "address": 4, "reset": 4} }
            ] },
            { "serie": [
                { "Parallel": [
                { "serie": [
                    { "NO": 11 },
                    { "Parallel": [
                        { "serie": [
                            { "FallingEdge": 104 }
                        ] },
                        { "serie": [
                            { "RisingEdge": 105 }
                        ] }
                    ] }
                ] },
                { "serie": [
                    { "NO": 5 },
                    { "FallingEdge": 106 }
                ] }
                ] },
                { "SetReset": { "address": 2, "reset": 2} }
            ] },
        ] }


It is composed by the following parts:

## Flow tags
Flow tags are the tags used to define the interactions between the instructions on ladder engine. They do not perform any tasks on devices outputs, but define the flow of rung conditions between instructions.

## Program
The base of JSON Ladder. Its used only once on a JSON Ladder program and just define the root of the logics. Its similar to the "parallel" instructions discussed later on purpose, but is the only tag that can be used on program root.

Contains an array of series to be executed by JSON Engine.

     { "program": [ 
        { "serie": [ ... ] },
        { "serie": [ ... ] }
      ]}

## Serie
Represents a serie of instructions processed in a serial form on JSON Engine. 
Its represented by an array of instructions to processed in a serial form, each one passing a *rungCondition* to the next one and using this value on its own logics.

    { "serie": [
       { ... },
       { ... }
    ]}

## Instructions
Unlike the flow tags, instructions are intended for interactions with memory addresses, device ports or simply to interact with rungCondition to the next instruction.

Basically, every instruction receives a rungCondition and passes a new rungCondition to the next instruction. The simplest instructions are NO and NC, discussed later.

### Contact NO and NC
Contact NO and NC are the simplest instructions with input on a Ladder logics. As the ladder borns based on eletrical relays, they are representation of an eletrical switch.
NO represents a "Normally Open" switch and NC represents a "Normally Closed" switch. So the only difference between then are the inverted results, so "NO = !NC" is a good representation. 

On JSON Ladder, both contacts need an "interaction value", and this can be represented with a "boolean value".

Boolean values can be a constant value, like "true" or "false" or a representing address. As on a device an memory address can represent a phisical port or just a memory address,
Boolean Values can be used for phisical or memory interactions, allowing great flexibility to the instruction.

The constant form of a boolean value is represented by a boolean value on JSON format, as following examples:

    { "NO": true }
    { "NC": false }

Is not very logical to use constant values for contacts as the example, but boolean values are used for others purposes so its important to know that is possible.

Other way to use the boolean values are assigning to then a memory address that can be a phisical port or just a memory value. This can be done by passing the integer value of desired address, like following:

    { "NO": 0 }
    { "NC": 108 }

This assigns address 0 (normally phisical port 2) and 108 (just a memory address) to contacts.

The main objective of contact instructions are they rungCondition result. They are always based on 2 values:
* Incoming rungCondition: or the rungCondition that comes from prior instruction
* Their input value: as represented by assigned address or constant value

This way, we can represent the resulting rungCondition for NO as:

    <Incoming rungCondition> && <Input value>

And for NC as:

    <Incoming rungCondition> && !<Input value>

As you can see, the only difference is the invertion of the Input value. Incoming rungCondition always affect the resulting rungCondition the same way.



### Coil
Represents an Output on Ladder logics.

Basically, a Coil instruction reflects the incoming rungCondition on their Output Address, that can be a phisical output or memory value. The resulting rungCondition is exactly the same value as input rungCondition.
Normally Coil is the final instruction for a serie, but its not mandatory.

On JSON Ladder, Coil representation is like following:

    { "Coil": 1 }

As an coil always sets the value of their output destination, necessarily only one coil can be assigned to each address. This is not a restriction. If more than one coil are assigned to the same address, 
the last one on the engine flow will override their value before it reflect a phisical response. Probabily an unexpected behavior.

### Parallel
Represents a *fork* on ladder logics, allowing parallel series to be executed and resulting *rungCondition* being united in an "or" logics.

    { "Parallel": [
       { "serie": [ ... ] },
       { "serie": [ ... ] }
    ]} 

In a "program" tag, resulting rungCondition is just discarded and next serie is initialized with a "true" value, not affecting the next serie. 
On a Parallel instruction, all inner series resulting rungCondition are combined on a "or logics" like the following:

    serie1 | serie2 | serie3 | ...

Resulting rungCondition is passed to the next instruction on the outer serie. Is possible to use Parallel instructions on as many inner levels as necessary.       

Instructions are the interactions maded by Ladder logics.

### Edges
Detects an edge on signal by the preceding logics. It detects an variation on signal from high to low (FallingEge) or low to high (RisingEdge), switching by only one cycle. No mather what signal is before an edge, the edges only pass a high signal just one cicle when the expected edge is detected, turning back to low signal.

An address has to be associated with an edge. It is responsible for the control of edge detection across the cycles of the controller.

FallingEdge representation:

    -----[-_]-----

FallingEdge on JSON Ladder:

    { "FallingEdge": 1 }

FallingEdge on Java:

    new FallingEdge(builder, getReservedAddress(builder, ++addressIndex))
    
RisingEdge representation:

    -----[_-]-----

RisingEdge on JSON Ladder:

    { "RisingEdge": 1 }    

RisingEdge on Java:

    new RisingEdge(builder, getReservedAddress(builder, ++addressIndex))

Ps.: *The address associated with the edges is used just for store the previous state of signal. Never could be used for other operations, with the risk of anomalous behaviors.*