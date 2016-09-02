package br.com.ezhome.jwt;

import br.com.ezhome.webserver.HttpHandlerException;

/**
 *
 * @author cristofer
 */
public class JWTException extends HttpHandlerException {
   
   public JWTException(int statusCode, String message) {
      super(statusCode, message);
   }
   
   public JWTException(int statusCode, String message, Throwable cause) {
      super(statusCode, message, cause);
   }
   
}
