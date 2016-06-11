package br.com.ezhome.scheduler;

import br.com.ezhome.Controller;
import br.com.ezhome.device.DeviceManager;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This scheduler is the responsible to monitor the connection between Arduinos
 * and Controller.
 *
 * @author cristofer
 */
public class PortConnectorSchedulerTask extends TimerTask {
   
   private ArrayList<String> runtimeIds;
   
   public PortConnectorSchedulerTask() {
      runtimeIds = new ArrayList<>();
   }

   @Override
   public void run() {
      try {
         DeviceManager.getInstance().scanDevices(false);
      } catch (Exception ex) {
         Controller.getLogger().log(Level.SEVERE, "Error scanning devices", ex);
      }
      try {
         DeviceManager.getInstance().localRegisterEvents();
      } catch (Exception ex) {
         Controller.getLogger().log(Level.SEVERE, "Error registering events", ex);
      }
   }

}
