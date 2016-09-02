package br.com.ezhome.webserver;

import br.com.ezhome.Controller;
import br.com.ezhome.webserver.context.HttpHandlerActivation;
import br.com.ezhome.webserver.context.HttpHandlerConfigDevice;
import br.com.ezhome.webserver.context.HttpHandlerConfigDeviceProgram;
import br.com.ezhome.webserver.context.HttpHandlerConfigSerialPort;
import br.com.ezhome.webserver.context.HttpHandlerLogin;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.util.logging.Level;

/**
 *
 * @author cristofer
 */
public class WebServer {

   private static WebServer instance;

   private HttpServer httpServer;

   public static WebServer getInstance() {
      if (instance == null) {
         throw new UnsupportedOperationException("Webserver still not initialized");
      }
      return instance;
   }

   public static void create(int port) throws IOException {
      if (instance == null) {
         instance = new WebServer(port);
         Controller.getLogger().log(Level.INFO, "Webserver started on port {0}", port);
      }
   }

   public static void create() throws IOException {
      create(8080);
   }

   private WebServer(int port) throws IOException {
      httpServer = HttpServer.create(new InetSocketAddress(port), 0);
      //httpServer.setExecutor(null);
      httpServer.createContext("/", new HttpHandlerDefault());
      
      httpServer.createContext("/activation", new HttpHandlerActivation());
      httpServer.createContext("/login", new HttpHandlerLogin());
      httpServer.createContext("/config/serialPort", new HttpHandlerConfigSerialPort());
      httpServer.createContext("/config/device", new HttpHandlerConfigDevice());
      httpServer.createContext("/config/device/program", new HttpHandlerConfigDeviceProgram());
      
      httpServer.createContext("/device/", new HttpHandlerDevice());
      httpServer.start();
   }

}
