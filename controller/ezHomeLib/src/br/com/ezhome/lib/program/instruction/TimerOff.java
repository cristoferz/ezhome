package br.com.ezhome.lib.program.instruction;

import br.com.ezhome.lib.program.ProgramAddress;
import br.com.ezhome.lib.program.ProgramBuilder;

/**
 *
 * @author cristofer
 */
public class TimerOff extends TimerOn {

   public TimerOff(ProgramBuilder builder, NumericValue setpointValue, ProgramAddress doneAddress, ProgramAddress elapsedAddress) {
      super(builder, setpointValue, doneAddress, elapsedAddress);
   }

   @Override
   public byte getInstructionCode() {
      return 0x14;
   }

   @Override
   protected String getTagName() {
      return "TimerOff";
   }
   
}
