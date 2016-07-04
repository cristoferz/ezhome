package br.com.ezhome.webserver.context;

import br.com.ezhome.device.Device;
import br.com.ezhome.device.DeviceManager;
import br.com.ezhome.device.FirmwareUploader;
import br.com.ezhome.webserver.HttpHandlerAbstract;
import br.com.ezhome.webserver.HttpHandlerJWTAbstract;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import org.json.JSONObject;

/**
 *
 * @author cristofer
 */
public class HttpHandlerConfigDevice extends HttpHandlerJWTAbstract {

   @Override
   public void handleRequest(HttpExchange he) throws Exception {
      OutputStream os = he.getResponseBody();
      try {
         String path = he.getRequestURI().getPath();
         switch (path) {
            case "/config/device":
               switch (he.getRequestMethod()) {
                  case "GET":
                     list(he, os);
                     break;
                  case "POST":
                     insert(he, os);
                     break;
                  case "PUT":
                     update(he, os);
                     break;
                  case "DELETE":
                     delete(he, os);
                     break;
                  default:
                     throw new Exception("Invalid method \"" + he.getRequestMethod() + "\" for path \"" + path + "\"");
               }
               break;
            case "/device/send":
               switch (he.getRequestMethod()) {
                  case "POST":
                     sendCommand(he, os);
                     break;
                  default:
                     throw new Exception("Invalid method \"" + he.getRequestMethod() + "\" for path \"" + path + "\"");
               }
               break;
            case "/device/initialize":
               switch (he.getRequestMethod()) {
                  case "POST":
                     initialize(he, os);
                     break;
                  default:
                     throw new Exception("Invalid method \"" + he.getRequestMethod() + "\" for path \"" + path + "\"");
               }
               break;
            case "/device/upgrade":
               switch (he.getRequestMethod()) {
                  case "POST":
                     upgrade(he, os);
                     break;
                  default:
                     throw new Exception("Invalid method \"" + he.getRequestMethod() + "\" for path \"" + path + "\"");
               }
               break;
            case "/device/config-reset":
               switch (he.getRequestMethod()) {
                  case "POST":
                     configReset(he, os);
                     break;
                  default:
                     throw new Exception("Invalid method \"" + he.getRequestMethod() + "\" for path \"" + path + "\"");
               }
               break;
            case "/device/factory-reset":
               switch (he.getRequestMethod()) {
                  case "POST":
                     factoryReset(he, os);
                     break;
                  default:
                     throw new Exception("Invalid method \"" + he.getRequestMethod() + "\" for path \"" + path + "\"");
               }
               break;
            case "/device/reset":
               switch (he.getRequestMethod()) {
                  case "POST":
                     reset(he, os);
                     break;
                  default:
                     throw new Exception("Invalid method \"" + he.getRequestMethod() + "\" for path \"" + path + "\"");
               }
               break;
            case "/device/pause":
               switch (he.getRequestMethod()) {
                  case "POST":
                     pause(he, os);
                     break;
                  default:
                     throw new Exception("Invalid method \"" + he.getRequestMethod() + "\" for path \"" + path + "\"");
               }
               break;
            case "/device/resume":
               switch (he.getRequestMethod()) {
                  case "POST":
                     resume(he, os);
                     break;
                  default:
                     throw new Exception("Invalid method \"" + he.getRequestMethod() + "\" for path \"" + path + "\"");
               }
               break;
            case "/device/information":
               switch (he.getRequestMethod()) {
                  case "POST":
                     information(he, os);
                     break;
                  default:
                     throw new Exception("Invalid method \"" + he.getRequestMethod() + "\" for path \"" + path + "\"");
               }
               break;
            case "/device/device-config":
               switch (he.getRequestMethod()) {
                  case "POST":
                     deviceConfig(he, os);
                     break;
                  default:
                     throw new Exception("Invalid method \"" + he.getRequestMethod() + "\" for path \"" + path + "\"");
               }
               break;
            case "/device/read-boolean":
               switch (he.getRequestMethod()) {
                  case "POST":
                     readBoolean(he, os);
                     break;
                  default:
                     throw new Exception("Invalid method \"" + he.getRequestMethod() + "\" for path \"" + path + "\"");
               }
               break;
            case "/device/model":
               switch (he.getRequestMethod()) {
                  case "GET":
                     listModel(he, os);
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
      } finally {
         os.close();
      }
   }

   private void list(HttpExchange he, OutputStream os) throws IOException {
      he.getResponseHeaders().add("Content-type", "application/json");
      JSONObject result = new JSONObject();
      he.sendResponseHeaders(200, result.toString().getBytes().length);
      os.write(result.toString().getBytes());
      
      
   }

   private void insert(HttpExchange he, OutputStream os) {
      throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
   }

   private void update(HttpExchange he, OutputStream os) {
      throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
   }

   private void delete(HttpExchange he, OutputStream os) {
      throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
   }

   private void sendCommand(HttpExchange he, OutputStream os) throws Exception {
      he.getResponseHeaders().add("Content-type", "application/json");
      JSONObject parameters = getJSONRequest(he);
      Device connector = DeviceManager.getInstance().connect(parameters.getString("portName"));
      String result = connector.sendCommand(parameters.getString("command"));
      JSONObject json = new JSONObject();
      json.put("success", true);
      json.put("result", result);
      he.sendResponseHeaders(200, json.toString().getBytes().length);
      os.write(json.toString().getBytes());
   }

   private void initialize(HttpExchange he, OutputStream os) throws IOException, InterruptedException {
      // TODO generate RuntimeId and apply to device
      
      he.getResponseHeaders().add("Content-type", "application/json");
      JSONObject json = new JSONObject();
      JSONObject parameters = getJSONRequest(he);
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
      he.sendResponseHeaders(200, json.toString().getBytes().length);
      os.write(json.toString().getBytes());

   }

   private void upgrade(HttpExchange he, OutputStream os) {
      throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
   }

   private void configReset(HttpExchange he, OutputStream os) {
      throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
   }

   private void factoryReset(HttpExchange he, OutputStream os) {
      throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
   }

   private void reset(HttpExchange he, OutputStream os) {
      throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
   }

   private void pause(HttpExchange he, OutputStream os) {
      throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
   }

   private void resume(HttpExchange he, OutputStream os) {
      throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
   }

   private void information(HttpExchange he, OutputStream os) {
      throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
   }

   private void deviceConfig(HttpExchange he, OutputStream os) {
      throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
   }

   private void readBoolean(HttpExchange he, OutputStream os) {
      throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
   }

   private void listModel(HttpExchange he, OutputStream os) {
      throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
   }

}
