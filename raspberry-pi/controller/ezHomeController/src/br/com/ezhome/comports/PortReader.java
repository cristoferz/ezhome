package br.com.ezhome.comports;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Realiza a leitura constante das portas do Raspberry Pi
 *
 * @author cristofer
 */
public class PortReader extends Thread {

   private static final String END_OF_MESSAGE = "EOM";

   private InputStream in;
   private StringBuilder stringBuilder;
   private BufferedReader bufferedReader;

   private ArrayList<PortReaderListener> listeners;

   public PortReader(InputStream in) {
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
               System.out.println("read " + line);
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
         ex.printStackTrace();
      }
   }

   public void close() throws IOException {

      bufferedReader.close();
      in.close();
   }

   public void addListener(PortReaderListener listener) {
      listeners.add(listener);
   }

   public boolean removeListener(PortReaderListener listener) {
      return listeners.remove(listener);
   }

   protected void fireLineReceived(String line) {
      for (PortReaderListener listener : listeners) {
         listener.lineReceived(line);
      }
   }

   protected void fireMessageReceived(String message) {
      for (PortReaderListener listener : listeners) {
         listener.messageReceived(message);
      }
   }
}
