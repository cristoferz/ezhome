package br.com.ezhome.webserver;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;

/**
 *
 * @author cristofer
 */
public class WebServer {

   private static WebServer instance;

   private HttpServer httpServer;

   public static WebServer getInstance() {
      if (instance == null) {
         throw new UnsupportedOperationException("Webserver ainda não foi inicializado");
      }
      return instance;
   }

   public static void create(int port) throws IOException {
      if (instance == null) {
         instance = new WebServer(port);
         System.out.println("Criado na porta " + port);
      }
   }

   public static void create() throws IOException {
      create(8080);
   }

   private WebServer(int port) throws IOException {
      httpServer = HttpServer.create(new InetSocketAddress(port), 0);
      httpServer.setExecutor(null);
      httpServer.createContext("/status");
      httpServer.start();
   }

}
