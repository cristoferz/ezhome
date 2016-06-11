package br.com.ezhome.webserver;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 *
 * @author cristofer
 */
public abstract class HttpHandlerAbstract implements HttpHandler {

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

   public HashMap<String, String> getUrlParameters(HttpExchange exchange) {
      return queryToMap(exchange.getRequestURI().getQuery());
   }

   public HashMap<String, String> getPostParameters(HttpExchange exchange) throws IOException {
      StringBuilder sb = new StringBuilder();
      BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
      try {
         int len;
         char[] buf = new char[1024];
         while ((len = reader.read(buf)) != -1) {
            sb.append(buf);
         }
      } finally {
         reader.close();
      }
      return queryToMap(sb.toString());
   }
   
   public JSONObject getJSONRequest(HttpExchange exchange) {
      return new JSONObject(new JSONTokener(exchange.getRequestBody()));
   }
   
   
}
