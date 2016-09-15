package br.com.ezhome.device.program.instruction;

import br.com.ezhome.device.program.ProgramAddress;
import br.com.ezhome.device.program.ProgramBuilder;
import br.com.ezhome.device.program.ProgramSeriesBuilder;
import org.json.JSONObject;

/**
 *
 * @author cristofer
 */
public class MathOperation extends ProgramInstruction {
   
   public static final String ADD = "+", SUBTRACT = "-", MULTIPLY = "*", DIVIDE = "/", CHOOSE = "#";
   
   private String type;
   private NumericValue operator1, operator2;
   private ProgramAddress resultAddress;

   public MathOperation(ProgramBuilder builder, JSONObject json) {
      super(builder);
      String comparator = json.getJSONObject("MathOperation").getString("type");
      switch (comparator) {
         case ADD:
         case SUBTRACT:
         case MULTIPLY:
         case DIVIDE:
         case CHOOSE:
            this.type = comparator;
            break;
         default:
            throw new IllegalArgumentException("Invalid instruction type: "+comparator);
      }
      this.operator1 = NumericValue.fromJSON(builder, json.getJSONObject("MathOperation").getJSONObject("operator1"));
      this.operator2 = NumericValue.fromJSON(builder, json.getJSONObject("MathOperation").getJSONObject("operator2"));
      this.resultAddress = getBuilder().getAddress(json.getJSONObject("MathOperation").getInt("resultAddress"));
   }

   @Override
   public byte getInstructionCode() {
      switch (type) {
         case ADD:
            return 0x78;
         case SUBTRACT:
            return 0x79;
         case MULTIPLY:
            return 0x7A;
         case DIVIDE:
            return 0x7B;
         case CHOOSE:
            return 0x7C;
         default:
            throw new IllegalArgumentException("Invalid instruction type: "+type);
      }
   }

   @Override
   public int getInstructionSize() {
      return 7;
   }

   @Override
   public int getDataSize() {
      int size = getInstructionSize();
      size += operator1.getDataSize();
      size += operator2.getDataSize();
      size += getBuilder().getBitsPerNumericAddress();
      return size;
   }

   @Override
   public void appendBytes(ProgramSeriesBuilder builder) {
      builder.getArrayBuilder().append(getInstructionCode(), getInstructionSize(), false, true);
      operator1.appendBytes(builder);
      operator2.appendBytes(builder);
      builder.getArrayBuilder().append(resultAddress.getAddress(), getBuilder().getBitsPerNumericAddress(), false, true);
   }

   @Override
   public JSONObject toJSON() {
      JSONObject result = new JSONObject();
      result.put("type", type);
      result.put("operator1", operator1.toJSON());
      result.put("operator2", operator2.toJSON());
      result.put("resultAddress", resultAddress.getAddress());
      return result;
   }
   
}
