package br.com.ezhome.lib.program.instruction;

import br.com.ezhome.lib.program.ProgramAddress;
import br.com.ezhome.lib.program.ProgramBuilder;
import br.com.ezhome.lib.program.ProgramSeriesBuilder;
import org.json.JSONObject;

/**
 *
 * @author cristofer
 */
public class RisingEdge extends ProgramInstruction {

   private final ProgramAddress address;

   public RisingEdge(ProgramBuilder builder, ProgramAddress address) {
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
      builder.getArrayBuilder().append(address.getAddress(), getBuilder().getBitsPerBoolAddress(), false, true);
   }

   @Override
   public JSONObject toJSON() {
      JSONObject result = new JSONObject();
      result.put("RisingEdge", address.getAddress());
      return result;
   }

}
