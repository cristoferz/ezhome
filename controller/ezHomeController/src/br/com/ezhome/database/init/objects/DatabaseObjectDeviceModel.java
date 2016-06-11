package br.com.ezhome.database.init.objects;

import br.com.ezhome.database.DatabaseConnector;
import br.com.ezhome.database.init.DatabaseInitializerTable;
import java.sql.Connection;
import java.util.ArrayList;

/**
 *
 * @author cristofer
 */
public class DatabaseObjectDeviceModel extends DatabaseInitializerTable {

   public DatabaseObjectDeviceModel(Connection connection) {
      super(connection);
   }

   @Override
   protected String getSchemaName() {
      return "config";
   }

   @Override
   protected String getTableName() {
      return "device_model";
   }

   @Override
   protected ArrayList<DatabaseInitializerTableColumn> getColumns() {
      ArrayList<DatabaseInitializerTableColumn> columns = new ArrayList<>();
      columns.add(new DatabaseInitializerTableColumn("model_id", DatabaseInitializerTableColumn.DATATYPE_SERIAL, 0, 0));
      columns.add(new DatabaseInitializerTableColumn("description", DatabaseInitializerTableColumn.DATATYPE_VARCHAR, 60, 0));
      columns.add(new DatabaseInitializerTableColumn("digital_ports", DatabaseInitializerTableColumn.DATATYPE_INTEGER, 0, 0));
      columns.add(new DatabaseInitializerTableColumn("image_path", DatabaseInitializerTableColumn.DATATYPE_VARCHAR, 1000, 0));
      return columns;
   }

}
