package br.com.ezhome.device.model;

import br.com.ezhome.database.DatabaseConnector;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author cristofer
 */
public class DeviceModels extends HashMap<Integer, DeviceModel> {

   public DeviceModels() {
      super();
   }

   /**
    * Get device models from database
    *
    * @throws java.sql.SQLException
    * @throws java.io.IOException
    */
   public void init() throws SQLException, IOException {
      try (Connection conn = DatabaseConnector.getInstance().connect()) {
         try (PreparedStatement stmt = conn.prepareStatement("select * from config.device_model")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
               put(rs.getInt("model_id"), new DeviceModel(rs.getInt("model_id"), rs.getString("description"), rs.getInt("digital_ports"), rs.getString("image_path")));
            }
         }
      }
   }

   /**
    * Returns a list of all device models available
    * 
    * @return JSONObject with all device models
    */
   public JSONObject toJSON() {
      JSONObject result = new JSONObject();
      JSONArray array = new JSONArray();
      result.put("models", array);
      keySet().stream().forEach((modelId) -> {
         array.put(get(modelId).toJSON());
      });
      return result;
   }
}
