package br.com.ezhome.webserver.context;

import br.com.ezhome.jwt.JWT;
import br.com.ezhome.security.EzHomeSecurityManager;
import br.com.ezhome.webserver.HttpHandlerAbstract;
import br.com.ezhome.webserver.HttpHandlerException;
import com.sun.net.httpserver.HttpExchange;
import java.io.OutputStream;
import org.json.JSONObject;

/**
 *
 * @author cristofer
 */
public class HttpHandlerLogin extends HttpHandlerAbstract {

   @Override
   public void handleRequest(HttpExchange he) throws Exception {
      switch (he.getRequestURI().getPath()) {
         case "/login/token":
            switch (he.getRequestMethod()) {
               case "POST":
                  JSONObject data = new JSONObject();
                  
                  JSONObject result = new JSONObject();
                  
                  JWT jwt = new JWT(EzHomeSecurityManager.getInstance().getLocalSecret());
                  result.put("token", jwt.sign(data));
                  
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
            throw new HttpHandlerException(404, "Invalid path d\"" + he.getHttpContext().getPath() + "\"");
      }
   }

}
