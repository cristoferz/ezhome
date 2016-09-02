package br.com.ezhome.device.program.instruction;

import br.com.ezhome.device.program.ProgramAddress;
import br.com.ezhome.device.program.ProgramBuilder;
import br.com.ezhome.device.program.ProgramSeriesBuilder;
import org.json.JSONObject;

/**
 *
 * @author cristofer
 */
public class Counter extends ProgramInstruction {
   
   private boolean countDown;
   
   private NumericValue setpointValue;
   private BooleanValue resetValue;
   private ProgramAddress doneAddress, countAddress, oneshotStateAddress;

   public Counter(boolean countDown, NumericValue setpointValue, BooleanValue resetValue, ProgramAddress doneAddress, ProgramAddress countAddress, ProgramAddress oneshotStateAddress, ProgramBuilder builder) {
      super(builder);
      this.countDown = countDown;
      this.setpointValue = setpointValue;
      this.resetValue = resetValue;
      this.doneAddress = doneAddress;
      this.countAddress = countAddress;
      this.oneshotStateAddress = oneshotStateAddress;
   }
   
   @Override
   public byte getInstructionCode() {
      return 0x17;
   }

   @Override
   public int getInstructionSize() {
      return 5;
   }

   @Override
   public int getDataSize() {
      int size = 1;
      size += setpointValue.getDataSize();
      size += resetValue.getDataSize();
      size += getBuilder().getBitsPerBoolAddress(); // Done
      size += getBuilder().getBitsPerBoolAddress(); // Count
      size += getBuilder().getBitsPerBoolAddress(); // OneshotState
      return size;
   }

   @Override
   public void appendBytes(ProgramSeriesBuilder builder) {
      builder.getArrayBuilder().appendBit(countDown);
      setpointValue.appendBytes(builder);
      resetValue.appendBytes(builder);
      builder.getArrayBuilder().append(doneAddress.getAddress(), getBuilder().getBitsPerBoolAddress(), false, true);
      builder.getArrayBuilder().append(countAddress.getAddress(), getBuilder().getBitsPerBoolAddress(), false, true);
      builder.getArrayBuilder().append(oneshotStateAddress.getAddress(), getBuilder().getBitsPerBoolAddress(), false, true);
   }

   @Override
   public JSONObject toJSON() {
      throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
   }
   
}
