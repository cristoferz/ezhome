package br.com.ezhome.security;

/**
 *
 * @author cristofer
 */
public class EzHomeSecurityManager {

   private static EzHomeSecurityManager instance;

   public EzHomeSecurityManager() {

   }

   public static EzHomeSecurityManager getInstance() {
      if (instance == null) {
         instance = new EzHomeSecurityManager();
      }
      return instance;
   }
   
   public byte[] getLocalSecret() {
      return "Teste".getBytes();
   }

}
