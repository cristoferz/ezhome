package br.com.ezhome.webserver;

import br.com.ezhome.device.FirmwareUploader;
import br.com.ezhome.device.DeviceImpl;
import br.com.ezhome.device.DeviceManager;
import br.com.ezhome.device.model.DeviceModels;
import br.com.ezhome.lib.LibManager;
import br.com.ezhome.lib.compiler.EzHomeCompiler;
import br.com.ezhome.lib.program.ProgramBuilder;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import org.json.JSONObject;

/**
 *
 * @author cristofer
 */
public class HttpHandlerDevice extends HttpHandlerAbstract {

   @Override
   public void handleRequest(HttpExchange exchange) throws IOException {
      //
      //System.out.println("data: "+data.toString());
      OutputStream os = exchange.getResponseBody();
      exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
      try {
         String path = exchange.getRequestURI().getPath();

         switch (path) {
            case "/device/upload":
               switch (exchange.getRequestMethod()) {
                  case "POST":
                     exchange.getResponseHeaders().add("Content-type", "application/json");
                     JSONObject json = new JSONObject();
                     JSONObject parameters = getJSONRequest(exchange);
                     // disconnect before upload
                     DeviceImpl connector = DeviceManager.getInstance().get(parameters.getString("portName"));
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
                     DeviceImpl connector = DeviceManager.getInstance().connect(parameters.getString("portName"));
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
               switch (exchange.getRequestMethod()) {
                  case "POST":
                     HashMap<String, String> postParameters = getPostParameters(exchange);
                     DeviceImpl connector = DeviceManager.getInstance().connect(postParameters.get("device"));
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
                  default:
                     throw new Exception("Invalid method: " + exchange.getRequestMethod());
               }
               break;
            case "/device/compileAndSend":
               switch (exchange.getRequestMethod()) {
                  case "POST":
                     JSONObject request = getJSONRequest(exchange);
                     
                     EzHomeCompiler compiler = new EzHomeCompiler();
                     compiler.parse(request.getJSONObject("program"));
                     compiler.setDeviceModel(LibManager.getInstance().getDeviceModel(request.getString("deviceModel")));
                     ProgramBuilder builder = new ProgramBuilder((byte) 0x8, (byte) 0x8, request.getString("runtimeId"), request.getString("versionId"));
                     compiler.compile(builder);
                     DeviceImpl connector = DeviceManager.getInstance().connect(request.getString("portName"));
                     //System.out.println(builder.toJSON().toString(3));
                     builder.sendProgram(connector);
                     JSONObject json = new JSONObject();
                     json.put("success", true);
                     json.put("result", "OK");
                     exchange.sendResponseHeaders(200, json.toString().getBytes().length);
                     os.write(json.toString().getBytes());
                     break;
                  default:
                     throw new Exception("Invalid method: " + exchange.getRequestMethod());
               }
               break;
            case "/device/states": 
               switch (exchange.getRequestMethod()) {
                  case "POST":
                     exchange.getResponseHeaders().add("Content-type", "application/json");
                     JSONObject data = getJSONRequest(exchange);
                     DeviceImpl connector2 = DeviceManager.getInstance().connect(data.getString("portName"));
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
