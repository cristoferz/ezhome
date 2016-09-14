package br.com.ezhome.webserver.context;

import br.com.ezhome.device.Device;
import br.com.ezhome.device.DeviceManager;
import br.com.ezhome.device.program.ProgramBuilder;
import br.com.ezhome.webserver.HttpHandlerJWTAbstract;
import com.sun.net.httpserver.HttpExchange;
import gnu.io.CommPortIdentifier;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author cristofer
 */
public class HttpHandlerConfigSerialPort extends HttpHandlerJWTAbstract {

   @Override
   public void handleRequest(HttpExchange he) throws Exception {
      try (OutputStream os = he.getResponseBody()) {
         String path = he.getRequestURI().getPath();
         switch (path) {
            case "/config/serialPort":
               switch (he.getRequestMethod()) {
                  case "GET":
                     listSerialPort(he, os);
                     break;
                  case "POST":
                     connectPort(he, os);
                     break;
                  case "DELETE":
                     disconnectPort(he, os);
                     break;
                  default:
                     throw new Exception("Invalid method \"" + he.getRequestMethod() + "\" for path \"" + path + "\"");
               }
               break;
            case "/config/serialPort/scan":
               switch (he.getRequestMethod()) {
                  case "POST":
                     scanPorts(he, os);
                     break;
                  default:
                     throw new Exception("Invalid method \"" + he.getRequestMethod() + "\" for path \"" + path + "\"");
               }
               break;
            case "/config/serialPort/autoScan":
               switch (he.getRequestMethod()) {
                  case "POST":
                     autoScan(he, os);
                     break;
                  default:
                     throw new Exception("Invalid method \"" + he.getRequestMethod() + "\" for path \"" + path + "\"");
               }
               break;
            case "/config/serialPort/program":
               switch (he.getRequestMethod()) {
                  case "POST":
                     program(he, os);
                     break;
                  default:
                     throw new Exception("Invalid method \"" + he.getRequestMethod() + "\" for path \"" + path + "\"");
               }
               break;
            default:
               String result = "Invalid path \"" + path + "\"";
               he.sendResponseHeaders(404, result.getBytes().length);
               os.write(result.getBytes());
               break;
         }
      }
   }

   /**
    * Lists all serial ports available to controller<br>
    * <br>
    * GET /config/serialPort?connected=%value%&portName=%value%<br>
    * <br>
    * <table><tr><td>Name</td><td>Required</td><td>Values</td><td>Description</td></tr>
    * <tr><td>connected</td><td>false</td><td>Boolean</td><td>Filter only ports
    * on specified state</td></tr>
    * <tr><td>portName</td><td>false</td><td>String</td><td>Filter only
    * specified port</td></tr></table>
    *
    * @param he
    * @param os
    * @throws IOException
    */
   private void listSerialPort(HttpExchange he, OutputStream os) throws IOException {
      HashMap<String, String> params = getUrlParameters(he);

      JSONObject json = new JSONObject();
      JSONArray devices = new JSONArray();
      if (!params.containsKey("connected") || params.get("connected").equals("true")) {
         ArrayList<Device> connectors = DeviceManager.getInstance().listConnectedPorts();
         for (Device connector : connectors) {
            if (!params.containsKey("portName") || params.get("portName").equals(connector.getName())) {
               JSONObject device = new JSONObject();
               device.put("portName", connector.getName());
               device.put("currentOwner", "self");
               device.put("connected", true);
               device.put("runtimeId", connector.getRuntimeId());
               device.put("versionId", connector.getVersionId());
               if (connector.getRegisteredDevice() != null) {
                  device.put("deviceId", connector.getRegisteredDevice().getDeviceId());
               }
               devices.put(device);
            }
         }
      }

      if (!params.containsKey("connected") || params.get("connected").equals("false")) {
         Enumeration<CommPortIdentifier> ports = DeviceManager.getInstance().listPorts();
         while (ports.hasMoreElements()) {
            CommPortIdentifier portIdentifier = ports.nextElement();
            if (!params.containsKey("portName") || params.get("portName").equals(portIdentifier.getName())) {
               JSONObject device = new JSONObject();
               device.put("portName", portIdentifier.getName());
               device.put("currentOwner", portIdentifier.getCurrentOwner());
               device.put("connected", false);
               devices.put(device);
            }
         }
         json.put("devices", devices);
      }

      byte[] response = json.toString().getBytes();
      he.sendResponseHeaders(200, response.length);
      he.getResponseHeaders().add("Content-type", "application/json");
      os.write(response);
   }

   private void connectPort(HttpExchange he, OutputStream os) throws Exception {
      he.getResponseHeaders().add("Content-type", "application/json");
      JSONObject json = new JSONObject();
      DeviceManager.getInstance().connect(getJSONRequest(he).getString("portName"));
      json.put("success", true);
      he.sendResponseHeaders(200, json.toString().getBytes().length);
      os.write(json.toString().getBytes());
   }

   private void disconnectPort(HttpExchange he, OutputStream os) throws IOException {
      he.getResponseHeaders().add("Content-type", "application/json");
      JSONObject json = new JSONObject();
      JSONObject parameters = getJSONRequest(he);
      Device connector = DeviceManager.getInstance().get(parameters.getString("portName"));
      if (connector == null) {
         json.put("success", false);
         json.put("message", "Device " + parameters.getString("portName") + " is not connected.");
         he.sendResponseHeaders(500, json.toString().getBytes().length);
      } else {
         connector.close();
         json.put("success", true);
         he.sendResponseHeaders(200, json.toString().getBytes().length);
      }
      os.write(json.toString().getBytes());
   }

   private void scanPorts(HttpExchange he, OutputStream os) {
      throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
   }

   private void autoScan(HttpExchange he, OutputStream os) {
      throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
   }

   private void program(HttpExchange he, OutputStream os) throws Exception {
      he.getResponseHeaders().add("Content-type", "application/json");
      JSONObject json = new JSONObject();
      JSONObject parameters = getJSONRequest(he);
      Device device = DeviceManager.getInstance().connect(parameters.getString("portName"));
      ProgramBuilder builder = new ProgramBuilder((byte) 0x8, (byte) 0x8, "0123456789ABCDEFFEDCBA9876543210", "0123456789ABCDEFFEDCBA9876543210");
      builder.loadJSON(parameters.getJSONObject("program"));
      builder.sendProgram(device);
      json.put("success", true);
      json.put("result", "OK");
      he.sendResponseHeaders(200, json.toString().getBytes().length);
      os.write(json.toString().getBytes());
   }
}
