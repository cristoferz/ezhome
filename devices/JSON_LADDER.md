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

### NumericValues
At this point is necessary for complete understanding of the following instructions that we known a new structure:
the NumericValues. Until now, we are using just boolean values, either constants or not, but never more than this.
For the next instructions are necessary to understand that is possible to use NumericValues.

NumericValues, as the same says, are basically numbers, but we have some differences on the address system. A numeric 
address of value 18 is not the same address as a boolean address of value 18. This is because the address system for
numeric values are completely isolated for boolean addresses. This happens because of natures of values and phisical
ports of devices. Boolean address are linked to digital ports of device and Numeric address are linked to analog ports
of device. As an example, lets take the Arduino MEGA 2560. They have 54 digital ports and 16 analog ports, that are 
completely different ports. So, the boolean addresses from 0 to 52 (thats because ports 0 and 1 are useless) are linked
to the digital ports. And, the numeric addresses from 0 to 15 are linked to analog ports. The preceding addresses are 
just memory addresses, having no phisical interaction.

NumericValue have 2 forms of representation:
* Constant: having static value like above:

 
    { "value": 200 }

* Variable: assigned to a numeric memory address like above:


     { "address": 18 }

Once assigned to an analog port the value will reflect in the analog value for the port.

### Timers
Timers are the time based instructions for the JSON Ladder. The logics are based on 3 values, specified on definition:
* setpointValue: specifies the time in milliseconds elapsed for a timer be considered done.
* doneAddress: a boolean address that represents the done state for the timer.
* elapsedAddress: a numeric address to store the elapsed time for the timer.

With the same variables, a timer can be one of 2 types:
* TimerOn 
   
   Every time the preceding rungCondition is true the timer start counting time, until achieve the setpointValue. During 
   this time, doneAddress keeps a false value, elapsedAddress keep the time passed until the true condition and resulting
   rungCondition return false. Once the setpointValue is achieved, doneAddress gains a true value and rungCondition returns 
   true.

* TimerOff

   Every time the preceding rungCondition is true the timer start counting time, until achieve the setpointValue. During 
   this time, doneAddress keeps a true value, elapsedAddress keep the time passed until the false condition and resulting
   rungCondition return true. Once the setpointValue is achieved, doneAddress gains a false value and rungCondition returns 
   false.

If the running condition for the timer (true for TimerOn and false for TimerOff) changes, the timer resets immediatelly and 
elapsedTime returns to zero. 

An example of usage of TimerOn is as following:

    { "serie": [
       { "NO": 0 },
       { "TimerOn": {
          "setpointValue": 2000,
          "doneAddress": 1,
          "elapsedAddress": 18
       }},
       { "Coil": 2 }
    ]}
     
In this example, the contact NO commands the execution of Timer, as their control the preceding rungCondition. The Timer is 
configured for a 2s delay (2000ms), with doneAddress configured for address 1 and elapsedAddress configured for numeric 
address 18. A Coil assigned to address 2 is connected to the resulting rungCondition.

The logic is interpreted as follows:
Once the address 0 has a high value the timer will start running. During 2s the rungCondition reflects no alteration. 
If during this time the address 0 goes to an low value, the timer resets and the output will never change. Otherwise, if 
passes the 2 seconds and address 0 keeps the high value, then doneAddress will change to true and resulting rungCondition too. 
This stays like this for as long as the address 0 keeps the high value. Once the change to low value, immediatelly doneAddress 
and resulting rungCondition goes low too, no delay is applied this way.

TimerOff have a similar aproach, but with inverted values. Timer starts with a low value and reset with a high value.

Some considerations about parameters of timers:
* setpointValue can use both constants and variable values. A change during the timer execution will affect the result of timer 
without reseting him.
* doneAddress and resulting rungCondition always have the same value. Because of internal logics of the timer is necessary to 
specify an address, but is not necessary to use him.
* elapsedAddress can be used for reading, but take care with writings. A write on this address can cause unexpected behaviors. 
A mistake easy to take is try to use an address of an analog port. This will cause very strange results, and dificulty to find 
the problem. That because every cycle of engine will override the value if the analog value of the phisical port, and or the 
timer never ends, or ends very fast, depending on values of setpointValue. Anyway, of course this will not be the expected result.

### Counters
Counters are the most complex structures for now. Counters are instructions that uses the preceding rungCondition edge for autoincrement 
or autodecrement their values, according to their type (either count up or down). Their basic implementation is examplified above:

    { "Counter": {
       "countDown": false,
       "setpointValue": {
          "value": 10
       },
       "resetValue": 100,
       "doneAddress": 101,
       "countAddress": 18,
       "oneshotStateAddress": 102
    }} 

With this basics, this are the explanations about all the parameters:
* countDown: sets the timer as a count up or down. Its always a constant value so, its not possible to change at runtime.
* setpointValue: defines the value that the counter is considered "done". Explanations about what is "done" are maded above.
* resetValue: defines the boolean value that resets the counter. Can be a constant or variable
* doneAddress: boolean address that identify a counter as done. Similar to Timers this also reflects the resulting rungCondition
* countAddress: numeric address that stores the current count value.
* oneshotStateAddress: as the counters act like an edge, this is the address for the internal edge control

One simple explanation for counters is that they are an edge with a math add operation. But they do a little more than this. They 
controls a setpointValue, doneAddress and a reset operation in only one instruction. Lets explain this by parts. First of all the 
doneAddress and rungCondition. For our first example we have a count up counter (countDown: false), with a setpoint of 10. Lets put 
more instructions to be more clear:

    { "serie": [
       { "NO": 0 },
       { "Counter": {
          "countDown": false,
          "setpointValue": {
             "value": 10
          },
          "resetValue": 100,
          "doneAddress": 101,
          "countAddress": 18,
          "oneshotStateAddress": 102
       }},
       { "Coil": 1 }
    ]}  

In this example lets think we have a push button on address 0 and a LED on address 1.

At the start of program, we have the LED turn off, and the counter at 0. So, if we click 5 times on the button, nothing realy 
happens, just the internal counter goes to 5 and the LED still off. If click more 5 times, than the counter goes to 10 and 
setpointValue is hit so the rungCondition goes to true together with doneAddress and finally the LED turn on.

If at any time you read the countAddress you will have the counter value, so now we have the value of 10.

But, our LED is now turn on and there is no way to turn it off. That because differently from timers, counters do not 
autoreset at any condition. The only way to autoreset a counter is will a pulse on resetValue. If you keep the resetValue 
constantly on true, the counter will reset every cycle and never autoincrement their values.

If will implement another serie with the example above, together with our first example, will can reset your counter:

    { "serie": [
       { "NO": 2 },
       { "Coil": 100 }
    ]}

With a button connected to address 2, a click on it will reset the counter. while you keep the button pushed, the counter
never increment, they keep reseting.

### Math Operators
The counters are very useful for lots of situations, but their use is very specific as a counter. 
Sometimes is necessary to make some math operations with values. For this kind of situations there is the "MathOperation"
instruction. There 5 kind of math operations implemented by this instruction, they are (with their representation):
* ADD: +
* SUBTRACT: -
* MULTIPLY: *
* DIVIDE: /
* CHOOSE: #

They are self explanatory with the exception of the CHOOSE one. They are bettter explained above.

An example of MathOperation follows:

    { "MathOperation": {
       "type": "*",
       "operator1": {
          "address": 0
       },
       "operator2": {
          "value": 2
       },
       "resultAddress": {
          "address": 18
       }
    }}

This example shows a MathOperation with the MULTIPLY type. The result is that the value read from address 0, on operator1, 
is multiplied by 2, as the operator2 and the result is stored on numeric address 18. So the parameters explanation follows:

* type: the operator for the math operation. Can be one of: +,-,*,/,#
* operator1: a NumericValue for the first operator
* operator2: a NumericValue for the second operator
* resultAddress: a NumericAddress to store the operation result.

Math operations can use analog inputs, counters variables, timers elapsedAddress or any others numeric values as their 
operators. Is possible to use the same address on operators and resultAddress at the same instruction. One example is an
autoincrement on a numeric address, as follows:

    { "MathOperation": {
       "type": "+",
       "operator1": {
          "address": 18
       },
       "operator2": {
          "value": 1
       },
       "resultAddress": {
          "address": 18
       }
    }} 

This instruction will increment on the numeric value of address 18 every time the preceding rungCondition is true. 
But take care. The operation is done every cycle that the rungCondition is true, so if you put a NO contact before and
the user clicks the button, lots of cycle will passes during the high value of the button, incrementing the value lots 
of times. To prevent this, its common to use edges before the MathOperation, asuring that only one cycle will perform 
the operation per click.

A not self explanatory MathOperation is the CHOOSE type. But is a very simple one. Basically they choose the value to 
put on resulting address based on preceding rungCondition. If the rungCondition is false, then the operator1 value is 
putted on resultAddress, if true the value of operator2 is putted. Is a little different from the others because a edge 
will not serve well as a preceding instruction in most cases. 

The last thing to consider is the resulting rungCondition. They are not affected by MathOperation. So, if the preceding 
rungCondition is true, the resulting will be true and vice versa.

### Math Comparators
Math comparators are the "if" on Ladder logics for numeric values. Their objective is just affect the resulting 
rungCondition, not altering any memory addresses. An example of usage follows:

    { "Comparator": {
       "type": ">=",
       "value1": {
          "address": 0
       },
       "value2": {
          "value": 2
       }
    }}

Their parameter explanation follows:
* type: type of comparation. Can be one of: ==,>,>=,<,<=,!=
* value1: first value of comparation
* value2: second value of comparation

The type are all self explanatory and the logics can be read as follows:

    rungCondition = rungCondition && (value1 <type> value2)

Or as the example before:

    rungCondition = rungCondition && ([address 0] >= 2)

As you can see the operation always depends on preceding rungCondition. If they are false, the result is false and 
the instruction is not even processed. Otherwise, is processed and the result is the new rungCondition.

The example show a comparation of type >= of the value readed from address 0 (probabily an analog port) between the 
constant 2. If the value of the port is greater or equals 2, the resulting rungCondition will be true, otherwise false
respecting the preceding rungCondition too.

