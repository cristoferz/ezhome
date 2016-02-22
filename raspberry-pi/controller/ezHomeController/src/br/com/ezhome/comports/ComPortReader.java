package br.com.ezhome.comports;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.RXTXPort;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.TooManyListenersException;

/**
 *
 * @author cristofer
 */
public class ComPortReader {

   private RXTXPort port;
   private BufferedReader reader;
   private PrintStream printStream;

   private static final String COMMAND_EOF = "\r\n";

   public ComPortReader() {
   }

   public static Enumeration getPorts() {
      return CommPortIdentifier.getPortIdentifiers();
   }

   public static CommPortIdentifier getPort(String identifier) throws NoSuchPortException {
      return CommPortIdentifier.getPortIdentifier(identifier);
   }

   public static RXTXPort openPort(CommPortIdentifier portIdentifier) throws PortInUseException, UnsupportedCommOperationException {
      RXTXPort port = (RXTXPort) portIdentifier.open("ezHomeController", 1000);
      port.setSerialPortParams(
              115200,
              SerialPort.DATABITS_8,
              SerialPort.STOPBITS_1,
              SerialPort.PARITY_NONE);
      return port;
   }

   public void connect(CommPortIdentifier portIdentifier) throws PortInUseException, UnsupportedCommOperationException, TooManyListenersException {
      port = openPort(portIdentifier);
      System.out.println("1");
      reader = new BufferedReader(new InputStreamReader(port.getInputStream()));
      System.out.println("2");
      printStream = new PrintStream(port.getOutputStream(), true);
      System.out.println("3");
//      port.notifyOnDataAvailable(true);
//      port.addEventListener(new SerialPortEventListener() {
//
//         @Override
//         public void serialEvent(SerialPortEvent spe) {
//            if (spe.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
//               try {
//                  String line = reader.readLine();
//                  while (line != null) {
//                     line = reader.readLine();
//                     System.out.println(line);
//                     if(line.contains("EOM")) 
//                        System.exit(1);
//                  }
//                  System.out.println(line);
//               } catch (Exception e) {
//                  System.out.println(e.toString());
//               }
//            }
//         }
//      });
   }

   public BufferedReader getReader() {
      return reader;
   }

   public PrintStream getPrintStream() {
      return printStream;
   }

   public RXTXPort getPort() {
      return port;
   }

   public String sendCommand(String command) throws IOException, InterruptedException {
      getPrintStream().print(command);
      getPrintStream().print(COMMAND_EOF);
      System.out.println("enviado");
      String line = "";
      StringBuilder sb = new StringBuilder();

      while (line != null) {
         line = reader.readLine();
         System.out.println(line);
         sb.append(line+"\n");
         if (line.contains("EOM")) {
            break;
         }
         Thread.sleep(1000);
      }

      //InputStream is = port.getInputStream();
      int len;
//      System.out.println("quase");
//      byte buf[] = new byte[1024];
//      
//      while ((len = is.read(buf)) > 0) {
//         System.out.println("lendo");
//         String line2 = new String(buf, 0, len);
//         //System.out.println("len "+len);
//         System.out.print(line2);
//         Thread.sleep(200);
//         sb.append(line2);
////         if (line2.contains("EOM")) {
////            break;
////         }
//         
//         if(sb.toString().contains("EOM")) {
//            break;
//         }
//      }
//      System.out.println("7");
      return sb.toString();
   }

   public void close() throws IOException {
//      port.getInputStream().close();
//      printStream.close();
      //port.close();

   }

   public void teste() throws IOException {
      while (true) {
         String line = "";
         while ((line = reader.readLine()) != null) {

         }
      }
   }

   public static void main(String[] args) throws PortInUseException, UnsupportedCommOperationException, IOException, InterruptedException, TooManyListenersException {
      ComPortReader reader = new ComPortReader();
      Enumeration ports = getPorts();
      System.out.println("lendo portas");
      while (ports.hasMoreElements()) {
         CommPortIdentifier portIdentifier = (CommPortIdentifier) ports.nextElement();
         System.out.println("Conectando a " + portIdentifier.getName());
         reader.connect(portIdentifier);

         System.out.println(reader.sendCommand("device-config"));
         reader.close();
      }
     // while (true);
//      Enumeration ports = CommPortIdentifier.getPortIdentifiers();
//      System.out.println("teste");
//      while (ports.hasMoreElements()) {
//         CommPortIdentifier port = (CommPortIdentifier) ports.nextElement();
//         System.out.print(port.getName() + " -> " + port.getCurrentOwner() + " -> ");
//         RXTXPort commPort = (RXTXPort) port.open("Teste", 1000);
//         try {
//            commPort.setSerialPortParams(
//                    115200,
//                    SerialPort.DATABITS_8,
//                    SerialPort.STOPBITS_1,
//                    SerialPort.PARITY_NONE);
//
//            BufferedReader is = new BufferedReader(new InputStreamReader(commPort.getInputStream()));
//            try {
//               PrintStream os = new PrintStream(commPort.getOutputStream(), true);
//               try {
//                  os.print("runtime-id\r\n");
//                  System.out.println(is.readLine());
//                  System.out.println(is.readLine());
//                  System.out.println(is.readLine());
//                  System.out.println(is.readLine());
//
//               } finally {
//                  os.close();
//               }
//            } finally {
//               is.close();
//            }
//         } finally {
//            commPort.close();
//         }
//      }
   }

}
