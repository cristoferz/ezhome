package br.com.ezhome;

import br.com.ezhome.lib.LibManager;
import br.com.ezhome.lib.config.ConfigFile;
import br.com.ezhome.lib.logger.EzHomeLogger;
import br.com.ezhome.webserver.WebServer;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author cristofer
 */
public class Controller {

   public static final Logger getLogger() {
      return EzHomeLogger.getLogger();
   }

   public Controller() throws Exception {
      try {
         initConfig();
         
         // Install Libs
         LibManager.getInstance().install();

         // Start the webserver
         WebServer.create(ConfigFile.getInstance().getHttpPort());
         
         

         // Start the scheduler
         //new GlobalScheduler().start();
      } catch (Exception ex) {
         EzHomeLogger.getLogger().log(Level.SEVERE, "Initialization errors", ex);
      }
   }

   public static void initConfig() throws IOException {
      ConfigFile.init();
   }

}
