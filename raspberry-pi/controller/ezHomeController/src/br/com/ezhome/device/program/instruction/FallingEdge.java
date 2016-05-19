package br.com.ezhome.device.program.instruction;

import br.com.ezhome.device.program.ProgramAddress;
import br.com.ezhome.device.program.ProgramBuilder;
import br.com.ezhome.device.program.ProgramSeriesBuilder;

/**
 *
 * @author cristofer
 */
public class FallingEdge extends ProgramInstruction {
   
   private ProgramAddress address;

   public FallingEdge(ProgramBuilder builder, ProgramAddress address) {
      super(builder);
      this.address = address;
   }

   @Override
   public byte getInstructionCode() {
      return 0x11;
   }

   @Override
   public int getInstructionSize() {
      return 5;
   }

   @Override
   public int getDataSize() {
      return getBuilder().getBitsPerBoolAddress()+getInstructionSize();
   }
   
   @Override
   public void appendBytes(ProgramSeriesBuilder builder) {
      builder.getArrayBuilder().append(getInstructionCode(), getInstructionSize(), false, true);
      builder.getArrayBuilder().append(address.getAddress(), getBuilder().getBitsPerBoolAddress(), false, true);
   }
   
}
