package br.com.ezhome;

import br.com.ezhome.device.ComPortReader;
import br.com.ezhome.device.PortConnector;
import br.com.ezhome.webserver.WebServer;
import java.io.IOException;

/**
 *
 * @author cristofer
 */
public class Controller {
   private WebServer webServer;

   public Controller() throws Exception {
      WebServer.create();
      webServer = WebServer.getInstance();
      
      PortConnector.main(new String[0]);
      
      //ComPortReader.main(new String[] {} );
   }
   
   
}
