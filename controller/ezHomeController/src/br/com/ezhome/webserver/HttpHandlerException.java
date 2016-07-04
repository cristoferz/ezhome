package br.com.ezhome.webserver;

import java.io.IOException;

/**
 *
 * @author cristofer
 */
public class HttpHandlerException extends IOException {
   private int statusCode;
   private String message;

   public HttpHandlerException(int statusCode, String message, Throwable cause) {
      super(message, cause);
      this.statusCode = statusCode;
      this.message = message;
   }
   
   public HttpHandlerException(int statusCode, String message) {
      super(message);
      this.statusCode = statusCode;
      this.message = message;
   }

   public int getStatusCode() {
      return statusCode;
   }

   public void setStatusCode(int statusCode) {
      this.statusCode = statusCode;
   }

   public String getMessage() {
      return message;
   }

   public void setMessage(String message) {
      this.message = message;
   }
   
}
