package br.com.ezhome.device;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.RXTXPort;
import gnu.io.SerialPort;
import java.io.IOException;
import java.util.Enumeration;

/**
 * Utilizado para o gerenciamento de conexões das portas seriais
 *
 * @author cristofer
 */
public class PortConnector {

   public static final int TIMEOUT = 2000, COMMAND_TIMEOUT = 2000;

   private PortManager manager;
   private PortReader reader;
   private PortWriter writer;

   private RXTXPort serialPort;
   
   private PortReaderAdapter commandReceiver;
   private String receivedMessage;

   public PortConnector(PortManager manager, CommPortIdentifier portIdentifier) throws Exception {
      this.manager = manager;
      if (portIdentifier.isCurrentlyOwned()) {
         throw new Exception("Port " + portIdentifier.getName() + " is in use.");
      }
      
      commandReceiver = new PortReaderAdapter() {

         @Override
         public void messageReceived(String message) {
            receivedMessage = message;
            System.out.println("chegou");
         }
         
      };
      CommPort commPort = portIdentifier.open(this.getClass().getName(), TIMEOUT);
      if (commPort instanceof SerialPort) {
         serialPort = (RXTXPort) commPort;
         // Define os parametros de conexão para os arduinos ezHome
         serialPort.setSerialPortParams(115200,
                 SerialPort.DATABITS_8,
                 SerialPort.STOPBITS_1,
                 SerialPort.PARITY_NONE);

         reader = new PortReader(this, serialPort.getInputStream());
         writer = new PortWriter(this, serialPort.getOutputStream());
         reader.start();
      }
      // Delay para garantir o funcionamento da conexão 
      // Se tentar enviar dados em um periodo muito curto o arduino não responde
      Thread.sleep(3000);
   }
   
   public String getName() {
      return serialPort.getName();
   }

   public void addReaderListener(PortReaderListener listener) {
      reader.addListener(listener);
   }

   public boolean removeReaderListener(PortReaderListener listener) {
      return reader.removeListener(listener);
   }

   public PortWriter getWriter() {
      return writer;
   }
   
   public String sendCommand(String command, int timeout) throws IOException {
      receivedMessage = null;
      addReaderListener(commandReceiver);
      getWriter().write((command+"\n").getBytes());
      
      long start = System.currentTimeMillis();
      while (receivedMessage == null) {
         if (System.currentTimeMillis() > start + timeout) {
            break;
         }
      }
      removeReaderListener(commandReceiver);
      return receivedMessage;
   }
   
   public String sendCommand(String command) throws IOException {
      return sendCommand(command, COMMAND_TIMEOUT);
   }

   public void close() throws IOException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
      reader.close();
      writer.close();
      serialPort.close();
   }

   public static void main(String[] args) throws Exception {
//      Enumeration<CommPortIdentifier> ports = PortManager.listPorts();
//      while (ports.hasMoreElements()) {
//         CommPortIdentifier portIdentifier = ports.nextElement();
//         System.out.println("Conectando a " + portIdentifier.getName());
//         if (portIdentifier.isCurrentlyOwned()) {
//            System.out.println("Porta está ocupada.");
//            continue;
//         }
//         PortConnector port = new PortConnector(portIdentifier);
//         port.addReaderListener(new PortReaderListener() {
//
//            @Override
//            public void lineReceived(String line) {
//               System.out.println("Linha: " + line);
//            }
//
//            @Override
//            public void messageReceived(String message) {
//               System.out.println("Mensagem: " + message);
//            }
//         });
//
//         new Thread() {
//
//            @Override
//            public void run() {
//               try {
//                  int c = 0;
//                  while ((c = System.in.read()) > -1) {
////               if((char)c == '\n') {
////                  this.out.write('\r');
////               }
//                     port.getWriter().write(c);
//                  }
//               } catch (IOException e) {
//                  e.printStackTrace();
//               }
//            }
//
//         }.start();
//         System.out.println("write");
//          port.getWriter().write("device-config\n".getBytes());
//      }
   }
}
