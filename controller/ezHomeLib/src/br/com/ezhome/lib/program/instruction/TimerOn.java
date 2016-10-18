package br.com.ezhome.lib.program.instruction;

import br.com.ezhome.lib.program.ProgramAddress;
import br.com.ezhome.lib.program.ProgramBuilder;
import br.com.ezhome.lib.program.ProgramSeriesBuilder;
import org.json.JSONObject;

/**
 *
 * @author cristofer
 */
public class TimerOn extends ProgramInstruction {
   
   private NumericValue setpointValue;
   private ProgramAddress doneAddress, elapsedAddress;

   public TimerOn(ProgramBuilder builder, NumericValue setpointValue, ProgramAddress doneAddress, ProgramAddress elapsedAddress) {
      super(builder);
      this.setpointValue = setpointValue;
      this.doneAddress = doneAddress;
      this.elapsedAddress = elapsedAddress;
   }

   @Override
   public byte getInstructionCode() {
      return 0x13;
   }

   @Override
   public int getInstructionSize() {
      return 5;
   }

   @Override
   public int getDataSize() {
      int size = getInstructionSize();
      size += setpointValue.getDataSize();
      size += getBuilder().getBitsPerBoolAddress();
      size += getBuilder().getBitsPerNumericAddress();
      return size;
   }

   @Override
   public void appendBytes(ProgramSeriesBuilder builder) {
      builder.getArrayBuilder().append(getInstructionCode(), getInstructionSize(), false, true);
      setpointValue.appendBytes(builder);
      builder.getArrayBuilder().append(doneAddress.getAddress(), getBuilder().getBitsPerBoolAddress(), false, true);
      builder.getArrayBuilder().append(elapsedAddress.getAddress(), getBuilder().getBitsPerNumericAddress(), false, true);
   }

   @Override
   public JSONObject toJSON() {
      JSONObject result = new JSONObject();
      JSONObject details = new JSONObject();
      result.put(getTagName(), details);
      details.put("setpointValue", setpointValue.toJSON());
      details.put("doneAddress", doneAddress.getAddress());
      details.put("elapsedAddress", elapsedAddress.getAddress());
      
      return result;
   }
   
   protected String getTagName() {
      return "TimerOn";
   }
}
