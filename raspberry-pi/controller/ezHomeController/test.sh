OPCAO="$1"
if [ -z "$OPCAO" ]
then
	echo "1. Programa 1"
	echo "2. Programa 2"
	echo "S. Send Command"

	read -p "Opção: " OPCAO
fi

case $OPCAO in
	1)
curl http://192.168.1.29:8080/device/program -X POST --data 'device=/dev/ttyACM0&program={ "program": 
   [ { "serie": [ 
        { "Parallel": [ 
           { "serie": [ { "NO": 9 } ] }, 
           { "serie": [ { "NC": 49 } ] } 
        ] } , 
        { "Parallel": [ 
           { "serie": [ { "NC": 9 } ] }, 
           { "serie": [ { "NO": 49 } ] } 
        ] }, 
        { "Coil": 4 } 
     ] },
     { "serie": [
        { "Parallel": [
           { "serie": [ { "NO": 10 } ] },
           { "serie": [ { "NC": 50 } ] }
        ] } ,
        { "Parallel": [
           { "serie": [ { "NC": 10 } ] },
           { "serie": [ { "NO": 50 } ] }
        ] },
        { "Coil": 2 }
     ] } 
   ] }';
	;;
	2)
curl http://192.168.1.29:8080/device/program -X POST --data 'device=/dev/ttyACM0&program={ "program":
   [ { "serie": [
        { "Parallel": [
           { "serie": [
              { "NO": 9 },
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
              { "NO": 10 },
              { "Parallel": [
                 { "serie": [
                    { "FallingEdge": 104 }
                 ] },
                 { "serie": [
                    { "RisingEdge": 105 }
                 ] }
              ] }
           ] }
        ] },
        { "SetReset": { "address": 2, "reset": 2} }
     ] }
   ] }';

	;;
        "S")
                command="$2";
                if [ -z "$command" ]
                then
   		   read -p "Comando a ser enviado: " command;
                fi;
                comm=`echo "$command" | sed 's/ /%20/g'`;
		curl "http://192.168.1.29:8080/device/send?device=/dev/ttyACM0&command=$comm";
esac
echo "";

