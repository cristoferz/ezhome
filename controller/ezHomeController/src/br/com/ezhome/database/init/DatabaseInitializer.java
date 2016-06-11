package br.com.ezhome.database.init;

import br.com.ezhome.database.DatabaseConnector;
import br.com.ezhome.database.init.objects.DatabaseObjectDeviceEvent;
import br.com.ezhome.database.init.objects.DatabaseObjectDeviceModel;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * This class is responsible for database initialization after the instalation
 * of ezHomeProject
 *
 * @author cristofer
 */
public class DatabaseInitializer {

   private Connection connection;

   public DatabaseInitializer(Connection connection) {
      this.connection = connection;
   }

   public Connection getConnection() {
      return connection;
   }

   public void repair() throws SQLException {
      for (DatabaseInitializerObject object : getObjects()) {
         System.out.println("Repair ");
         object.repair();
      }
   }

   public ArrayList<DatabaseInitializerObject> getObjects() {
      ArrayList<DatabaseInitializerObject> result = new ArrayList<>();
      result.add(new DatabaseObjectDeviceModel(getConnection()));
      result.add(new DatabaseObjectDeviceEvent(getConnection()));
      return result;
   }
   
   public static void main(String[] args) throws SQLException, IOException {
      new DatabaseInitializer(DatabaseConnector.getInstance().connect()).repair();
   }
}
