/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.ezhome.webserver.context;

import br.com.ezhome.webserver.HttpHandlerAbstract;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import org.json.JSONObject;

/**
 *
 * @author cristofer
 */
public class HttpHandlerConfigDeviceProgram extends HttpHandlerAbstract{

   @Override
   public void handleRequest(HttpExchange he) throws Exception {
      OutputStream os = he.getResponseBody();
      try {
         String path = he.getRequestURI().getPath();
         switch (path) {
            case "/device/program":
               switch (he.getRequestMethod()) {
                  case "GET":
                     listProgram(he, os);
                     break;
                  case "POST":
                     insertProgram(he, os);
                     break;
                  case "DELETE":
                     deleteProgram(he, os);
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

   private void listProgram(HttpExchange he, OutputStream os) {
      throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
   }

   private void insertProgram(HttpExchange he, OutputStream os) {
      throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
   }

   private void deleteProgram(HttpExchange he, OutputStream os) {
      throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
   }
   
   
   
}
