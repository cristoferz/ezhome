package br.com.ezhome.device;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;

/**
 * Utilizado para a detecção dos equipamentos conectados ao raspberry
 *
 * @author cristofer
 */
public class PortManager {

   private static PortManager instance;

   private HashMap<String, PortConnector> connectedPorts;

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
}
