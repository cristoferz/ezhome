package br.com.ezhome.database;

import br.com.ezhome.lib.config.ConfigFile;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 *
 * @author cristofer
 */
public class DatabaseConnector {

   private static DatabaseConnector instance;

   public static DatabaseConnector getInstance() {
      if (instance == null) {
         instance = new DatabaseConnector();
      }
      return instance;
   }

   private DatabaseConnector() {
   }

   public Connection connect() throws SQLException, IOException {
      String url = "jdbc:postgresql://" + ConfigFile.getInstance().getDatabaseHost() + ":" + ConfigFile.getInstance().getDatabasePort() + "/" + ConfigFile.getInstance().getDatabaseName();
      Properties props = new Properties();
      props.setProperty("user", ConfigFile.getInstance().getDatabaseUsername());
      props.setProperty("password", ConfigFile.getInstance().getDatabasePassword());
      //props.setProperty("ssl", "true");
      return DriverManager.getConnection(url, props);
   }
}
