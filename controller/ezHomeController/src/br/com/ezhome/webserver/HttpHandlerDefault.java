package br.com.ezhome.webserver;

import br.com.ezhome.lib.config.ConfigFile;
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
   public void handleRequest(HttpExchange he) throws Exception {
      File folder = new File(ConfigFile.getInstance().getWebsiteFolder());
      if (!folder.exists()) {
         response(404, "Defined websiteFolder \"" + ConfigFile.getInstance().getWebsiteFolder() + "\" does not exists. Verify config.properties.", he);
         return;
      }

      try {
         File file = new File(folder, he.getRequestURI().getPath());
         //System.out.println(he.getRequestMethod() + " " + file.getAbsolutePath() + " " + file.length());

         if (file.exists()) {
            if (file.isDirectory()) {
               directoryResponse(he, file);
               return;
            } else {
               fileResponse(he, file);
            }
         } else {
            fileResponse(he, new File(folder, "index.html"));
            //response(404, "File \"" + he.getRequestURI().getPath() + "\" not found", he);
            return;
         }
      } catch (IOException ex) {
         ex.printStackTrace();
      }
   }

   private void response(int code, String text, HttpExchange he) throws IOException {
      he.sendResponseHeaders(404, text.getBytes().length);
      OutputStream os = he.getResponseBody();
      try {
         os.write(text.getBytes());
      } finally {
         os.close();
      }

   }

   private void directoryResponse(HttpExchange he, File dir) throws IOException {
      if (new File(dir, "index.html").exists()) {
         fileResponse(he, new File(dir, "index.html"));
      } else {
         response(403, "<html><h1>403 Forbidden</h1></html>", he);
      }
   }

   private void fileResponse(HttpExchange he, File file) throws IOException {
      he.sendResponseHeaders(200, file.length());
      OutputStream os = he.getResponseBody();
      try {
         InputStream is = new FileInputStream(file);
         try {
            int len;
            byte[] buf = new byte[1024];
            while ((len = is.read(buf)) != -1) {
               os.write(buf, 0, len);
            }

         } finally {
            is.close();
         }
      } finally {
         os.close();
      }
   }

}
