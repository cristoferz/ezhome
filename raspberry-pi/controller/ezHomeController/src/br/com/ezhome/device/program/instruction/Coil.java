package br.com.ezhome.device.program.instruction;

import br.com.ezhome.device.program.ProgramAddress;
import br.com.ezhome.device.program.ProgramBuilder;
import br.com.ezhome.device.program.ProgramSeriesBuilder;
import org.json.JSONObject;

/**
 *
 * @author cristofer
 */
public class Coil extends ProgramInstruction {

   private ProgramAddress address;
   private boolean monitor;
   private boolean writeEEPROM;
   private int eEPROMAddress;

   public Coil(ProgramBuilder builder, ProgramAddress address, boolean monitor, boolean writeEeprom, int eepromAddress) {
      super(builder);
      this.address = address;
      this.monitor = monitor;
      this.writeEEPROM = writeEeprom;
      this.eEPROMAddress = eepromAddress;
   }

   public Coil(ProgramBuilder builder, ProgramAddress address, boolean monitor) {
      this(builder, address, monitor, false, -1);
   }

   public Coil(ProgramBuilder builder, ProgramAddress address) {
      this(builder, address, false, false, -1);
   }

   @Override
   public int getInstructionSize() {
      return 3;
   }

   @Override
   public byte getInstructionCode() {
      return 0x00;
   }

   @Override
   public int getDataSize() {
      return getBuilder().getBitsPerBoolAddress() + getInstructionSize();
   }

   @Override
   public void appendBytes(ProgramSeriesBuilder builder) {
      builder.getArrayBuilder().append(getInstructionCode(), getInstructionSize(), false, true);
      builder.getArrayBuilder().append(address.getAddress(), getBuilder().getBitsPerBoolAddress(), false, true);
      builder.getArrayBuilder().appendBit(monitor);
      builder.getArrayBuilder().appendBit(writeEEPROM);
      if (writeEEPROM) {
         builder.getArrayBuilder().append(eEPROMAddress, 8, true, true);
      }
   }

   @Override
   public JSONObject toJSON() {
      JSONObject result = new JSONObject();
      result.put("Coil", address.getAddress());
      return result;
   }
}
