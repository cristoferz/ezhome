package br.com.ezhome.webserver;

import br.com.ezhome.device.PortConnector;
import br.com.ezhome.device.PortManager;
import br.com.ezhome.device.PortReaderAdapter;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import gnu.io.CommPortIdentifier;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author cristofer
 */
public class HttpHandlerDevice implements HttpHandler {

   /**
    * Converte a query string em um Map de parametros
    *
    * @param query
    * @return
    */
   public HashMap<String, String> queryToMap(String query) {
      HashMap<String, String> result = new HashMap<>();
      if (query != null) {
         String[] parameters = query.split("&");
         if (parameters != null) {
            for (String parameter : query.split("&")) {
               String pair[] = parameter.split("=");
               if (pair.length == 1) {
                  result.put(pair[0], "");
               } else {
                  result.put(pair[0], pair[1]);
               }
            }
         }
      }
      return result;
   }

   @Override
   public void handle(HttpExchange exchange) throws IOException {
      OutputStream os = exchange.getResponseBody();
      try {
         String path = exchange.getRequestURI().getPath();
         HashMap<String, String> urlParameters = queryToMap(exchange.getRequestURI().getQuery());
         if (path.equals("/device/list")) {
            JSONObject json = new JSONObject();
            JSONArray devices = new JSONArray();
            ArrayList<PortConnector> connectors = PortManager.getInstance().listConnectedPorts();
            for (PortConnector connector : connectors) {
               JSONObject device = new JSONObject();
               device.put("name", connector.getName());
               device.put("currentOwner", "self");
               device.put("connected", true);
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
               PortManager.getInstance().connect(urlParameters.get("device"));
               json.put("success", true);
               exchange.sendResponseHeaders(200, json.toString().getBytes().length);
            } catch (Exception ex) {
               json.put("success", false);
               json.put("message", ex.getMessage());
               exchange.sendResponseHeaders(500, json.toString().getBytes().length);
            }
            os.write(json.toString().getBytes());
//         } else if (path.equals("/device/disconnect")) {
//            // Desconecta do arduino
//         } else if (path.equals("/device/firmwareWrite")) {
//            // Grava o firmware no arduino
//         } else if (path.equals("/device/upload")) {
//            // Faz o carregamento do programa para o arduino
//         } else if (path.equals("/device/scan")) {
//            // Faz um scan de portas do arduino
//         } else if (path.equals("/device/upload")) {
//            
         } else if (path.equals("/device/send")) {
            PortConnector connector = PortManager.getInstance().connect(urlParameters.get("device"));
            String result = connector.sendCommand(urlParameters.get("command"));
            JSONObject json = new JSONObject();
            json.put("success", true);
            json.put("result", result);
            exchange.sendResponseHeaders(200, json.toString().getBytes().length);
            os.write(json.toString().getBytes());
            
         } else {
            String response = "Invalid URL: " + path;
            exchange.sendResponseHeaders(404, response.getBytes().length);
            os.write(response.getBytes());
         }
      } catch (Throwable ex) {
         JSONObject json = new JSONObject();
         json.put("success", false);
         json.put("message", ex.getMessage());
         json.put("exception", ex);
         exchange.sendResponseHeaders(500, json.toString().getBytes().length);
         os.write(json.toString().getBytes());
         ex.printStackTrace();
      } finally {
         os.close();
      }
   }

}
