package br.com.ezhome.webserver.context;

import br.com.ezhome.jwt.JWT;
import br.com.ezhome.lib.LibManager;
import br.com.ezhome.lib.ModulePrototype;
import br.com.ezhome.security.EzHomeSecurityManager;
import br.com.ezhome.webserver.HttpHandlerAbstract;
import br.com.ezhome.webserver.HttpHandlerException;
import com.sun.net.httpserver.HttpExchange;
import java.io.OutputStream;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author cristofer
 */
public class HttpHandlerModules extends HttpHandlerAbstract {

   @Override
   public void handleRequest(HttpExchange he) throws Exception {
      switch (he.getRequestURI().getPath()) {
         case "/modules":
            switch (he.getRequestMethod()) {
               case "GET":
                  JSONObject result = new JSONObject();
                  JSONArray modules = new JSONArray();
                  result.put("modules", modules);
                  
                  for (ModulePrototype module : LibManager.getInstance().getModules()) {
                     JSONObject jsonModule = new JSONObject();
                     jsonModule.put("class", module.getClasse().getName());
                     jsonModule.put("description", module.getDescription());
                     jsonModule.put("configs", module.getConfigs());
                     
                     modules.put(jsonModule);
                  }
                  
                  he.sendResponseHeaders(200, result.toString().getBytes().length);
                  try (OutputStream os = he.getResponseBody()) {
                     os.write(result.toString().getBytes());
                  }
                  
                  break;
               default:
                  throw new HttpHandlerException(400, "Invalid method \"" + he.getRequestMethod() + "\" for path \"" + he.getHttpContext().getPath() + "\"");
            }
            break;
         default:
            throw new HttpHandlerException(404, "Invalid path \"" + he.getHttpContext().getPath() + "\"");
      }
   }
   
}
