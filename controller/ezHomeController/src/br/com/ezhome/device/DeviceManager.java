package br.com.ezhome.device;

import br.com.ezhome.Controller;
import br.com.ezhome.database.DatabaseConnector;
import br.com.ezhome.device.event.DeviceEvent;
import gnu.io.CommPortIdentifier;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;
import java.util.logging.Level;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Utilizado para a detecção dos equipamentos conectados ao raspberry
 *
 * @author cristofer
 */
public class DeviceManager {

   private static DeviceManager instance;

   private HashMap<String, Device> connectedPorts;

   private ArrayList<RegisteredDevice> devices;

   private boolean pauseScan, runningScan;

   private Queue<DeviceEvent> events;

   /**
    * Returns de Manager single instance
    *
    * @return
    */
   public static DeviceManager getInstance() {
      if (instance == null) {
         instance = new DeviceManager();
      }
      return instance;
   }

   /**
    * Inittializes the Port Manager
    */
   private DeviceManager() {
      connectedPorts = new HashMap<>();
      devices = new ArrayList<>();
      this.pauseScan = false;
      this.runningScan = false;
      this.events = new LinkedList<>();
   }

   /**
    * Connects with the specified portIdentifier. If there is an active
    * connection with this port, return this connection
    *
    * @param identifier
    * @return
    * @throws Exception
    */
   public Device connect(CommPortIdentifier identifier) throws Exception {
      Device connector = connectedPorts.get(identifier.getName());
      if (connector == null) {
         connector = new Device(this);
         connector.connect(identifier);
         connectedPorts.put(identifier.getName(), connector);
      }
      return connector;
   }

   public Device connect(String name) throws Exception {
      return connect(CommPortIdentifier.getPortIdentifier(name));
   }

   public Device get(String name) {
      return connectedPorts.get(name);
   }

   protected void remove(Device connector) {
      RegisteredDevice device = getDeviceByRuntimeId(connector.getRuntimeId());
      device.setConnector(null);
      connectedPorts.remove(connector.getName());
   }

   /**
    * List the serial ports that stills not connected
    *
    * @return Enumeration of ports that is not connected
    */
   public Enumeration<CommPortIdentifier> listPorts() {
      return CommPortIdentifier.getPortIdentifiers();
   }

   public ArrayList<Device> listConnectedPorts() {
      ArrayList<Device> connectors = new ArrayList<>();
      for (String key : connectedPorts.keySet()) {
         connectors.add(connectedPorts.get(key));
      }
      return connectors;
   }

   public JSONObject jsonListPorts() {
      JSONObject json = new JSONObject();
      JSONArray devices = new JSONArray();
      ArrayList<Device> connectors = listConnectedPorts();
      for (Device connector : connectors) {
         JSONObject device = new JSONObject();
         device.put("name", connector.getName());
         device.put("currentOwner", "self");
         device.put("connected", true);
         device.put("runtimeId", connector.getRuntimeId());
         device.put("versionId", connector.getVersionId());
         if (connector.getRegisteredDevice() != null) {
            device.put("deviceId", connector.getRegisteredDevice().getDeviceId());
         }
         devices.put(device);
      }
      Enumeration<CommPortIdentifier> ports = listPorts();
      while (ports.hasMoreElements()) {
         CommPortIdentifier portIdentifier = ports.nextElement();
         JSONObject device = new JSONObject();
         device.put("name", portIdentifier.getName());
         device.put("currentOwner", portIdentifier.getCurrentOwner());
         device.put("connected", false);
         devices.put(device);
      }
      json.put("devices", devices);
      return json;
   }

   private void readDevices() {
      try {
         Connection conn = DatabaseConnector.getInstance().connect();

         try (PreparedStatement stmt = conn.prepareStatement("select * from config.device")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
               RegisteredDevice device = getDeviceByRuntimeId(rs.getString("runtime_id"));
               if (device == null) {
                  device = new RegisteredDevice();
                  device.setRuntimeId(rs.getString("runtime_id"));
                  devices.add(device);
               }
               device.setDeviceId(rs.getInt("id"));
            }
         }
      } catch (Exception ex) {
         ex.printStackTrace();
      }
   }

   public void clearDevices() {
      devices.clear();
   }

   /**
    * Finds a device with runtimeId
    *
    * @param runtimeId
    * @return
    */
   public RegisteredDevice getDeviceByRuntimeId(String runtimeId) {
      for (RegisteredDevice device : devices) {
         if (device.getRuntimeId().equalsIgnoreCase(runtimeId)) {
            return device;
         }
      }
      return null;
   }

   /**
    *
    * @param refreshDevices
    * @throws Exception
    */
   public void scanDevices(boolean refreshDevices) throws Exception {
      if (pauseScan) {
         return;
      }
      try {
         runningScan = true;
         if (refreshDevices || devices.isEmpty()) {
            readDevices();
         }

         for (int i = 0; i < devices.size(); i++) {
            RegisteredDevice device = devices.get(i);
            boolean found = false;
            // verifies if connect
            if (connectedPorts.containsValue(device.getConnector())) {
               device.getConnector().sendCommand("a");
               found = true;
               continue;
            }

            // loops on connected ports to find runtimeId
            for (Device connectedPort : listConnectedPorts()) {
               if (device.getRuntimeId().equalsIgnoreCase(connectedPort.getRuntimeId())) {
                  device.setConnector(connectedPort);
                  device.setPort(connectedPort.getName());
                  Controller.getLogger().log(Level.FINEST, "Found device {0} with runtimeId {1} already connected", new Object[]{connectedPort.getName(), device.getRuntimeId()});
                  found = true;
                  break;
               }
            }

            // loops on disconnected ports
            if (!found) {
               Enumeration<CommPortIdentifier> ports = listPorts();
               while (ports.hasMoreElements()) {
                  CommPortIdentifier port = ports.nextElement();
                  Device p = connect(port);
                  if (p.getRuntimeId() != null && !p.getRuntimeId().equals(Device.BLANK_GUID)) {

                     if (device.getRuntimeId().equalsIgnoreCase(p.getRuntimeId())) {
                        device.setConnector(p);
                        Controller.getLogger().log(Level.INFO, "Connected with device {0} id {1} with runtimeId {2}", new Object[]{p.getName(), device.getDeviceId(), device.getRuntimeId()});
                        found = true;
                        break;
                     } else {
                        RegisteredDevice otherDevice = getDeviceByRuntimeId(p.getRuntimeId());
                        if (otherDevice == null) {
                           otherDevice = new RegisteredDevice();
                           otherDevice.setConnector(p);
                           Controller.getLogger().log(Level.INFO, "Connected with unregistered device {0} with runtimeId {1}", new Object[]{p.getName(), otherDevice.getRuntimeId()});
                           devices.add(otherDevice);
                        } else {
                           Controller.getLogger().log(Level.INFO, "Connected with device {0} id {1} with runtimeId {2}", new Object[]{p.getName(), otherDevice.getDeviceId(), otherDevice.getRuntimeId()});
                           otherDevice.setConnector(p);
                        }
                     }

                  } else {
                     Controller.getLogger().log(Level.INFO, "Connected with device {0} with runtimeId {1}", new Object[]{p.getName(), device.getRuntimeId()});
                     p.close();
                  }
               }
            }

            if (!found) {
               Controller.getLogger().log(Level.INFO, "Expected device with runtimeId {0} not available", new Object[]{device.getRuntimeId()});
            }
         }
      } finally {
         runningScan = false;
      }
   }

   public void pause() {
      pauseScan = true;
      while (runningScan);
   }

   public void resume() {
      pauseScan = false;
   }

   public void registerEvent(DeviceEvent event) {
      events.add(event);
   }

   /**
    * Register all events on local database
    *
    * @throws java.sql.SQLException
    * @throws java.io.IOException
    */
   public void localRegisterEvents() throws SQLException, IOException {
      Controller.getLogger().log(Level.INFO, "Local database event synchronizing");
      Connection conn = DatabaseConnector.getInstance().connect();
      String sql
              = "insert into config.device_event\n"
              + "   (device_id, event_type, timestamp, event_data)\n"
              + "values\n"
              + "   (?, ?, ?, ?::json)";
      try (PreparedStatement stmt = conn.prepareStatement(sql)) {
         synchronized (events) {
            while (!events.isEmpty()) {
               DeviceEvent event = events.peek();
               stmt.setInt(1, event.getDevice().getRegisteredDevice().getDeviceId());
               stmt.setString(2, event.getEventType());
               stmt.setTimestamp(3, new java.sql.Timestamp(event.getDate().getTime()));
               stmt.setString(4, event.getJSON().toString());
               stmt.execute();
               // removes from list
               events.poll();
            }
         }
      }
   }
}
