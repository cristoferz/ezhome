# JSON Ladder
JSON Ladder is the base language used to program ezHome Project compatible devices 
with the logics implemented on ezHome Project. Is basically a translation of the 
Ladder Language to a JSON format. Above is an example of implementation:

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

## Program
The base of JSON Ladder. Is the first and unique node on JSON Ladder model.

## serie
Represents the series of Ladder processing. 

## Parallel
Represents a parallel logics on a single ladder serie.

## Instructions
Instructions are the interactions maded by Ladder logics.

### Coil
Represents an Output on Ladder logics. The rule is that a coil is not preceded by any other instruction, so comes in the end of a serie.

Example of a Coil representation:
      
    ---(  )---|

### NO/NC
NO represents a "Normally Open" switch.

Example of a NO representation:

    ---[ ]---

NC represents a "Normally Closed" switch. Basically are the inverted NO.

Example of a NC representation:

    ---[/]---

### Edges
Detects an edge on signal by the preceding logics. It detects an variation on signal from high to low (FallingEge) or low to high (RisingEdge), switching by only one cycle. No mather what signal is before an edge, the edges only pass a high signal just one cicle when the expected edge is detected, turning back to low signal.

An address has to be associated with an edge. It is responsible for the control of edge detection across the cycles of the controller.

Ps.: ''The address associated with the edges is used just for store the previous state of signal. Never could be used for other operations, with the risk of anomalous behaviors.''

Example of FallingEdge representation:

    -----[-_]-----

Example of FallingEdge on JSON Ladder:

   { "FallingEdge": 1 }

Example of RisingEdge representation:

    -----[_-]-----

Example of Java implementation:

    new FallingEdge(builder, getReservedAddress(builder, ++addressIndex))
    
    new RisingEdge(builder, getReservedAddress(builder, ++addressIndex))
