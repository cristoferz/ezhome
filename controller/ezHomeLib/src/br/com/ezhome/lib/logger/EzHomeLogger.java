package br.com.ezhome.lib.logger;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 *
 * @author cristofer
 */
public class EzHomeLogger {
   private static EzHomeLogger instance;
   
   public static final String LOGGER_NAME = "ezHome";

   public static Logger logger;
   
   public static void init() throws IOException {
      if (instance == null) {
         instance = new EzHomeLogger();
      }
   }
   
   public static EzHomeLogger getInstance() {
      if (instance == null) {
         try {
            init();
         } catch (IOException ex) {
            Logger.getLogger(EzHomeLogger.class.getName()).log(Level.SEVERE, null, ex);
         }
      }
      return instance;
   }
   
   private EzHomeLogger() throws IOException {
      FileHandler logHandler = new FileHandler(LOGGER_NAME + ".log", 10 * 1024 * 1024, 5, true);
      logHandler.setFormatter(new SimpleFormatter());
      logHandler.setEncoding("UTF-8");
      logger = Logger.getLogger(LOGGER_NAME);
      logger.addHandler(logHandler);
      logger.setLevel(Level.ALL);
      logger.setUseParentHandlers(true); 
      logger.log(Level.INFO, "Logger initialized");
   }

   public static Logger getLogger() {
      return logger;
   }
}
