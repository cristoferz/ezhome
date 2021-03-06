package br.com.ezhome.lib.program.instruction;

import br.com.ezhome.lib.program.ProgramAddress;
import br.com.ezhome.lib.program.ProgramBuilder;
import br.com.ezhome.lib.program.ProgramSeriesBuilder;
import org.json.JSONObject;

/**
 *
 * @author cristofer
 */
public class NC extends ProgramInstruction {

   private ProgramAddress address;
   private boolean constant, value;

   public NC(ProgramBuilder builder, ProgramAddress address) {
      super(builder);
      this.address = address;
      this.constant = false;
   }

   public NC(ProgramBuilder builder, boolean value) {
      super(builder);
      this.constant = true;
      this.value = value;
   }
   
   public NC(ProgramBuilder builder, JSONObject json) {
      super(builder);
      if (json.get("NC") instanceof Boolean) {
         this.constant = true;
         this.value = json.getBoolean("NC");
      } else {
         this.constant = false;
         this.address = builder.getAddress(json.getInt("NC"));
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
      return 0x02;
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
      result.put("NC", address.getAddress());
      return result;
   }
   
   public static ProgramInstruction fromJSON(ProgramBuilder builder, JSONObject json) {
      if (json.has("NO")) {
         if (json.get("NO") instanceof Boolean) {
            return new NO(builder, json.getBoolean("NO"));
         } else {
            return new NO(builder, builder.getAddress(json.getInt("NO")));
         }
      } else {
         return null;
      }
   }

}
