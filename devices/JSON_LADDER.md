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

As an coil always sets the value of their output destination, necessarily only one coil can be 
assigned to each address. This is not a restriction. If more than one coil are assigned to the 
same address, the last one on the engine flow will override their value before it reflect a 
phisical response. Probabily an unexpected behavior.

### Parallel
Parallel is a instruction, but can also be seen as an flow tag. 
It represents a *fork* on ladder logics, allowing parallel series to be executed inside a serie
and resulting *rungCondition* being united in an "or" logics.

    { "Parallel": [
       { "serie": [ ... ] },
       { "serie": [ ... ] }
    ]} 

In a "program" tag, resulting rungCondition is just discarded and next serie is initialized
with a "true" value, not affecting the next serie. On a Parallel instruction, all inner 
series resulting rungCondition are combined on a "or logics" like the following:

    serie1 | serie2 | serie3 | ...

Resulting rungCondition is passed to the next instruction on the outer serie. Following is an example of 2 Contact NO interacting with 1 Coil, using a parallel:

    { "serie": [
       { "Parallel": [
          { "serie": [
             { "NO": 1 }
          ]},
          { "serie": [
             { "NO": 2 }
          ]}
       ] },
       { "Coil": 3 }
    } 

In this example, any of the NO that have an high level, will put a high level on the Coil. 
This is because the "OR" logics of the Parallel instruction.

The series on the Parallel are the same as the series on a Program, so its possible to do anything on then, as incluing Parallel inside another Parallel. 
Is possible to use Parallel instructions on as many inner levels as necessary.       

### Edges
The edge type of instructions are the type of instruction that detects an edge on signal 
represented by rungCondition. There are 2 types of edges: Rising and Falling. They detect an 
variation on signal from high to low (FallingEge) or low to high (RisingEdge). 

The effect of such variation is a "true" value on preceding rungCondition for just on engine cycle. This is 
different from NO and NC contacts. They keep the value for as long as the signal is high or low. The edges 
just but a high level to represent the variation of the signal an just for one cycle. 
No mather what signal is before an edge, the edges only pass a high signal just one cicle when 
the expected edge is detected, turning back to low signal.

The edges have no directly output. They unique purpose is to set the preceding rungCondition as the edge is detected. 
So, they will never appear alone in a serie. There is no restriction to this, but its meaningless to do this.

The edges needs, for their internal logics, a boolean memory address. In this case, the memory address cannot be a
phisical port from device. There is no restriction to do this, but this will cause an unexpected behavior, as the port 
will interact with the internal address of the edge. The purpose of this address is to "save" the "old state" of signal
across engine cycles. This is necessary because the edge always compares the current state with the "old state", and this 
have to be saved across engine cycles.

With this is mind, there is no use for the edge address except for the edge itself.

Examples of both edge representation are the following:

    { "FallingEdge": 100 }
    { "RisingEdge": 101 }

A practical example is explore after we understand the SetReset instruction.

### SetReset
SetReset instruction are maded for, as the name says, set or reset the value of a memory address
of device. We can understand set as put "true" on this address and reset as put "false" on this 
address. Its a single instruction because its possible to define their state at runtime, inverting 
their result.

This instruction easily work in conjuction with edges, but there is no restriction to the use with others 
instructions. This instruction take 2 parameters:
* address: The output address. That the address that will be set or reset;
* reset: A boolean value that represents if the instruction will set or reset the output;

As the reset parameter is a boolean value, it can be a constant value (true or false) or a memory address
containing the expected behavior. 

An example of usage is as following:

    { "SetReset": {
       "address": 0,
       "reset": true
    }}

Or:

   { "SetReset": {
      "address": 0,
      "reset": 100
   }}    

This represents the both forms of SetReset instruction. First one use the constant form, where the value
of address 0 is always set to false when a rungCondition of true precedes the instuction. On the second form,
the value of true or false depends on value of the memory address 100. The value of the address is inverted 
accordingly to the name "reset" so, if the value of address is true (reset), the value setted is false and if
the value is false (not reset), the value setted it true.

A very useful case of use of the runtime reset value is a "auto reversing configuration". An example is as 
following:

    { "SetReset": {
       "address": 0,
       "reset": 0
    }} 

As the same address is used for both output and reset values, for every cycle that rungCondition is true the 
value of address 0 is inverted, passing from true to false or false to true. This is very useful if combined 
with an edge instruction, as the example above:

    { "serie": [
       { "NO": 1 },
       { "FallingEdge": 100 },
       { "SetReset": {
          "address": 0,
          "reset": 0
       }}
    ]} 

Consider that a push button is connected to address 1 and a LED is connected to address 0. With this configuration
for every click on the button, the light turn off or turn on and keeps this state until the next interaction with 
the button.

 
     