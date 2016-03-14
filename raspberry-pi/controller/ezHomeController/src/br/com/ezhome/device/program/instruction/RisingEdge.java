package br.com.ezhome.device.program.instruction;

import br.com.ezhome.device.program.ProgramBuilder;
import br.com.ezhome.device.program.ProgramSeriesBuilder;

/**
 *
 * @author cristofer
 */
public class RisingEdge extends ProgramInstruction {

   private final int address;

   public RisingEdge(ProgramBuilder builder, int address) {
      super(builder);
      this.address = address;
   }

   @Override
   public byte getInstructionCode() {
      return 0x10;
   }

   @Override
   public int getInstructionSize() {
      return 5;
   }

   @Override
   public int getDataSize() {
      return getInstructionSize() + getBuilder().getBitsPerBoolAddress();
   }

   @Override
   public void appendBytes(ProgramSeriesBuilder builder) {
      builder.getArrayBuilder().append(getInstructionCode(), getInstructionSize(), false, true);
      builder.getArrayBuilder().append(address, getBuilder().getBitsPerBoolAddress(), false, true);
   }

}
