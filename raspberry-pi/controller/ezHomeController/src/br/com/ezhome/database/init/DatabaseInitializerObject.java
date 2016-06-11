package br.com.ezhome.database.init;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author cristofer
 */
public abstract class DatabaseInitializerObject {

   public static final int NOT_EXIST = 0, OK = 1, WRONG_DATATYPE = 2;

   private Connection connection;

   public DatabaseInitializerObject(Connection connection) {
      this.connection = connection;
   }

   public Connection getConnection() {
      return connection;
   }

   public void setConnection(Connection connection) {
      this.connection = connection;
   }

   /**
    * Verifies if the target object already exists on target database
    *
    * @return
    */
   public abstract boolean exists() throws SQLException;

   /**
    * Creates the object on target database
    */
   public abstract void create() throws SQLException;

   /**
    * Validate object structure of object on target database
    */
   public abstract boolean validateStructure() throws SQLException;

   /**
    * Repair the structure of object
    */
   public abstract void repair() throws SQLException;
   
   protected void execute(String sql) throws SQLException {
      try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
         stmt.execute();
      }
   }
}
