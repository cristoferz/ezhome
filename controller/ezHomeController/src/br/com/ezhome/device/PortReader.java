package br.com.ezhome.device;

import br.com.ezhome.Controller;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.logging.Level;

/**
 * Realiza a leitura constante das portas do Raspberry Pi
 *
 * @author cristofer
 */
public class PortReader extends Thread {

   private static final String END_OF_MESSAGE = "EOM";

   private final DeviceImpl connector;
   private final InputStream in;
   private final StringBuilder stringBuilder;
   private final BufferedReader bufferedReader;

   private final ArrayList<PortReaderListener> listeners;

   /**
    * Initializes a PortReader
    * 
    * @param connector
    * @param in 
    */
   protected PortReader(DeviceImpl connector, InputStream in) {
      this.connector = connector;
      this.in = in;
      stringBuilder = new StringBuilder();
      bufferedReader = new BufferedReader(new InputStreamReader(in));

      listeners = new ArrayList<>();
   }

   @Override
   public void run() {
      try {
         String line;
         while (true) {
            if (bufferedReader.ready()) {
               line = bufferedReader.readLine();
               if (line == null) {
                  break;
               }
               fireLineReceived(line);
               if (END_OF_MESSAGE.equals(line)) {
                  fireMessageReceived(stringBuilder.toString());
                  // Limpa o buffer de mensagens
                  stringBuilder.setLength(0);
               } else {
                  stringBuilder.append(line).append("\n");
               }
            }
         }
      } catch (Exception ex) {
         Controller.getLogger().log(Level.SEVERE, "Error on PortReader thread", ex);
      }
   }

   /**
    * Closes this PortReader and all streams and buffers
    * 
    * @throws IOException 
    */
   public void close() throws IOException {
      bufferedReader.close();
      in.close();
   }

   /**
    * Adds a readerListener to this PortReader
    *
    * @param listener
    */
   public void addListener(PortReaderListener listener) {
      listeners.add(listener);
   }

   /**
    * Removes the especified listener from this PortReader
    *
    * @param listener
    * @return
    */
   public boolean removeListener(PortReaderListener listener) {
      return listeners.remove(listener);
   }

   /**
    * Fires all listeners when a line is received by this PortReader device
    *
    * @param line the line received
    */
   protected void fireLineReceived(String line) {
      for (PortReaderListener listener : listeners) {
         listener.lineReceived(line);
      }
   }

   /**
    * Fires all listeners when a complete message is received by this PortReader
    * device. A complete message is finished by a EOM (End Of Message).
    *
    * @param message the complete message received
    */
   protected void fireMessageReceived(String message) {
      for (PortReaderListener listener : listeners) {
         listener.messageReceived(message);
      }
   }
}
