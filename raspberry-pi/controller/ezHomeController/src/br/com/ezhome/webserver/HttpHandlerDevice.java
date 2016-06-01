package br.com.ezhome.webserver;

import br.com.ezhome.device.FirmwareUploader;
import br.com.ezhome.device.PortConnector;
import br.com.ezhome.device.PortManager;
import br.com.ezhome.device.program.ProgramBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import gnu.io.CommPortIdentifier;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
public class HttpHandlerDevice extends HttpHandlerAbstract{

   @Override
   public void handle(HttpExchange exchange) throws IOException {
      OutputStream os = exchange.getResponseBody();
      try {
         String path = exchange.getRequestURI().getPath();

         if (path.equals("/device/list")) {
            
            JSONObject json = new JSONObject();
            JSONArray devices = new JSONArray();
            ArrayList<PortConnector> connectors = PortManager.getInstance().listConnectedPorts();
            for (PortConnector connector : connectors) {
               JSONObject device = new JSONObject();
               device.put("name", connector.getName());
               device.put("currentOwner", "self");
               device.put("connected", true);
               device.put("runtimeId", connector.getRuntimeId());
               devices.put(device);
            }
            Enumeration<CommPortIdentifier> ports = PortManager.getInstance().listPorts();
            while (ports.hasMoreElements()) {
               CommPortIdentifier portIdentifier = ports.nextElement();
               JSONObject device = new JSONObject();
               device.put("name", portIdentifier.getName());
               device.put("currentOwner", portIdentifier.getCurrentOwner());
               device.put("connected", false);
               devices.put(device);
            }
            json.put("devices", devices);
            exchange.sendResponseHeaders(200, json.toString().getBytes().length);
            os.write(json.toString().getBytes());
         } else if (path.equals("/device/connect")) {
            // Conecta a um arduino já preparado
            JSONObject json = new JSONObject();

            try {
               PortManager.getInstance().connect(getUrlParameters(exchange).get("device"));
               json.put("success", true);
               exchange.sendResponseHeaders(200, json.toString().getBytes().length);
            } catch (Exception ex) {
               json.put("success", false);
               json.put("message", ex.getMessage());
               exchange.sendResponseHeaders(500, json.toString().getBytes().length);
            }
            os.write(json.toString().getBytes());
         } else if (path.equals("/device/disconnect")) {
            // Desconecta do arduino
            JSONObject json = new JSONObject();
            HashMap<String, String> urlParameters = getUrlParameters(exchange);
            PortConnector connector = PortManager.getInstance().get(urlParameters.get("device"));
            if (connector == null) {
               json.put("success", false);
               json.put("message", "Device " + urlParameters.get("device") + " is not connected.");
               exchange.sendResponseHeaders(500, json.toString().getBytes().length);
            } else {
               connector.close();
               json.put("success", true);
               exchange.sendResponseHeaders(200, json.toString().getBytes().length);
            }
            os.write(json.toString().getBytes());
//         } else if (path.equals("/device/firmwareWrite")) {
//            // Grava o firmware no arduino
//         } else if (path.equals("/device/upload")) {
//            // Faz o carregamento do programa para o arduino
//         } else if (path.equals("/device/scan")) {
//            // Faz um scan de portas do arduino
//         } else if (path.equals("/device/upload")) {
//            
         } else if (path.equals("/device/upload")) {

            JSONObject json = new JSONObject();
            HashMap<String, String> urlParameters = getUrlParameters(exchange);
            // disconnect before upload
            PortConnector connector = PortManager.getInstance().get(urlParameters.get("device"));
            if (connector != null) {
               connector.close();
            }
//            PortConnector connector = PortManager.getInstance().connect(urlParameters.get("device"));
            FirmwareUploader uploader = new FirmwareUploader();
            uploader.setPort(urlParameters.get("device"));
            if (urlParameters.get("model") != null) {
               if (urlParameters.get("model").equalsIgnoreCase("UNO")) {
                  uploader.preloadConfig(FirmwareUploader.ARDUINO_UNO);
               } else if (urlParameters.get("model").equalsIgnoreCase("MEGA")) {
                  uploader.preloadConfig(FirmwareUploader.ARDUINO_MEGA);
               } else if (urlParameters.get("model").equalsIgnoreCase("NANO")) {
                  uploader.preloadConfig(FirmwareUploader.ARDUINO_NANO);
               } else {
                  throw new IllegalArgumentException("Invalid model " + urlParameters.get("model"));
               }
            } else {
               throw new IllegalArgumentException("Model cannot be null");
            }
            FirmwareUploader.UploadResult result = uploader.upload();
            if (result.getExitStatus() == 0) {
               json.put("success", true);
            } else {
               json.put("success", false);
               json.put("exitStatus", result.getExitStatus());
            }
            json.put("result", result.getOutput());
            exchange.sendResponseHeaders(200, json.toString().getBytes().length);
            os.write(json.toString().getBytes());

         } else if (path.equals("/device/send")) {
            HashMap<String, String> urlParameters = getUrlParameters(exchange);
            PortConnector connector = PortManager.getInstance().connect(urlParameters.get("device"));
            String result = connector.sendCommand(urlParameters.get("command"));
            JSONObject json = new JSONObject();
            json.put("success", true);
            json.put("result", result);
            exchange.sendResponseHeaders(200, json.toString().getBytes().length);
            os.write(json.toString().getBytes());
         } else if (path.equals("/device/program")) {
            HashMap<String, String> postParameters = getPostParameters(exchange);
            PortConnector connector = PortManager.getInstance().connect(postParameters.get("device"));
            String program = postParameters.get("program");
            JSONObject obj = new JSONObject(program);
            ProgramBuilder builder = new ProgramBuilder((byte) 0x8, (byte) 0x8, "0123456789ABCDEFFEDCBA9876543210", "0123456789ABCDEFFEDCBA9876543210");
            builder.loadJSON(obj);
            builder.sendProgram(connector);

            JSONObject json = new JSONObject();
            json.put("success", true);
            json.put("result", "OK");
            exchange.sendResponseHeaders(200, json.toString().getBytes().length);
            os.write(json.toString().getBytes());
         } else if (path.equals("/device/states")) {
            HashMap<String, String> urlParameters = getUrlParameters(exchange);
            PortConnector connector = PortManager.getInstance().connect(urlParameters.get("device"));
            
            exchange.sendResponseHeaders(200, connector.getPortStates().toString().getBytes().length);
            os.write(connector.getPortStates().toString().getBytes());
         } else {
            String response = "Invalid URL: " + path;
            exchange.sendResponseHeaders(404, response.getBytes().length);
            os.write(response.getBytes());
         }
      } catch (Throwable ex) {
         ex.printStackTrace();
         JSONObject json = new JSONObject();
         json.put("success", false);
         json.put("message", ex.getMessage());
         json.put("exception", ex);
         exchange.sendResponseHeaders(500, json.toString().getBytes().length);
         os.write(json.toString().getBytes());
      } finally {
         os.close();
      }
   }

}
