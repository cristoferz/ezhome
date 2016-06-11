package br.com.ezhome.config;

import br.com.ezhome.Controller;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;

/**
 *
 * @author cristofer
 */
public final class ConfigFile {

   public static ConfigFile instance;

   private File file;
   private final Properties properties;

   public static ConfigFile getInstance() throws IOException {
      if (instance == null) {
         init();
      }
      return instance;
   }

   public static void init(File file) throws IOException {
      if (instance == null) {
         instance = new ConfigFile();
      }
      instance.setSource(file, true);
   }

   public static void init() throws IOException {
      if (instance == null) {
         instance = new ConfigFile();
         instance.setSource(new File("config.properties"), true);
      }
   }

   private ConfigFile() throws IOException {
      this.properties = new Properties();
   }

   public void setSource(File file, boolean load) throws IOException {
      this.file = file;
      if (load) {
         load();
      }
   }

   public void load() throws IOException {
      if (file == null) {
         throw new IllegalStateException("No target file specified");
      }
      if (file.exists()) {
         try (FileInputStream f = new FileInputStream(file)) {
            this.properties.load(f);
         }
      }
   }

   public void save() throws IOException {
      try (FileOutputStream f = new FileOutputStream(file)) {
         this.properties.store(f, null);
      }
   }

   public String getDatabaseHost() {
      return this.properties.getProperty("databaseHost", "localhost");
   }

   public void setDatabaseHost(String value) {
      this.properties.setProperty("databaseHost", value);
   }

   public int getDatabasePort() {
      return Integer.valueOf(this.properties.getProperty("databasePort", "5432"));
   }

   public void setDatabasePort(int value) {
      this.properties.setProperty("databasePort", "" + value);
   }

   public String getDatabaseName() {
      return this.properties.getProperty("databaseName", "");
   }

   public void setDatabaseName(String value) {
      this.properties.setProperty("databaseName", value);
   }

   public String getDatabaseUsername() {
      return this.properties.getProperty("databaseUsername", "");
   }

   public void setDatabaseUsername(String value) {
      this.properties.setProperty("databaseUsername", value);
   }

   public String getDatabasePassword() {
      return this.properties.getProperty("databasePassword", "");
   }

   public void setDatabasePassword(String value) {
      this.properties.setProperty("databasePassword", value);
   }

   public int getHttpPort() {
      return Integer.valueOf(this.properties.getProperty("httpPort", "8080"));
   }

   public void setHttpPort(int value) {
      this.properties.setProperty("httpPort", "" + value);
   }
   
   public Level getLoggerLevel() {
      return Level.parse(this.properties.getProperty("loggerLevel", "INFO"));
   }
   
   public void setLoggerLevel(Level level) {
      this.properties.setProperty("loggerLevel", level.getName());
      Controller.getLogger().setLevel(level);
   }

}
