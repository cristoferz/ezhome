echo "1. Programar"
echo "2. "

curl http://192.168.1.29:8080/device/program -X POST --data 'device=/dev/ttyACM0&program={ "program": 
   [ { "serie": [ 
        { "Parallel": [ 
           { "serie": [ { "NO": 9 } ] }, 
           { "serie": [ { "NC": 58 } ] } 
        ] } , 
        { "Parallel": [ 
           { "serie": [ { "NC": 9 } ] }, 
           { "serie": [ { "NO": 58 } ] } 
        ] }, 
        { "Coil": 4 } 
     ] } 
   ] }'
