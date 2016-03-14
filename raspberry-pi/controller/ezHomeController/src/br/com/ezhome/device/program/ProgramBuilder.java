package br.com.ezhome.device.program;

import br.com.ezhome.device.PortConnector;
import br.com.ezhome.device.PortManager;
import br.com.ezhome.device.PortReaderAdapter;
import br.com.ezhome.device.program.instruction.Coil;
import br.com.ezhome.device.program.instruction.NC;
import br.com.ezhome.device.program.instruction.NO;
import br.com.ezhome.device.program.instruction.RisingEdge;
import br.com.ezhome.device.program.instruction.SetReset;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author cristofer
 */
public class ProgramBuilder implements ProgramSeriesBuilder {

   public static final int GUID_LENGTH = 16;
   /**
    * ID do aparelho
    */
   public static final int RUNTIME_ID_INDEX = 0;
   /**
    * Versão do programa
    */
   public static final int VERSION_ID_INDEX = RUNTIME_ID_INDEX + GUID_LENGTH;
   /**
    * Indica o numero de bits por endereço booleano
    */
   public static final int BITS_PER_BOOL_ADDRESS_INDEX = VERSION_ID_INDEX + GUID_LENGTH;
   /**
    * Indica o numero de bits por endereço numerico
    */
   public static final int BITS_PER_NUMERIC_ADDRESS_INDEX = BITS_PER_BOOL_ADDRESS_INDEX + 1;
   /**
    * Onde começa o código do programa
    */
   public static final int OPCODES_INDEX = BITS_PER_NUMERIC_ADDRESS_INDEX + 1;
   /**
    * Instruction of program finalization
    */
   public static final byte INSTRUCTION_PROGRAM_END = (byte)0xFF;
   /**
    * Instruction of Series End
    */
   public static final byte INSTRUCTION_SERIES_END = (byte)0x03;
   /**
    * Size of instruction of Series End
    */
   public static final int INSTRUCTION_SERIES_END_SIZE = 3;
   /**
    * Instruction of Parallel end
    */
   public static final byte INSTRUCTION_PARALLEL_END = (byte)0x16;
   /**
    * Size of instruction of Parallel end
    */
   public static final int INSTRUCTION_PARALLEL_END_SIZE = 5;
   
   private byte bitsPerBoolAddress, bitsPerNumericAddress;
   private String runtimeId, versionId;
   private ByteArrayBuilder programBytes;
   private ArrayList<ProgramSeries> series;

   public ProgramBuilder(byte bitsPerBoolAddress, byte bitsPerNumericAddress, String runtimeId, String versionId) {
      this.bitsPerBoolAddress = bitsPerBoolAddress;
      this.bitsPerNumericAddress = bitsPerNumericAddress;
      this.runtimeId = runtimeId;
      this.versionId = versionId;
      this.programBytes = new ByteArrayBuilder();
      this.series = new ArrayList<>();
   }

   public String getRuntimeId() {
      return runtimeId;
   }

   public void setRuntimeId(String runtimeId) {
      if (runtimeId == null) {
         throw new IllegalArgumentException("Runtime ID cannot be null");
      }

      if (runtimeId.getBytes().length != GUID_LENGTH) {
         throw new IllegalArgumentException("Runtime ID must have 16 bytes");
      }
      this.runtimeId = runtimeId;
   }

   public String getVersionId() {
      return versionId;
   }

   public void setVersionId(String versionId) {
      if (versionId == null) {
         throw new IllegalArgumentException("Version ID cannot be null");
      }

      if (versionId.getBytes().length != GUID_LENGTH) {
         throw new IllegalArgumentException("Version ID must have 16 bytes");
      }
      this.versionId = versionId;
   }

   public int getProgramSize() {
      return programBytes.size();
   }

   public void writeProgram() {
      programBytes.clear();
      // Writes runtime id
      for (int i = 0; i < GUID_LENGTH; i++) {
         programBytes.append(runtimeId.getBytes()[i], 8, true, true);
      }
      // Writes version id
      for (int i = 0; i < GUID_LENGTH; i++) {
         programBytes.append(versionId.getBytes()[i], 8, true, true);
      }
      programBytes.append(bitsPerBoolAddress, 8, true, true);
      programBytes.append(bitsPerNumericAddress, 8, true, true);
      // Build instructions
      for (int i = 0; i < series.size(); i++) {
         series.get(i).appendBytes(this);
      }
      // Appends the program end
      programBytes.append(INSTRUCTION_PROGRAM_END, 8, true, true);
   }

   public byte getBitsPerBoolAddress() {
      return bitsPerBoolAddress;
   }

   public void setBitsPerBoolAddress(byte bitsPerBoolAddress) {
      this.bitsPerBoolAddress = bitsPerBoolAddress;
   }

   public byte getBitsPerNumericAddress() {
      return bitsPerNumericAddress;
   }

   public void setBitsPerNumericAddress(byte bitsPerNumericAddress) {
      this.bitsPerNumericAddress = bitsPerNumericAddress;
   }
   
   @Override
   public ByteArrayBuilder getArrayBuilder() {
      return programBytes;
   }

   public ProgramSeries createSerie() {
      ProgramSeries serie = new ProgramSeries();
      series.add(serie);
      return serie;
   }
   
   public void sendProgram(PortConnector port) throws IOException {
      writeProgram();
      port.sendCommand("download "+getProgramSize());
      port.write(getArrayBuilder().getBytes());
   }
   
   public static void main(String[] args) throws Exception {
      PortConnector connector = PortManager.getInstance().connect("/dev/ttyACM0");
      connector.addReaderListener(new PortReaderAdapter() {

         @Override
         public void lineReceived(String line) {
            System.out.println(line);
         }
      });
      ProgramBuilder builder = new ProgramBuilder((byte)0x6, (byte)0x6, "0987654321123456", "1234567890098765");
      ProgramSeries serie = builder.createSerie();

//      serie.add(new NC(builder, 5)); // Porta 7 - In
//      serie.add(new NO(builder, 4)); // Porta 6 - In
//      serie.add(new Coil(builder, 3)); // Porta 5 - Out
      
      serie.add(new NO(builder, 5)); // Porta 7 - In
      serie.add(new RisingEdge(builder, 20));
      serie.add(new SetReset(builder, 3, 3));

      builder.sendProgram(connector);
      connector.sendCommand("config-output 5");
   }
}
