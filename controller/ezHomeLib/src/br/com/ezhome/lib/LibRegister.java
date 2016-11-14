package br.com.ezhome.lib;

import br.com.ezhome.lib.compiler.EzHomeCompilerDeviceModel;

/**
 *
 * @author cristofer
 */
public abstract class LibRegister {
   public abstract void install() throws InstantiationException, IllegalAccessException;
   
   public void registerModule(Class module, String description) throws InstantiationException, IllegalAccessException {
      LibManager.getInstance().registerModule(new ModulePrototype(module, description));
   }
   
   public void registerDeviceModel(EzHomeCompilerDeviceModel deviceModel, String description) {
      LibManager.getInstance().registerDeviceModel(deviceModel, description);
   }
   
   
   
}
