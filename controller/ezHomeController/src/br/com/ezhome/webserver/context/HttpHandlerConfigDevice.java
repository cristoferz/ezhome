package br.com.ezhome.webserver.context;

import br.com.ezhome.database.DatabaseConnector;
import br.com.ezhome.device.Device;
import br.com.ezhome.device.DeviceManager;
import br.com.ezhome.device.FirmwareUploader;
import br.com.ezhome.webserver.HttpHandlerAbstract;
import br.com.ezhome.webserver.HttpHandlerJWTAbstract;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author cristofer
 */
public class HttpHandlerConfigDevice extends HttpHandlerJWTAbstract {

   @Override
   public void handleRequest(HttpExchange he) throws Exception {
      try (OutputStream os = he.getResponseBody()) {
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
            case "/config/device/model":
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
      }
   }

   private void list(HttpExchange he, OutputStream os) throws IOException, SQLException {
      JSONObject result = new JSONObject();
      JSONArray devices = new JSONArray();
      result.put("devices", devices);
      try (Connection con = DatabaseConnector.getInstance().connect()) {
         try (PreparedStatement stmt = con.prepareStatement("select device_id\n"
                 + "     , name\n"
                 + "     , model_id\n"
                 + "     , runtime_id\n"
                 + "     , version_id\n"
                 + "     , status_id"
                 + "  from config.device\n"
                 + " where 1=1")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
               JSONObject device = new JSONObject();
               device.put("device_id", rs.getInt("device_id"));
               device.put("name", rs.getString("name"));
               device.put("model_id", rs.getInt("model_id"));
               device.put("runtime_id", rs.getString("runtime_id"));
               device.put("version_id", rs.getString("version_id"));
               device.put("status_id", rs.getInt("status_id"));
               devices.put(device);
            }
            he.getResponseHeaders().add("Content-type", "application/json");
            he.sendResponseHeaders(200, result.toString().getBytes().length);
            os.write(result.toString().getBytes());
         }
      }
   }

   private void insert(HttpExchange he, OutputStream os) throws SQLException, IOException {
      JSONObject parameters = getJSONRequest(he);
      JSONObject result = new JSONObject();
      try (Connection con = DatabaseConnector.getInstance().connect()) {
         String newGUID = guid2String(generateGUID());
         try (PreparedStatement stmt = con.prepareStatement("insert into config.device\n"
                 + "   (name, model_id, runtime_id)\n"
                 + "values\n"
                 + "   (?, ?, ?)\n"
                 + "returning device_id")) {
            stmt.setString(1, parameters.getString("name"));
            stmt.setInt(2, parameters.getInt("model_id"));
            stmt.setString(3, newGUID);
            result.put("success", true);
            result.put("runtime_id", newGUID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
               result.put("device_id", rs.getInt(1));
            }
            he.getResponseHeaders().add("Content-type", "application/json");
            he.sendResponseHeaders(200, result.toString().getBytes().length);
            os.write(result.toString().getBytes());
         }
      }
   }

   private void update(HttpExchange he, OutputStream os) throws SQLException, IOException {
      JSONObject parameters = getJSONRequest(he);
      JSONObject result = new JSONObject();
      try (Connection con = DatabaseConnector.getInstance().connect()) {
         try (PreparedStatement stmt = con.prepareStatement("update config.device\n"
                 + "   set name = ?"
                 + " where device_id = ?")) {
            stmt.setString(1, parameters.getString("name"));
            stmt.setInt(2, parameters.getInt("device_id"));
            result.put("success", true);
            stmt.execute();
            he.getResponseHeaders().add("Content-type", "application/json");
            he.sendResponseHeaders(200, result.toString().getBytes().length);
            os.write(result.toString().getBytes());
         }
      }
   }

   private void delete(HttpExchange he, OutputStream os) throws SQLException, IOException {
      JSONObject parameters = getJSONRequest(he);
      JSONObject result = new JSONObject();
      try (Connection con = DatabaseConnector.getInstance().connect()) {
         try (PreparedStatement stmt = con.prepareStatement("delete from config.device\n"
                 + " where device_id = ?")) {
            stmt.setInt(1, parameters.getInt("device_id"));
            result.put("success", true);
            stmt.execute();
            he.getResponseHeaders().add("Content-type", "application/json");
            he.sendResponseHeaders(200, result.toString().getBytes().length);
            os.write(result.toString().getBytes());
         }
      }
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

   private void listModel(HttpExchange he, OutputStream os) throws Exception {
      he.getResponseHeaders().add("Content-type", "application/json");
      JSONObject result = new JSONObject();
      JSONArray models = new JSONArray();
      result.put("models", models);
      try (Connection con = DatabaseConnector.getInstance().connect()) {
         try (PreparedStatement stmt = con.prepareStatement(
                 "select model_id\n"
                 + "     , model_cod\n"
                 + "     , name\n"
                 + "     , thumbnail\n"
                 + "  from config.device_model m\n"
                 + " where 1=1")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
               JSONObject model = new JSONObject();
               model.put("model_id", rs.getInt("model_id"));
               model.put("model_cod", rs.getString("model_cod"));
               model.put("name", rs.getString("name"));
               model.put("thumbnail", rs.getString("thumbnail"));
               models.put(model);
            }
         }

      }
      he.sendResponseHeaders(200, result.toString().getBytes().length);
      os.write(result.toString().getBytes());
   }

   private byte[] generateGUID() {
      byte[] bytes = new byte[16];
      new Random().nextBytes(bytes);
      //SecureRandom.getInstanceStrong().nextBytes(bytes);
      return bytes;
   }

   private String guid2String(byte[] guid) {
      StringBuilder sb = new StringBuilder();
      for (byte b : guid) {
         sb.append(String.format("%02X", b));
      }

      return sb.toString();
   }

}
