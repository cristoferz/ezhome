package br.com.ezhome.webserver;

import br.com.ezhome.device.FirmwareUploader;
import br.com.ezhome.device.Device;
import br.com.ezhome.device.DeviceManager;
import br.com.ezhome.device.model.DeviceModels;
import br.com.ezhome.device.program.ProgramBuilder;
import com.sun.net.httpserver.HttpExchange;
import gnu.io.CommPortIdentifier;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 *
 * @author cristofer
 */
public class HttpHandlerDevice extends HttpHandlerAbstract {

   @Override
   public void handle(HttpExchange exchange) throws IOException {
      //
      //System.out.println("data: "+data.toString());
      OutputStream os = exchange.getResponseBody();
      try {
         String path = exchange.getRequestURI().getPath();

         switch (path) {
            case "/device/list":
               if (exchange.getRequestMethod().equals("GET")) {
                  exchange.getResponseHeaders().add("Content-type", "application/json");
                  JSONObject json = DeviceManager.getInstance().jsonListPorts();
                  byte[] response = json.toString().getBytes();
                  exchange.sendResponseHeaders(200, response.length);
                  os.write(response);

               } else {
                  throw new Exception("Invalid method: " + exchange.getRequestMethod());
               }
               break;
            case "/device/connect":
               switch (exchange.getRequestMethod()) {
                  case "POST":
                     exchange.getResponseHeaders().add("Content-type", "application/json");
                     JSONObject json = new JSONObject();
                     try {
                        DeviceManager.getInstance().connect(getJSONRequest(exchange).getString("portName"));
                        json.put("success", true);
                        exchange.sendResponseHeaders(200, json.toString().getBytes().length);
                     } catch (Exception ex) {
                        json.put("success", false);
                        json.put("message", ex.getMessage());
                        json.put("exception", ex);
                        exchange.sendResponseHeaders(500, json.toString().getBytes().length);
                     }
                     os.write(json.toString().getBytes());
                     break;
                  default:
                     throw new Exception("Invalid method: " + exchange.getRequestMethod());
               }
               break;
            case "/device/disconnect":
               switch (exchange.getRequestMethod()) {
                  case "POST":
                     exchange.getResponseHeaders().add("Content-type", "application/json");
                     // Desconecta do arduino
                     JSONObject json = new JSONObject();
                     JSONObject parameters = getJSONRequest(exchange);
                     Device connector = DeviceManager.getInstance().get(parameters.getString("portName"));
                     if (connector == null) {
                        json.put("success", false);
                        json.put("message", "Device " + parameters.getString("portName") + " is not connected.");
                        exchange.sendResponseHeaders(500, json.toString().getBytes().length);
                     } else {
                        connector.close();
                        json.put("success", true);
                        exchange.sendResponseHeaders(200, json.toString().getBytes().length);
                     }
                     os.write(json.toString().getBytes());
                     break;
                  default:
                     throw new Exception("Invalid method: " + exchange.getRequestMethod());
               }

//         } else if (path.equals("/device/firmwareWrite")) {
//            // Grava o firmware no arduino
//         } else if (path.equals("/device/upload")) {
//            // Faz o carregamento do programa para o arduino
//         } else if (path.equals("/device/scan")) {
//            // Faz um scan de portas do arduino
               break;
            case "/device/upload":
               switch (exchange.getRequestMethod()) {
                  case "POST":
                     exchange.getResponseHeaders().add("Content-type", "application/json");
                     JSONObject json = new JSONObject();
                     JSONObject parameters = getJSONRequest(exchange);
                     // disconnect before upload
                     Device connector = DeviceManager.getInstance().get(parameters.getString("portName"));
                     if (connector != null) {
                        connector.close();
                     }
//            PortConnector connector = PortManager.getInstance().connect(urlParameters.get("device"));
                     FirmwareUploader uploader = new FirmwareUploader();
                     uploader.setPort(parameters.getString("portName"));
                     if (parameters.getString("model") != null) {
                        switch (parameters.getString("model").toUpperCase()) {
                           case "UNO":
                              uploader.preloadConfig(FirmwareUploader.ARDUINO_UNO);
                              break;
                           case "MEGA":
                              uploader.preloadConfig(FirmwareUploader.ARDUINO_MEGA);
                              break;
                           case "NANO":
                              uploader.preloadConfig(FirmwareUploader.ARDUINO_NANO);
                              break;
                           default:
                              throw new IllegalArgumentException("Invalid model " + parameters.getString("model"));
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
                     break;
                  default:
                     throw new Exception("Invalid method: " + exchange.getRequestMethod());
               }
               break;
            case "/device/send":
               switch (exchange.getRequestMethod()) {
                  case "POST":
                     exchange.getResponseHeaders().add("Content-type", "application/json");
                     JSONObject parameters = getJSONRequest(exchange);
                     Device connector = DeviceManager.getInstance().connect(parameters.getString("portName"));
                     String result = connector.sendCommand(parameters.getString("command"));
                     JSONObject json = new JSONObject();
                     json.put("success", true);
                     json.put("result", result);
                     exchange.sendResponseHeaders(200, json.toString().getBytes().length);
                     os.write(json.toString().getBytes());
                     break;
                  default:
                     throw new Exception("Invalid method: " + exchange.getRequestMethod());
               }
               break;
            case "/device/program":
               HashMap<String, String> postParameters = getPostParameters(exchange);
               Device connector = DeviceManager.getInstance().connect(postParameters.get("device"));
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
               break;
            case "/device/states": 
               switch (exchange.getRequestMethod()) {
                  case "POST":
                     exchange.getResponseHeaders().add("Content-type", "application/json");
                     JSONObject data = getJSONRequest(exchange);
                     Device connector2 = DeviceManager.getInstance().connect(data.getString("portName"));
                     //exchange.getResponseHeaders().add(, "application/json");
                     exchange.sendResponseHeaders(200, connector2.getPortStates().toString().getBytes().length);
                     os.write(connector2.getPortStates().toString().getBytes());
                     break;
                  default:
                     throw new Exception("Invalid method: " + exchange.getRequestMethod());
               }
               break;
            case "/device/models": {
               DeviceModels models = new DeviceModels();
               models.init();
               JSONObject result = models.toJSON();
               exchange.sendResponseHeaders(200, result.toString().getBytes().length);
               os.write(result.toString().getBytes());
               break;
            }
            default:
               String response = "Invalid URL: " + path;
               exchange.sendResponseHeaders(404, response.getBytes().length);
               os.write(response.getBytes());
               break;
         }
      } catch (Throwable ex) {
         exchange.getResponseHeaders().add("Content-type", "application/json");
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
