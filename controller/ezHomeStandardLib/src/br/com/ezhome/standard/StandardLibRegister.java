package br.com.ezhome.standard;

import br.com.ezhome.lib.LibRegister;

/**
 *
 * @author cristofer
 */
public class StandardLibRegister extends LibRegister {

   @Override
   public void install() {
      registerModule(SwitchModule.class, "Switch Module");
      registerModule(AlarmCentralModule.class, "Alarm Central Module");
   }
   
}
