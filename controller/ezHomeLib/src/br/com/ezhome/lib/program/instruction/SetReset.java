package br.com.ezhome.lib.program.instruction;

import br.com.ezhome.lib.program.ByteArrayBuilder;
import br.com.ezhome.lib.program.ProgramAddress;
import br.com.ezhome.lib.program.ProgramBuilder;
import br.com.ezhome.lib.program.ProgramSeriesBuilder;
import org.json.JSONObject;

/**
 * Operação de Set/Reset para um endereço
 *
 * @author cristofer
 */
public class SetReset extends ProgramInstruction {

   private ProgramAddress outputAddress, valueAddress;
   private boolean constant, constantValue;

   public SetReset(ProgramBuilder builder, ProgramAddress outputAddress, boolean constantValue) {
      super(builder);
      this.outputAddress = outputAddress;
      this.constant = true;
      this.constantValue = constantValue;
   }

   public SetReset(ProgramBuilder builder, ProgramAddress outputAddress, ProgramAddress valueAddress) {
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
         builder.getArrayBuilder().append(valueAddress.getAddress(), getBuilder().getBitsPerBoolAddress(), false, true);
      }
      builder.getArrayBuilder().append(outputAddress.getAddress(), getBuilder().getBitsPerBoolAddress(), false, true);
   }

   @Override
   public JSONObject toJSON() {
      JSONObject result = new JSONObject();
      JSONObject data = new JSONObject();
      result.put("SetReset", data);
      data.put("address", outputAddress.getAddress());
      if(isConstant()) {
         data.put("reset", constantValue);
      } else {
         data.put("reset", valueAddress.getAddress());
      }
      return result;
   }

}
