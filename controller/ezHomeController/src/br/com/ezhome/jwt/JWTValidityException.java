package br.com.ezhome.jwt;

/**
 *
 * @author cristofer
 */
public class JWTValidityException extends JWTException {
   
   public JWTValidityException(String message) {
      super(401, message);
   }
   
}
