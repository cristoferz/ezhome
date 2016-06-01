package br.com.ezhome;

import br.com.ezhome.config.ConfigFile;
import br.com.ezhome.device.ComPortReader;
import br.com.ezhome.device.PortConnector;
import br.com.ezhome.scheduler.GlobalScheduler;
import br.com.ezhome.webserver.WebServer;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 *
 * @author cristofer
 */
public class Controller {

   public static final String LOGGER_NAME = "ezHomeController";

   public static Logger logger;

   public static final Logger getLogger() {
      if (logger == null) {
         logger = Logger.getLogger(LOGGER_NAME);
         try {
            initLogger();
         } catch (IOException ex) {
            Logger.getLogger(LOGGER_NAME).log(Level.SEVERE, "Logger initialization errors", ex);
         }
      }
      return logger;
   }

   public Controller() throws Exception {
      try {
         initConfig();

         // Start the webserver
         WebServer.create(ConfigFile.getInstance().getHttpPort());

         // Start the scheduler
         new GlobalScheduler().start();

      } catch (Exception ex) {
         getLogger().log(Level.SEVERE, "Initialization errors", ex);
      }
   }

   public static void initLogger() throws IOException {
      FileHandler logHandler = new FileHandler(LOGGER_NAME + ".log", 10 * 1024 * 1024, 5, true);
      logHandler.setFormatter(new SimpleFormatter());
      logHandler.setEncoding("UTF-8");
      getLogger().addHandler(logHandler);
      getLogger().setLevel(ConfigFile.getInstance().getLoggerLevel());
      getLogger().setUseParentHandlers(true); 
      getLogger().log(Level.INFO, "Logger initialized");
   }

   public static void initConfig() throws IOException {
      ConfigFile.init();
   }

}
