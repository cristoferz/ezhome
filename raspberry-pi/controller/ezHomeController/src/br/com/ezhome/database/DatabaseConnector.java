package br.com.ezhome.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

/**
 *
 * @author cristofer
 */
public class DatabaseConnector {

   public DatabaseConnector() {
   }

   public void connect() throws SQLException {
      String url = "jdbc:postgresql://localhost:5432/ezhome";
      Properties props = new Properties();
      props.setProperty("user", "cristofer");
      props.setProperty("password", "zdepski");
      //props.setProperty("ssl", "true");
      Connection conn = DriverManager.getConnection(url, props);
      PreparedStatement stmt = conn.prepareStatement("select * from config.device");
      try {
         ResultSet rs = stmt.executeQuery();
         while (rs.next()) {
            System.out.println(": " + rs.getString("id") + "-" + rs.getString("runtime_id") + " : " + rs.getString("version_id") + " = " + rs.getString("connected_port"));
         }
      } finally {
         stmt.close();
      }

   }

   public static void main(String[] args) throws SQLException {
      new DatabaseConnector().connect();
   }
}
