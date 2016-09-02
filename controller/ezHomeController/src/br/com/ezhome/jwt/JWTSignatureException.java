package br.com.ezhome.jwt;

/**
 *
 * @author cristofer
 */
public class JWTSignatureException extends JWTException {
   
   public JWTSignatureException() {
      super(401, "Invalid signature for token");
   }
   
}
