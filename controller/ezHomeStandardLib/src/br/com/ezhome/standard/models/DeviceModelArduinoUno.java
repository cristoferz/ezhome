package br.com.ezhome.standard.models;

import br.com.ezhome.lib.compiler.EzHomeCompilerDeviceModel;

/**
 *
 * @author cristofer
 */
public class DeviceModelArduinoUno extends EzHomeCompilerDeviceModel {

   @Override
   public int getDigitalAddressStart() {
      return 0;
   }

   @Override
   public int getDigitalAddressEnd() {
      return 15;
   }

   @Override
   public int getAnalogAddressStart() {
      return 0;
   }

   @Override
   public int getAnalogAddressEnd() {
      return 5;
   }

   @Override
   public String getHexResourcePath() {
      return "/br/com/ezhome/standard/models/hex/Master.ino.standard.hex";
   }
   
}
