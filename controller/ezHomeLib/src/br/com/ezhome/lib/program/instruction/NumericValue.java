package br.com.ezhome.lib.program.instruction;

import br.com.ezhome.lib.program.ProgramAddress;
import br.com.ezhome.lib.program.ProgramBuilder;
import br.com.ezhome.lib.program.ProgramSeriesBuilder;
import org.json.JSONObject;

/**
 *
 * @author cristofer
 */
public class NumericValue {

   private ProgramBuilder builder;
   private ProgramAddress address;

   private boolean constant;
   private int value;

   public NumericValue(ProgramBuilder builder, int value) {
      this.builder = builder;
      this.value = value;
      this.constant = true;
   }

   public NumericValue(ProgramBuilder builder, ProgramAddress address) {
      this.builder = builder;
      this.address = address;
      this.constant = false;
   }

   public int getDataSize() {
      if (constant) {
         int numBytes = 4;
         if (value > (1 << (8 * 3) - 1)) {
            numBytes = 4;
         } else if (value > (1 << (8 * 2) - 1)) {
            numBytes = 3;
         } else if (value > (1 << (8 * 1) - 1)) {
            numBytes = 2;
         } else {
            numBytes = 1;
         }

         int bits = 2;
         if (numBytes < 4) {
            bits += 2;
         }
         bits += numBytes * 8;
         return bits;
      } else {
         int bits = 1;
         bits += builder.getBitsPerNumericAddress();
         return bits;
      }
   }

   public void appendBytes(ProgramSeriesBuilder builder) {
      builder.getArrayBuilder().appendBit(!constant);
      if (constant) {
         int numBytes = 4;
         if (value > (1 << (8 * 3) - 1)) {
            numBytes = 4;
         } else if (value > (1 << (8 * 2) - 1)) {
            numBytes = 3;
         } else if (value > (1 << (8 * 1) - 1)) {
            numBytes = 2;
         } else {
            numBytes = 1;
         }
         builder.getArrayBuilder().appendBit(numBytes >= 4);
         if (numBytes < 4) {
            builder.getArrayBuilder().append(numBytes-1, 2, false, true);
         }
         builder.getArrayBuilder().append(value, numBytes * 8, false, true);
      } else {
         builder.getArrayBuilder().append(address.getAddress(), getBuilder().getBitsPerNumericAddress(), false, true);
      }
   }

   public JSONObject toJSON() {
      JSONObject result = new JSONObject();
      if (constant) {
         result.put("value", value);
      } else {
         result.put("address", address.getAddress());
      }
      return result;
   }

   public static NumericValue fromJSON(ProgramBuilder builder, JSONObject json) {
      if (json.has("address")) {
         return new NumericValue(builder, builder.getAddress(json.getInt("address")));
      } else {
         return new NumericValue(builder, json.getInt("value"));
      }
   }

   public ProgramBuilder getBuilder() {
      return builder;
   }

   public void setBuilder(ProgramBuilder builder) {
      this.builder = builder;
   }

}
