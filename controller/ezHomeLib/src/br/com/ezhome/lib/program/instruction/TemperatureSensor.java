package br.com.ezhome.lib.program.instruction;

import br.com.ezhome.lib.program.ProgramAddress;
import br.com.ezhome.lib.program.ProgramBuilder;
import br.com.ezhome.lib.program.ProgramSeriesBuilder;
import org.json.JSONObject;

/**
 *
 * @author cristofer
 */
public class TemperatureSensor extends ProgramInstruction {
   
   private NumericValue readDelay;
   private ProgramAddress inputAddress, elapsedAddress, outputAddress, failAddress;
   private String sensorId;

   public TemperatureSensor(ProgramBuilder builder, String sensorId, NumericValue readDelay, 
           ProgramAddress inputAddress, ProgramAddress elapsedAddress, 
           ProgramAddress outputAddress, ProgramAddress failAddress) {
      super(builder);
      this.sensorId = sensorId;
      this.readDelay = readDelay;
      this.inputAddress = inputAddress;
      this.elapsedAddress = elapsedAddress;
      this.outputAddress = outputAddress;
      this.failAddress = failAddress;
   }

   @Override
   public byte getInstructionCode() {
      return 0x7e;
   }

   @Override
   public int getInstructionSize() {
      return 7;
   }

   @Override
   public int getDataSize() {
      int size = getInstructionSize();
      size += 3; // Switch for external devices
      size += 8 * 8; // 8 bytes for device address
      size += readDelay.getDataSize();
      size += getBuilder().getBitsPerNumericAddress(); //ElapsedAddress
      size += getBuilder().getBitsPerNumericAddress(); //OutputAddress
      size += getBuilder().getBitsPerBoolAddress();    //FaultAddress
      return size;
   }

   @Override
   public void appendBytes(ProgramSeriesBuilder builder) {
      builder.getArrayBuilder().append(getInstructionCode(), getInstructionSize(), false, true);
      builder.getArrayBuilder().append(0x0, 3, false, true); // External device switch
      builder.getArrayBuilder().append(inputAddress.getAddress(), getBuilder().getBitsPerBoolAddress(), false, true);
      for (int i = 0; i < 16; i+=2) {
         int value = Integer.parseInt(sensorId.substring(i, i+2), 16);
         builder.getArrayBuilder().append(value, 8, false, true);
      }
      readDelay.appendBytes(builder);
      builder.getArrayBuilder().append(elapsedAddress.getAddress(), getBuilder().getBitsPerNumericAddress(), false, true);
      builder.getArrayBuilder().append(outputAddress.getAddress(), getBuilder().getBitsPerNumericAddress(), false, true);
      builder.getArrayBuilder().append(failAddress.getAddress(), getBuilder().getBitsPerBoolAddress(), false, true);
      
   }

   @Override
   public JSONObject toJSON() {
      JSONObject result = new JSONObject();
      JSONObject details = new JSONObject();
      result.put(getTagName(), details);
      details.put("address", inputAddress.getAddress());
      details.put("idValue", sensorId);
      details.put("readDelay", readDelay.toJSON());
      details.put("elapsedAddress", elapsedAddress.getAddress());
      details.put("outputAddress", outputAddress.getAddress());
      details.put("failAddress", failAddress.getAddress());
      
      return result;
   }
   
   protected String getTagName() {
      return "TemperatureSensor";
   }
}
