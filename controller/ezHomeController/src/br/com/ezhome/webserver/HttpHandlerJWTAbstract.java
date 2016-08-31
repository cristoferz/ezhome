package br.com.ezhome.webserver;

import br.com.ezhome.jwt.JWT;
import br.com.ezhome.security.EzHomeSecurityManager;
import com.sun.net.httpserver.HttpExchange;
import org.json.JSONObject;

/**
 *
 * @author cristofer
 */
public abstract class HttpHandlerJWTAbstract extends HttpHandlerAbstract {
   
   private JSONObject tokenData;
   
   private JWT jwt;

   public HttpHandlerJWTAbstract() {
      this.jwt = new JWT(EzHomeSecurityManager.getInstance().getLocalSecret());
   }

   public JSONObject getTokenData() {
      return tokenData;
   }

   @Override
   public void beforeHandle(HttpExchange he) throws HttpHandlerException {
      String authorization = he.getRequestHeaders().getFirst("Authorization");
      if (authorization == null)  {
         throw new HttpHandlerException(401, "Null token");
      }
      // splits to remove the level
      String[] tokenParts = authorization.split(" ", 2);
      String token = tokenParts[(tokenParts.length > 1)?1:0];
      this.tokenData = jwt.validate(token);
   }

}
