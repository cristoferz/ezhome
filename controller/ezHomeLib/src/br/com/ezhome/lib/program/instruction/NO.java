package br.com.ezhome.lib.program.instruction;

import br.com.ezhome.lib.program.ProgramAddress;
import br.com.ezhome.lib.program.ProgramBuilder;
import br.com.ezhome.lib.program.ProgramSeriesBuilder;
import org.json.JSONObject;

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

   public NO(ProgramBuilder builder, JSONObject json) {
      super(builder);
      if (json.get("NO") instanceof Boolean) {
         this.constant = true;
         this.value = json.getBoolean("NO");
      } else {
         this.constant = false;
         this.address = builder.getAddress(json.getInt("NO"));
      }
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

   @Override
   public JSONObject toJSON() {
      JSONObject result = new JSONObject();
      result.put("NO", address.getAddress());
      return result;
   }

}
