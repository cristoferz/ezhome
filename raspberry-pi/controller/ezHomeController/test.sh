OPCAO="$1"
#REMOTEADDR="http://192.168.1.29:8080"
REMOTEADDR="http://localhost:8080"
#DEVICE=/dev/ttyACM1
DEVICE=/dev/ttyUSB0

if [ -z "$OPCAO" ]
then
	echo "1. Programa 1"
	echo "2. Programa 2"
	echo "L. List devices"
	echo "C. Connect"
	echo "D. Disconnect"
	echo "S. Send Command"
	echo "U. Upload UNO"

	read -p "Opção: " OPCAO
fi

case $OPCAO in
	1)
curl $REMOTEADDR/device/program -X POST --data 'device='$DEVICE'&program={ "program":
   [ { "serie": [
         { "NO": 35 },
         { "Coil": 1 }
      ] },
     { "serie": [
        { "Parallel": [
           { "serie": [
              { "NO": 33 },
              { "Parallel": [
                 { "serie": [
                    { "FallingEdge": 98 }
                 ] },
                 { "serie": [
                    { "RisingEdge": 101 }
                 ] }
              ] }
           ] }
        ] },
        { "SetReset": { "address": 0, "reset": 0 } }
     ] }
   ] }';
	exit 0;
curl $REMOTEADDR/device/program -X POST --data 'device='$DEVICE'&program={ "program":
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
   ] }';
	;;
	2)
curl $REMOTEADDR/device/program -X POST --data 'device='$DEVICE'&program={ "program":
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
     ] },
     { "serie": [
        { "NO": $CONTROLE_REMOTO },
        { "RisingEdge": $CONTROLE_REMOTO_EDGE },
        { "SetReset": { "address": $ALARME_LIGADO, "reset": $ALARME_LIGADO } }
     ] },
     { "serie": [
        { "Parallel": [
           { "serie": [ 
              { "NC": $SENSOR1 }
           ] },
           { "serie": [ 
              { "NC": $SENSOR2 }
           ] }
        ] },
        { "NO": $ALARME_LIGADO },
        { "SetReset": { "address": $ALARME_DISPARO, "reset": false } }
     ] },
     { "serie": [
        { "NO": $ALARME_LIGADO },
        { "FallingEdge": $ALARME_LIGADO_EDGE },
        { "SetReset": { "address": $ALARME_DISPARO, "reset": true } }
     ] }
   ] }';

		;;
	3)
curl $REMOTEADDR/device/program -X POST --data 'device='$DEVICE'&program={ "program":
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
                    { "FallingEdge": 102 }
                 ] },
                 { "serie": [
                    { "RisingEdge": 103 }
                 ] }
              ] }
           ] }
        ] },
        { "SetReset": { "address": 2, "reset": 2} }
     ] }


   ] }'
		;;

        "S")
                command="$2";
                if [ -z "$command" ]
                then
   		   read -p "Comando a ser enviado: " command;
                fi;
                comm=`echo "$command" | sed 's/ /%20/g'`;
		curl "$REMOTEADDR/device/send?device=$DEVICE&command=$comm";
		;;
	"U")
		curl "$REMOTEADDR/device/upload?device=$DEVICE&model=UNO";
		;;
	"L")
		curl "$REMOTEADDR/device/list"
		;;
	"C")
		read -p "Porta a ser conectado: " PORT;
		curl "$REMOTEADDR/device/connect?device=$PORT"
		;;
	"D")
		read -p "Porta a ser conectado: " PORT;
		echo "$REMOTEADDR/device/disconnect?device=$PORT"
		curl "$REMOTEADDR/device/disconnect?device=$PORT"
		;;	
	*)
		echo "Invalid option";
		;;
esac
echo "";

