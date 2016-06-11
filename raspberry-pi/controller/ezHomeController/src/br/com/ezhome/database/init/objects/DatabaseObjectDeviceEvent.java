package br.com.ezhome.database.init.objects;

import br.com.ezhome.database.init.DatabaseInitializerTable;
import java.sql.Connection;
import java.util.ArrayList;

/**
 *
 * @author cristofer
 */
public class DatabaseObjectDeviceEvent extends DatabaseInitializerTable {

   public DatabaseObjectDeviceEvent(Connection connection) {
      super(connection);
   }

   @Override
   protected String getSchemaName() {
      return "config";
   }

   @Override
   protected String getTableName() {
      return "device_event";
   }

   @Override
   protected ArrayList<DatabaseInitializerTableColumn> getColumns() {
      ArrayList<DatabaseInitializerTableColumn> result = new ArrayList<>();
      result.add(new DatabaseInitializerTableColumn("event_id", DatabaseInitializerTableColumn.DATATYPE_SERIAL, 0, 0));
      result.add(new DatabaseInitializerTableColumn("timestamp", DatabaseInitializerTableColumn.DATATYPE_TIMESTAMP, 0, 0));
      result.add(new DatabaseInitializerTableColumn("event_data", DatabaseInitializerTableColumn.DATATYPE_JSON, 0, 0));
      return result;
   }
   
}
