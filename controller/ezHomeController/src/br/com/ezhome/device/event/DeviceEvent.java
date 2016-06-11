package br.com.ezhome.device.event;

import br.com.ezhome.device.Device;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import org.json.JSONObject;

/**
 *
 * @author cristofer
 */
public abstract class DeviceEvent {

   private Device device;
   private Date date;

   public DeviceEvent(Device device) {
      this.device = device;
      this.date = new Date();
   }

   public Device getDevice() {
      return device;
   }

   public void setDevice(Device device) {
      this.device = device;
   }

   public Date getDate() {
      return date;
   }

   public void setDate(Date date) {
      this.date = date;
   }

   public abstract String getEventType();

   public abstract JSONObject getJSON();

   public void writeToDatabase(Connection connection) throws SQLException {
      String sql
              = "insert into config.device_event\n"
              + "   (device_id, timestamp, event_type, details)\n"
              + "values"
              + "   (:device_id, :timestamp, :event_type, :details);\n";

      try (PreparedStatement stmt = connection.prepareCall(sql)) {
         stmt.setInt(1, device.getRegisteredDevice().getDeviceId());
         stmt.setTimestamp(2, new java.sql.Timestamp(date.getTime()));
         stmt.setString(3, getEventType());
         stmt.setString(4, getJSON().toString());
         stmt.executeUpdate();
      }
   }
}
