package br.com.ezhome.device;

import br.com.ezhome.Controller;
import br.com.ezhome.config.ConfigFile;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;

/**
 * Utilizado para a detecção dos equipamentos conectados ao raspberry
 *
 * @author cristofer
 */
public class PortManager {

   private static PortManager instance;

   private HashMap<String, PortConnector> connectedPorts;

   private ArrayList<PortDevice> devices;

   /**
    * Returns de Manager single instance
    *
    * @return
    */
   public static PortManager getInstance() {
      if (instance == null) {
         instance = new PortManager();
      }
      return instance;
   }

   /**
    * Inittializes the Port Manager
    */
   private PortManager() {
      connectedPorts = new HashMap<>();
      devices = new ArrayList<>();
   }

   /**
    * Connects with the specified portIdentifier. If there is an active
    * connection with this port, return this connection
    *
    * @param identifier
    * @return
    * @throws Exception
    */
   public PortConnector connect(CommPortIdentifier identifier) throws Exception {
      PortConnector connector = connectedPorts.get(identifier.getName());
      if (connector == null) {
         connector = new PortConnector(this, identifier);
         connectedPorts.put(identifier.getName(), connector);
      }
      return connector;
   }

   public PortConnector connect(String name) throws Exception {
      return connect(CommPortIdentifier.getPortIdentifier(name));
   }

   public PortConnector get(String name) {
      return connectedPorts.get(name);
   }

   protected void remove(PortConnector connector) {
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

   public ArrayList<PortConnector> listConnectedPorts() {
      ArrayList<PortConnector> connectors = new ArrayList<>();
      for (String key : connectedPorts.keySet()) {
         connectors.add(connectedPorts.get(key));
      }
      return connectors;
   }

   private void readDevices() {
      try {

         String url = "jdbc:postgresql://" + ConfigFile.getInstance().getDatabaseHost() + ":" + ConfigFile.getInstance().getDatabasePort() + "/" + ConfigFile.getInstance().getDatabaseName();
         Properties props = new Properties();
         props.setProperty("user", ConfigFile.getInstance().getDatabaseUsername());
         props.setProperty("password", ConfigFile.getInstance().getDatabasePassword());
         //props.setProperty("ssl", "true");
         Connection conn = DriverManager.getConnection(url, props);

         try (PreparedStatement stmt = conn.prepareStatement("select * from config.device")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
               PortDevice device = getDeviceByRuntimeId(rs.getString("runtime_id"));
               if (device == null) {
                  device = new PortDevice();
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
   public PortDevice getDeviceByRuntimeId(String runtimeId) {
      for (PortDevice device : devices) {
         if(device.getRuntimeId().equalsIgnoreCase(runtimeId)) {
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
      if (refreshDevices || devices.isEmpty()) {
         readDevices();
      }

      for (PortDevice device : devices) {
         boolean found = false;
         // verifies if connect
         if (connectedPorts.containsValue(device.getConnector())) {
            device.getConnector().sendCommand("a");
            found = true;
            continue;
         }

         // loops on connected ports to find runtimeId
         for (PortConnector connectedPort : listConnectedPorts()) {
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
               PortConnector p = connect(port);
               if (device.getRuntimeId().equalsIgnoreCase(p.getRuntimeId())) {
                  device.setConnector(p);
                  device.setPort(p.getName());
                  Controller.getLogger().log(Level.INFO, "Connected with device {0} with runtimeId {1}", new Object[]{ p.getName(), device.getRuntimeId()});
                  found = true;
                  break;
               } else {
                  p.close();
               }
            }
         }

         if (!found) {
            Controller.getLogger().log(Level.FINER, "Expected device with runtimeId {0} not available", new Object[]{ device.getRuntimeId()});
         }
      }
   }
}
