package br.com.ezhome;

import br.com.ezhome.comports.ComPortReader;
import br.com.ezhome.webserver.WebServer;
import java.io.IOException;

/**
 *
 * @author cristofer
 */
public class Controller {
   private WebServer webServer;

   public Controller() throws Exception {
      //WebServer.create();
      //webServer = WebServer.getInstance();
      
      ComPortReader.main(new String[] {} );
   }
   
   
}
