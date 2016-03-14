package br.com.ezhome.device.program.instruction;

import br.com.ezhome.device.program.ByteArrayBuilder;
import br.com.ezhome.device.program.ProgramBuilder;
import br.com.ezhome.device.program.ProgramSeriesBuilder;

/**
 *
 * @author cristofer
 */
public class Coil extends ProgramInstruction {

   private int address;

   public Coil(ProgramBuilder builder, int address) {
      super(builder);
      this.address = address;
   }
   
   @Override
   public int getInstructionSize() {
      return 3;
   }

   @Override
   public byte getInstructionCode() {
      return 0x00;
   }

   @Override
   public int getDataSize() {
      return getBuilder().getBitsPerBoolAddress() + getInstructionSize();
   }

   @Override
   public void appendBytes(ProgramSeriesBuilder builder) {
      builder.getArrayBuilder().append(getInstructionCode(), getInstructionSize(), false, true);
      builder.getArrayBuilder().append(address, getBuilder().getBitsPerBoolAddress(), false, true);
   }
}
