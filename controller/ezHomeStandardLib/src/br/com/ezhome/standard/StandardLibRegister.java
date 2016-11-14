package br.com.ezhome.standard;

import br.com.ezhome.standard.modules.DigitalOutputModule;
import br.com.ezhome.standard.modules.DigitalInputModule;
import br.com.ezhome.standard.modules.LightningModule;
import br.com.ezhome.standard.modules.AlarmCentralModule;
import br.com.ezhome.lib.LibRegister;
import br.com.ezhome.standard.models.DeviceModelArduinoMega;
import br.com.ezhome.standard.models.DeviceModelArduinoUno;

/**
 *
 * @author cristofer
 */
public class StandardLibRegister extends LibRegister {

   @Override
   public void install() throws InstantiationException, IllegalAccessException {
      registerModule(DigitalInputModule.class, "Digital Input");
      registerModule(DigitalOutputModule.class, "Digital Output");
      registerModule(LightningModule.class, "Switch Module");
      registerModule(AlarmCentralModule.class, "Alarm Central Module");
      registerDeviceModel(new DeviceModelArduinoMega(), "Arduino MEGA");
      registerDeviceModel(new DeviceModelArduinoUno(), "Arduino UNO");
   }

}
