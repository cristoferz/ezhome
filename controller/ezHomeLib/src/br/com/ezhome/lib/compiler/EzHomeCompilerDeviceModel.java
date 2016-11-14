package br.com.ezhome.lib.compiler;

import br.com.ezhome.lib.program.ProgramAddress;

/**
 *
 * @author cristofer
 */
public abstract class EzHomeCompilerDeviceModel {
   
   public ProgramAddress getAddressForDigital(int address) {
      if (address > getDigitalAddressEnd() || address < getDigitalAddressStart()) {
         throw new IllegalArgumentException("Invalid phisical address "+address+ " must be between "+getDigitalAddressStart()+ " and "+getDigitalAddressEnd());
      }
      return ProgramAddress.create(address+getDigitalAddressStart());
   }
   
   public ProgramAddress getAddressForAnalogic(int address) {
      if (address > getAnalogAddressEnd() || address < getAnalogAddressStart()) {
         throw new IllegalArgumentException("Invalid phisical address "+address+ " must be between "+getAnalogAddressStart()+ " and "+getAnalogAddressEnd());
      }
      return ProgramAddress.create(address+getAnalogAddressStart());
   }
   
   public abstract int getDigitalAddressStart();
   
   public abstract int getDigitalAddressEnd();
   
   public abstract int getAnalogAddressStart();
   
   public abstract int getAnalogAddressEnd();
   
   public abstract String getHexResourcePath();
   
}
