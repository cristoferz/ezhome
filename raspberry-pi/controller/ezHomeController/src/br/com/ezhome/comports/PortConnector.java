package br.com.ezhome.comports;

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

   public static final int TIMEOUT = 2000;

   private PortReader reader;
   private PortWriter writer;

   private RXTXPort serialPort;

   public PortConnector(CommPortIdentifier portIdentifier) throws Exception {
      if (portIdentifier.isCurrentlyOwned()) {
         throw new Exception("Port " + portIdentifier.getName() + " is in use.");
      }
      CommPort commPort = portIdentifier.open(this.getClass().getName(), TIMEOUT);
      if (commPort instanceof SerialPort) {
         serialPort = (RXTXPort) commPort;
         // Define os parametros de conexão para os arduinos ezHome
         serialPort.setSerialPortParams(115200,
                 SerialPort.DATABITS_8,
                 SerialPort.STOPBITS_1,
                 SerialPort.PARITY_NONE);

         reader = new PortReader(serialPort.getInputStream());
         writer = new PortWriter(serialPort.getOutputStream());
         reader.start();
      }
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

   public void close() throws IOException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
      serialPort.removeEventListener();
      System.out.println("2");
      reader.close();
      System.out.println("3");
      writer.close();
      System.out.println("1");
      serialPort.close();
      //serialPort.System.out.println("close");
   }

   public static void main(String[] args) throws Exception {
//      while (true) {
      Enumeration<CommPortIdentifier> ports = PortIdentifier.listPorts();
      while (ports.hasMoreElements()) {
         CommPortIdentifier portIdentifier = ports.nextElement();
         System.out.println("Conectando a " + portIdentifier.getName());
         if (portIdentifier.isCurrentlyOwned()) {
            System.out.println("Porta está ocupada.");
            continue;
         }
         PortConnector port = new PortConnector(portIdentifier);
         port.addReaderListener(new PortReaderListener() {

            @Override
            public void lineReceived(String line) {
               System.out.println("Linha: " + line);
            }

            @Override
            public void messageReceived(String message) {
               System.out.println("Mensagem: " + message);
            }
         });

//         while (true) {
//            System.out.println("teste");
         //port.close();
         port.getWriter().write("device-config\n".getBytes());
         // break;
//            Thread.sleep(1000);
//         }
//            System.out.println("clo");
//           port.close();
      }
//         Thread.sleep(2000);
//      }
   }
}
