package br.com.ezhome.device.program.instruction;

import br.com.ezhome.device.program.ByteArrayBuilder;
import br.com.ezhome.device.program.ProgramAddress;
import br.com.ezhome.device.program.ProgramBuilder;
import br.com.ezhome.device.program.ProgramSeriesBuilder;

/**
 *
 * @author cristofer
 */
public class NO extends ProgramInstruction {

   private ProgramAddress address;
   private boolean constant, value;

   public NO(ProgramBuilder builder, ProgramAddress address) {
      super(builder);
      this.constant = false;
      this.address = address;
   }

   public NO(ProgramBuilder builder, boolean value) {
      super(builder);
      this.constant = true;
      this.value = value;
   }

   public boolean isConstant() {
      return constant;
   }

   @Override
   public int getInstructionSize() {
      return 3;
   }

   @Override
   public byte getInstructionCode() {
      return 0x01;
   }

   @Override
   public int getDataSize() {
      if (isConstant()) {
         return getInstructionSize() + 2;
      } else {
         return getInstructionSize() + 1 + getBuilder().getBitsPerBoolAddress();
      }
   }

   @Override
   public void appendBytes(ProgramSeriesBuilder builder) {
      builder.getArrayBuilder().append(getInstructionCode(), getInstructionSize(), false, true);
      if (isConstant()) {
         builder.getArrayBuilder().appendBit(false);
         builder.getArrayBuilder().appendBit(value);
      } else {
         builder.getArrayBuilder().appendBit(true);
         builder.getArrayBuilder().append(address.getAddress(), getBuilder().getBitsPerBoolAddress(), false, true);
      }
   }

}
