package br.com.ezhome.database.init;

import static br.com.ezhome.database.init.DatabaseInitializerObject.NOT_EXIST;
import static br.com.ezhome.database.init.DatabaseInitializerObject.OK;
import static br.com.ezhome.database.init.DatabaseInitializerObject.WRONG_DATATYPE;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author cristofer
 */
public abstract class DatabaseInitializerTable extends DatabaseInitializerObject {

   private ArrayList<DatabaseInitializerTableColumn> columns;

   public DatabaseInitializerTable(Connection connection) {
      super(connection);
      columns = getColumns();
   }

   protected abstract String getSchemaName();

   protected abstract String getTableName();

   protected abstract ArrayList<DatabaseInitializerTableColumn> getColumns();

   @Override
   public boolean exists() throws SQLException {
      return tableExists(getSchemaName(), getTableName());
   }

   @Override
   public void create() throws SQLException {
      try (PreparedStatement stmt = getConnection().prepareStatement(createString())) {
         stmt.execute();
      }
   }

   protected String createString() {
      String sql = "create table " + getSchemaName() + "." + getTableName() + "(\n";
      for (int i = 0; i < columns.size(); i++) {
         sql += columns.get(i).createString();
         if (i != columns.size() - 1) {
            sql += ",\n";
         } else {
            sql += "\n";
         }
      }
      sql += ");";
      return sql;
   }

   @Override
   public boolean validateStructure() throws SQLException {
      // Validate table existance
      if (!exists()) {
         return false;
      }
      // Validate columns
      for (DatabaseInitializerTableColumn column : columns) {
         if (validateColumn(getSchemaName(), getTableName(), column.getColumnName(), column.getDataType(), column.getSize(), column.getPrecision()) != OK) {
            return false;
         }
      }
      return true;
   }

   @Override
   public void repair() throws SQLException {
      if (!exists()) {
         // if table not exists creates, and nothing more is necessary
         System.out.println("Creating");
         create();
      } else {
         // if table exists is necessary to verify and repair every column
         for (DatabaseInitializerTableColumn column : columns) {
            System.out.println("Repair column " + column.getColumnName());
            repairColumn(column);
         }
      }
   }

   protected void repairColumn(DatabaseInitializerTableColumn column) throws SQLException {
      int validation = validateColumn(getSchemaName(), getTableName(), column.getColumnName(), column.getDataType(), column.getSize(), column.getPrecision());
      switch (validation) {
         case WRONG_DATATYPE:
            System.out.println("altering column");
            execute("alter table " + getSchemaName() + "." + getTableName() + " alter column " + column.modifyString());
            break;
         case NOT_EXIST:
            System.out.println("Creating column");
            execute("alter table " + getSchemaName() + "." + getTableName() + " add column " + column.createString());
            break;
         case OK:
            // Nothing to do on column
            System.out.println("Nothing to do");
            break;
         default:
            throw new IllegalArgumentException("Validation result invalid: " + validation);
      }
   }

   /**
    * Verifies if given schema.tableName exists on target database
    *
    * @param schema
    * @param tableName
    * @return
    * @throws SQLException
    */
   public boolean tableExists(String schema, String tableName) throws SQLException {
      String sql = "select 1\n"
              + "  from pg_tables\n"
              + " where schemaname = ?\n"
              + "   and tablename = ?";
      try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
         stmt.setString(1, schema);
         stmt.setString(2, tableName);
         ResultSet rs = stmt.executeQuery();
         return rs.next();
      }
   }

   /**
    *
    * @param schema
    * @param tableName
    * @param columnName
    * @param dataType
    * @return
    * @throws SQLException
    */
   public int validateColumn(String schema, String tableName, String columnName, String dataType, int size, int precision) throws SQLException {
      String sql = "select *\n"
              + "  from information_schema.columns\n"
              + " where table_schema = ?\n"
              + "   and table_name = ?\n"
              + "   and column_name = ?\n";
      try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
         stmt.setString(1, schema);
         stmt.setString(2, tableName);
         stmt.setString(3, columnName);
         ResultSet rs = stmt.executeQuery();
         String convertedDatatype = dataType;
         if (dataType.equalsIgnoreCase(DatabaseInitializerTableColumn.DATATYPE_SERIAL)) {
            convertedDatatype = DatabaseInitializerTableColumn.DATATYPE_INTEGER;
         }
         if (rs.next()) {
            if (rs.getString("data_type").equalsIgnoreCase(convertedDatatype)) {
               return OK;
            } else {
               return WRONG_DATATYPE;
            }
         } else {
            return NOT_EXIST;
         }
      }
   }

   public class DatabaseInitializerTableColumn {

      public static final String DATATYPE_VARCHAR = "character varying",
              DATATYPE_NUMERIC = "numeric", DATATYPE_INTEGER = "integer",
              DATATYPE_SERIAL = "serial", DATATYPE_JSON = "json", 
              DATATYPE_TIMESTAMP = "timestamp with time zone";

      private final String columnName;
      private final String dataType;

      private final int size, precision;

      public DatabaseInitializerTableColumn(String columnName, String dataType, int size, int precision) {
         this.columnName = columnName;
         this.dataType = dataType;
         this.size = size;
         this.precision = precision;
      }

      public String getColumnName() {
         return columnName;
      }

      public String getDataType() {
         return dataType;
      }

      public int getSize() {
         return size;
      }

      public int getPrecision() {
         return precision;
      }

      public String getFullDataType() {
         switch(dataType) {
            case DATATYPE_VARCHAR:
               if (size > 0) {
                  return getDataType() + "(" + getSize() + ")";
               } else {
                  return getDataType();
               }
            case DATATYPE_NUMERIC:
               if (size > 0) {
                  return getDataType() + "(" + getSize() + ")";
               } else {
                  return getDataType();
               }
            default:
               return getDataType();
         }
      }

      public String createString() {
         return getColumnName() + " " + getDataType();
      }

      public String modifyString() {
         if (getDataType().equals(DATATYPE_SERIAL)) {
            return getColumnName() + " type " + DATATYPE_INTEGER;
         }
         return getColumnName() + " type " + getDataType();
      }
   }
}
