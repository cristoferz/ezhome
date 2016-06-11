package br.com.ezhome.webserver;

import com.sun.net.httpserver.HttpExchange;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author cristofer
 */
public class HttpHandlerDefault extends HttpHandlerAbstract {

   @Override
   public void handle(HttpExchange he) throws IOException {
      try {
         File file = new File(getClass().getResource("/br/com/ezhome/webserver/website/" + he.getRequestURI().getPath()).getFile());
         System.out.println("Size: "+file.length());

         he.sendResponseHeaders(200, file.length());
         OutputStream os = he.getResponseBody();
         try {
            InputStream is = new FileInputStream(file);
            long size = 0;
            try {
               int len;
               byte[] buf = new byte[1024];
               while ((len = is.read(buf)) != -1) {
                  os.write(buf, 0, len);
                  size += len;
                  System.out.println("resp " + size);
               }
               
            } finally {
               is.close();
            }
         } finally {
            os.close();
         }
      } catch (IOException ex) {
         ex.printStackTrace();
      }
   }

}
