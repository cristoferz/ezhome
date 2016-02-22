package br.com.ezhome.comports;

import java.io.InputStream;

/**
 * Realiza a leitura constante das portas do Raspberry Pi
 *
 * @author cristofer
 */
public class PortReader implements Runnable {

   private InputStream in;
   private StringBuilder stringBuilder;

   public PortReader(InputStream in) {
      this.in = in;
      stringBuilder = new StringBuilder();
   }

   @Override
   public void run() {
      byte[] buffer = new byte[1024];
      int len;
      try {
         while ((len = in.read(buffer)) != -1) {
            stringBuilder.append(new String(buffer, 0, len));
            //System.out.print();
         }
      } catch (Exception ex) {
         ex.printStackTrace();
      }
   }

}
