package br.com.ezhome.device.program.instruction;

import br.com.ezhome.device.program.ByteArrayBuilder;
import br.com.ezhome.device.program.ProgramBuilder;
import br.com.ezhome.device.program.ProgramSeriesBuilder;

/**
 * Operação de Set/Reset para um endereço
 *
 * @author cristofer
 */
public class SetReset extends ProgramInstruction {

   private int outputAddress, valueAddress;
   private boolean constant, constantValue;

   public SetReset(ProgramBuilder builder, int outputAddress, boolean constantValue) {
      super(builder);
      this.outputAddress = outputAddress;
      this.constant = true;
      this.constantValue = constantValue;
   }

   public SetReset(ProgramBuilder builder, int outputAddress, int valueAddress) {
      super(builder);
      this.outputAddress = outputAddress;
      this.constant = false;
      this.valueAddress = valueAddress;
   }

   public boolean isConstant() {
      return constant;
   }

   @Override
   public byte getInstructionCode() {
      return 0x12;
   }

   @Override
   public int getInstructionSize() {
      return 5;
   }

   @Override
   public int getDataSize() {
      return getBuilder().getBitsPerBoolAddress() + 1 + getInstructionSize();
   }

   @Override
   public void appendBytes(ProgramSeriesBuilder builder) {
      builder.getArrayBuilder().append(getInstructionCode(), getInstructionSize(), false, true);
      if (isConstant()) {
         builder.getArrayBuilder().appendBit(false);
         builder.getArrayBuilder().appendBit(constantValue);
      } else {
         builder.getArrayBuilder().appendBit(true);
         builder.getArrayBuilder().append(valueAddress, getBuilder().getBitsPerBoolAddress(), false, true);
      }
      builder.getArrayBuilder().append(outputAddress, getBuilder().getBitsPerBoolAddress(), false, true);
   }

}
