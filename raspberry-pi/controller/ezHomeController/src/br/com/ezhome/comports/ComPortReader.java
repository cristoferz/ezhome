package br.com.ezhome.comports;

import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.RXTXPort;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Enumeration;

/**
 *
 * @author cristofer
 */
public class ComPortReader {

   public static void main(String[] args) throws PortInUseException, UnsupportedCommOperationException, IOException {
      Enumeration ports = CommPortIdentifier.getPortIdentifiers();
      System.out.println("teste");
      while (ports.hasMoreElements()) {
         CommPortIdentifier port = (CommPortIdentifier) ports.nextElement();
         System.out.print(port.getName() + " -> " + port.getCurrentOwner() + " -> ");
         RXTXPort commPort = (RXTXPort) port.open("Teste", 1000);
         try {
            commPort.setSerialPortParams(
                    115200,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);

            BufferedReader is = new BufferedReader(new InputStreamReader(commPort.getInputStream()));
            try {
               PrintStream os = new PrintStream(commPort.getOutputStream(), true);
               try {
                  os.print("runtime-id\r\n");
                  System.out.println(is.readLine());
                  System.out.println(is.readLine());
                  System.out.println(is.readLine());
                  System.out.println(is.readLine());
                  
               } finally {
                  os.close();
               }
            } finally {
               is.close();
            }
         } finally {
            commPort.close();
         }
      }
   }

}
