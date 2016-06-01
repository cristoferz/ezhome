package br.com.ezhome.device;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.RXTXPort;
import gnu.io.SerialPort;
import java.io.IOException;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Especifies a connection with a SerialPort, generally used for devices
 *
 * @author cristofer
 */
public class PortConnector {

   /**
    * Default connection timeout to devices 2s
    */
   public static final int TIMEOUT = 2000;
   /**
    * Default command timeout 2s
    */
   public static final int COMMAND_TIMEOUT = 2000;

   private final PortManager manager;
   private PortReader reader;
   private PortWriter writer;

   private RXTXPort serialPort;

   private PortReaderAdapter commandReceiver;
   private String receivedMessage;

   private String runtimeId, versionId;

   private HashMap<Integer, PortState> portStates;

   public static final int INPUT = 1, OUTPUT = 2, INTERNAL_ADDRESS = 4;

   /**
    * Initializes a connection with device on portIdentifier
    *
    * @param manager the PortManager
    * @param portIdentifier
    * @throws Exception
    */
   public PortConnector(PortManager manager, CommPortIdentifier portIdentifier) throws Exception {
      this.manager = manager;
      this.portStates = new HashMap<>();
      if (portIdentifier.isCurrentlyOwned()) {
         throw new Exception("Port " + portIdentifier.getName() + " is in use.");
      }

      commandReceiver = new PortReaderAdapter() {

         @Override
         public void lineReceived(String line) {
            processLine(line);
         }

         @Override
         public void messageReceived(String message) {
            receivedMessage = message;
         }

      };
      CommPort commPort = portIdentifier.open(this.getClass().getName(), TIMEOUT);
      if (commPort instanceof SerialPort) {
         serialPort = (RXTXPort) commPort;
         // default ezHome device connection parameters
         serialPort.setSerialPortParams(115200,
                 SerialPort.DATABITS_8,
                 SerialPort.STOPBITS_1,
                 SerialPort.PARITY_NONE);

         reader = new PortReader(this, serialPort.getInputStream());
         writer = new PortWriter(this, serialPort.getOutputStream());
         addReaderListener(commandReceiver);
         reader.start();
      }
      // Delay to ensure adequate response from device
      Thread.sleep(3000);
   }

   /**
    * Internal processing for lines received from device
    *
    * @param line
    */
   private void processLine(String line) {
      if (line != null) {
         if (line.startsWith("IS=")) {
            String[] parts = line.split("=", 2);
            String[] parts2 = parts[1].split(":");
            int address = Integer.parseInt(parts2[0]);
            boolean set = parts2[1].equals("1");
            setPortState(address, set, INPUT);
         } else if (line.startsWith("OS=")) {
            String[] parts = line.split("=", 2);
            String[] parts2 = parts[1].split(":");
            int address = Integer.parseInt(parts2[0]);
            boolean set = parts2[1].equals("1");
            setPortState(address, set, OUTPUT);
         } else if (line.startsWith("AS=")) {
            String[] parts = line.split("=", 2);
            String[] parts2 = parts[1].split(":");
            int address = Integer.parseInt(parts2[0]);
            boolean set = parts2[1].equals("1");
            setPortState(address, set, INTERNAL_ADDRESS);

         } else if (line.startsWith("RuntimeId=")) {
            String[] parts = line.split("=", 2);
            this.runtimeId = parts[1];//.substring(0, 15);
         } else if (line.startsWith("VersionId=")) {
            String[] parts = line.split("=", 2);
            this.versionId = parts[1];
         }
      }
   }

   private void setPortState(int address, boolean state, int type) {
      PortState portState = getPortState(address);
      portState.setState(state);
      if ((type & INPUT) == INPUT) {
         portState.setInput(true);
      }
      if ((type & OUTPUT) == OUTPUT) {
         portState.setInput(true);
      }
      if ((type & INTERNAL_ADDRESS) == INTERNAL_ADDRESS) {
         portState.setInput(true);
      }
      System.out.println("Port "+ address+ " state "+state);
   }

   public PortState getPortState(int address) {
      if (!portStates.containsKey(address)) {
         PortState result = new PortState(false, address, false, false, false);
         portStates.put(address, result);
         return result;
      } else {
         return portStates.get(address);
      }
   }
   
   public JSONObject getPortStates() {
      JSONObject result = new JSONObject();
      JSONArray states = new JSONArray();
      result.put("states", states);
      for (Integer key : portStates.keySet()) {
         JSONObject state = new JSONObject();
         state.put("address", portStates.get(key).getAddress());
         state.put("state", portStates.get(key).isState());
         state.put("input", portStates.get(key).isInput());
         state.put("output", portStates.get(key).isOutput());
         state.put("internalAddress", portStates.get(key).isInternalAddress());
         states.put(state);
      }
      return result;
   }
   
   private void printStates() {
      for (Integer keySet : portStates.keySet()) {
         System.out.println("AD: "+keySet + " state: "+ portStates.get(keySet).isState());
      }
   }

   /**
    * Returns serial port name for this connector
    *
    * @return
    */
   public String getName() {
      return serialPort.getName();
   }

   /**
    * Adds a readerListener for this connector
    *
    * @param listener
    */
   public void addReaderListener(PortReaderListener listener) {
      reader.addListener(listener);
   }

   /**
    * Removes especified listener from this connector
    *
    * @param listener
    * @return
    */
   public boolean removeReaderListener(PortReaderListener listener) {
      return reader.removeListener(listener);
   }

   /**
    * Returns the PortWriter from this connector
    *
    * @return
    */
   public PortWriter getWriter() {
      return writer;
   }

   /**
    * Send a command to device and waits for response until especified timeout
    * expires
    *
    * @param command command to be send
    * @param timeout time in millis to expires command
    * @return device response or null on command timeout
    * @throws IOException if a connection problem occurs
    */
   public String sendCommand(String command, int timeout) throws IOException {
      receivedMessage = null;
      getWriter().write((command + "\n").getBytes());

      long start = System.currentTimeMillis();
      while (receivedMessage == null) {
         if (System.currentTimeMillis() > start + timeout) {
            break;
         }
      }
      return receivedMessage;
   }

   /**
    * Send a command to device and waits for response until default
    * COMMAND_TIMEOUT expires
    *
    * @param command command to be send
    * @return device response or null on COMMAND_TIMEOUT
    * @throws IOException if a connection errors occur
    */
   public String sendCommand(String command) throws IOException {
      return sendCommand(command, COMMAND_TIMEOUT);
   }

   /**
    * Sends a command to device requesting runtimeId. Expected behavior is that
    * same runtimeId of this connector is returned by device. This request will
    * wait for response from device or default COMMAND_TIMEOUT expires
    *
    * @throws IOException if a connection problem occurs
    */
   public void requestRuntimeId() throws IOException {
      sendCommand("runtime-id");
   }

   /**
    * Returns runtimeId identified from device. This field identifies uniquely
    * every device on network
    *
    * @return a 32 chars unique identifier, named runtimeId
    */
   public String getRuntimeId() {
      return runtimeId;
   }

   /**
    * Returns current versionId identified from device.
    *
    * @return a 32 chars unique string, identified current version on device
    */
   public String getVersionId() {
      return this.versionId;
   }

   /**
    * Writes a byte array to device
    *
    * @param value
    * @throws IOException
    */
   public void write(byte[] value) throws IOException {
      getWriter().write(value);
   }

   /**
    * Close this connector and all its streams and connections
    *
    * @throws IOException
    */
   public void close() throws IOException {
      reader.close();
      writer.close();
      serialPort.close();
      manager.remove(this);
   }

   public class PortState {

      private boolean state;
      private int address;
      private boolean output, input, internalAddress;

      public PortState(boolean state, int address, boolean output, boolean input, boolean internalAddress) {
         this.state = state;
         this.address = address;
         this.output = output;
         this.input = input;
         this.internalAddress = internalAddress;
      }

      public boolean isState() {
         return state;
      }

      public void setState(boolean state) {
         this.state = state;
      }

      public int getAddress() {
         return address;
      }

      public void setAddress(int address) {
         this.address = address;
      }

      public boolean isOutput() {
         return output;
      }

      public void setOutput(boolean output) {
         this.output = output;
      }

      public boolean isInput() {
         return input;
      }

      public void setInput(boolean input) {
         this.input = input;
      }

      public boolean isInternalAddress() {
         return internalAddress;
      }

      public void setInternalAddress(boolean internalAddress) {
         this.internalAddress = internalAddress;
      }

   }

}
