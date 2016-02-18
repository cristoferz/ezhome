package br.com.ezhome;

import br.com.ezhome.webserver.WebServer;
import java.io.IOException;

/**
 *
 * @author cristofer
 */
public class Controller {
   private WebServer webServer;

   public Controller() throws IOException {
      WebServer.create();
      webServer = WebServer.getInstance();
   }
   
   
}
