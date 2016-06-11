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
      System.out.println("Content-type: "+exchange.getRequestHeaders().getFirst("Content-type"));
      System.out.println("method: "+exchange.getRequestMethod());
      //
      //System.out.println("data: "+data.toString());
      OutputStream os = exchange.getResponseBody();
      try {
         String path = exchange.getRequestURI().getPath();

         if (path.equals("/device/list")) {
            JSONObject json = DeviceManager.getInstance().jsonListPorts();
            byte[] response = json.toString().getBytes();
            exchange.sendResponseHeaders(200, response.length);
            os.write(response);
         } else if (path.equals("/device/connect")) {
            JSONObject data = new JSONObject(new JSONTokener(exchange.getRequestBody()));
            
            
            // Conecta a um arduino já preparado
            JSONObject json = new JSONObject();

            try {
               DeviceManager.getInstance().connect(data.getString("portName"));
               json.put("success", true);
               exchange.sendResponseHeaders(200, json.toString().getBytes().length);
            } catch (Exception ex) {
               json.put("success", false);
               json.put("message", "test"+ex.getMessage());
               exchange.getResponseHeaders().add("Content-type", "application/json");
               exchange.sendResponseHeaders(500, json.toString().getBytes().length);
            }
            os.write(json.toString().getBytes());
         } else if (path.equals("/device/disconnect")) {
            // Desconecta do arduino
            JSONObject json = new JSONObject();
            HashMap<String, String> urlParameters = getUrlParameters(exchange);
            Device connector = DeviceManager.getInstance().get(urlParameters.get("device"));
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
            Device connector = DeviceManager.getInstance().get(urlParameters.get("device"));
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
            Device connector = DeviceManager.getInstance().connect(urlParameters.get("device"));
            String result = connector.sendCommand(urlParameters.get("command"));
            JSONObject json = new JSONObject();
            json.put("success", true);
            json.put("result", result);
            exchange.sendResponseHeaders(200, json.toString().getBytes().length);
            os.write(json.toString().getBytes());
         } else if (path.equals("/device/program")) {
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
         } else if (path.equals("/device/states")) {
            JSONObject data = new JSONObject(new JSONTokener(exchange.getRequestBody()));
            Device connector = DeviceManager.getInstance().connect(data.getString("portName"));

            exchange.getResponseHeaders().add("Content-type", "application/json");
            //exchange.getResponseHeaders().add(, "application/json");
            exchange.sendResponseHeaders(200, connector.getPortStates().toString().getBytes().length);
            os.write(connector.getPortStates().toString().getBytes());
         } else if (path.equals("/device/models")) {
            DeviceModels models = new DeviceModels();
            models.init();
            JSONObject result = models.toJSON();

            exchange.sendResponseHeaders(200, result.toString().getBytes().length);
            os.write(result.toString().getBytes());
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
