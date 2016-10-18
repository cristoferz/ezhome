package br.com.ezhome.lib;

/**
 *
 * @author cristofer
 */
public abstract class LibRegister {
   public abstract void install();
   
   public void registerModule(Class module, String description) {
      LibManager.getInstance().registerModule(new ModuleClass(module, description));
   }
   
   
}
