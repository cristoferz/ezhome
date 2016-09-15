package br.com.ezhome.device.program.instruction;

import br.com.ezhome.device.program.ProgramBuilder;
import br.com.ezhome.device.program.ProgramSeriesBuilder;
import org.json.JSONObject;

/**
 *
 * @author cristofer
 */
public class Comparator extends ProgramInstruction {
   
   public static final String EQUAL = "==", GREATERTHAN = ">", GREATERTHANOREQUAL = ">=", LESSTHAN = "<", LESSTHANOREQUAL = "<=", NOTEQUAL = "!=";
   
   private String type;
   
   private NumericValue value1, value2;

   public Comparator(ProgramBuilder builder, JSONObject json) {
      super(builder);
      String comparator = json.getJSONObject("Comparator").getString("type");
      switch (comparator) {
         case EQUAL:
         case GREATERTHAN:
         case GREATERTHANOREQUAL:
         case LESSTHAN:
         case LESSTHANOREQUAL:
         case NOTEQUAL:
            this.type = comparator;
            break;
         default:
            throw new IllegalArgumentException("Invalid instruction type: "+comparator);
      }
      this.value1 = NumericValue.fromJSON(builder, json.getJSONObject("Comparator").getJSONObject("value1"));
      this.value2 = NumericValue.fromJSON(builder, json.getJSONObject("Comparator").getJSONObject("value2"));
   }

   @Override
   public byte getInstructionCode() {
      switch (type) {
         case EQUAL:
            return 0x18;
         case GREATERTHAN:
            return 0x19;
         case GREATERTHANOREQUAL:
            return 0x1A;
         case LESSTHAN:
            return 0x1B;
         case LESSTHANOREQUAL:
            return 0x1C;
         case NOTEQUAL:
            return 0x1D;
         default:
            throw new IllegalArgumentException("Invalid instruction type: "+type);
      }
   }

   @Override
   public int getInstructionSize() {
      return 5;
   }

   @Override
   public int getDataSize() {
      int size = getInstructionSize();
      size += value1.getDataSize();
      size += value2.getDataSize();
      return size;
   }

   @Override
   public void appendBytes(ProgramSeriesBuilder builder) {
      builder.getArrayBuilder().append(getInstructionCode(), getInstructionSize(), false, true);
      value1.appendBytes(builder);
      value2.appendBytes(builder);
   }

   @Override
   public JSONObject toJSON() {
      JSONObject result = new JSONObject();
      result.put("type", type);
      result.put("value1", value1.toJSON());
      result.put("value2", value2.toJSON());
      return result;
   }
   
}
