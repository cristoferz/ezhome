package br.com.ezhome.device.program;

import br.com.ezhome.device.PortConnector;
import br.com.ezhome.device.program.instruction.Coil;
import br.com.ezhome.device.program.instruction.FallingEdge;
import br.com.ezhome.device.program.instruction.NC;
import br.com.ezhome.device.program.instruction.NO;
import br.com.ezhome.device.program.instruction.ParallelSeries;
import br.com.ezhome.device.program.instruction.ProgramInstruction;
import br.com.ezhome.device.program.instruction.RisingEdge;
import br.com.ezhome.device.program.instruction.SetReset;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.json.JSONArray;
import org.json.JSONObject;

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
   public static final byte INSTRUCTION_PROGRAM_END = (byte) 0xFF;
   /**
    * Instruction of Series End
    */
   public static final byte INSTRUCTION_SERIES_END = (byte) 0x03;
   /**
    * Size of instruction of Series End
    */
   public static final int INSTRUCTION_SERIES_END_SIZE = 3;
   /**
    * Instruction of Parallel end
    */
   public static final byte INSTRUCTION_PARALLEL_END = (byte) 0x16;
   /**
    * Size of instruction of Parallel end
    */
   public static final int INSTRUCTION_PARALLEL_END_SIZE = 5;

   private byte bitsPerBoolAddress, bitsPerNumericAddress;
   private String runtimeId, versionId;
   private final ByteArrayBuilder programBytes;
   private final ArrayList<ProgramSeries> series;
   private final HashMap<Integer, ProgramAddress> addresses;

   public ProgramBuilder(byte bitsPerBoolAddress, byte bitsPerNumericAddress, String runtimeId, String versionId) {
      this.bitsPerBoolAddress = bitsPerBoolAddress;
      this.bitsPerNumericAddress = bitsPerNumericAddress;
      this.runtimeId = runtimeId;
      this.versionId = versionId;
      this.programBytes = new ByteArrayBuilder();
      this.series = new ArrayList<>();
      this.addresses = new HashMap<>();
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

   @Override
   public ProgramSeries createSerie() {
      ProgramSeries serie = new ProgramSeries();
      series.add(serie);
      return serie;
   }

   public void sendProgram(PortConnector port) throws IOException {
      writeProgram();
      port.sendCommand("download " + getProgramSize());
      port.write(getArrayBuilder().getBytes());
   }

   public void printJSON(JSONObject json) {
      Iterator<String> keys = json.keys();
      while (keys.hasNext()) {
         String key = keys.next();
         Object value = json.get(key);
         System.out.println("Key: " + key + " Value: " + value);
         if (value instanceof JSONObject) {
            printJSON((JSONObject) value);
         } else if (value instanceof JSONArray) {
            printJSONArray((JSONArray) value);
//         } else {
//            System.out.println(value.getClass().getName());
         }
      }
   }

   public void printJSONArray(JSONArray json) {
      for (int i = 0; i < json.length(); i++) {
         Object value = json.get(i);
         if (value instanceof JSONObject) {
            printJSON((JSONObject) value);
         } else if (value instanceof JSONArray) {
            printJSONArray((JSONArray) value);
         } else {
            System.out.println(value);
         }
      }
   }

   private ProgramInstruction parseInstruction(JSONObject json) throws Exception {
      if (json.has("NO")) {
         if (json.get("NO") instanceof Boolean) {
            return new NO(this, json.getBoolean("NO"));
         } else {
            return new NO(this, new ProgramAddress(json.getInt("NO")));
         }
      } else if (json.has("NC")) {
         if (json.get("NC") instanceof Boolean) {
            return new NC(this, json.getBoolean("NC"));
         } else {
            return new NC(this, new ProgramAddress(json.getInt("NC")));
         }
      } else if (json.has("RisingEdge")) {
         return new RisingEdge(this, new ProgramAddress(json.getInt("RisingEdge")));
      } else if (json.has("FallingEdge")) {
         return new FallingEdge(this, new ProgramAddress(json.getInt("FallingEdge")));
      } else if (json.has("SetReset")) {
         if (json.getJSONObject("SetReset").get("reset") instanceof Boolean) {
            return new SetReset(this, new ProgramAddress(json.getJSONObject("SetReset").getInt("address")), json.getJSONObject("SetReset").getBoolean("reset"));
         } else {
            return new SetReset(this, new ProgramAddress(json.getJSONObject("SetReset").getInt("address")), new ProgramAddress(json.getJSONObject("SetReset").getInt("reset")));
         }
      } else if (json.has("Coil")) {
         return new Coil(this, new ProgramAddress(json.getInt("Coil")));
      } else if (json.has("Parallel")) {
         ParallelSeries parallel = new ParallelSeries(this);
         loadJSONSeries(json.getJSONArray("Parallel"), parallel);
         return parallel;
      } else {
         throw new Exception("Nenhuma instrução valida");
      }
   }

   private void loadJSONSeries(JSONArray array, ProgramSeriesBuilder builder) throws Exception {
      for (int i = 0; i < array.length(); i++) {
         JSONObject serieObj = array.getJSONObject(i);
         if (serieObj.has("serie")) {
            ProgramSeries serie = builder.createSerie();
            JSONArray instructions = serieObj.getJSONArray("serie");
            for (int j = 0; j < instructions.length(); j++) {
               //System.out.println("serie "+i + " = "+instructions.length() + " "+serieObj);
               serie.add(parseInstruction(instructions.getJSONObject(j)));
            }
         }
      }
   }

   private void loadJSON(JSONObject json, ProgramSeriesBuilder builder) throws Exception {
      if (builder instanceof ProgramBuilder) {
         // clear
      }
      if (json.has("program")) {
         JSONArray array = json.getJSONArray("program");
         loadJSONSeries(array, builder);
      }
   }

   public void loadJSON(JSONObject json) throws Exception {
      loadJSON(json, this);
   }
   
   public ProgramAddress getFreeAddress() {
      int i = 100;
      while(true) {
         if(!addresses.containsKey(i)) {
            return new ProgramAddress(i);
         }
         i++;
      }
   }

   public static void main(String[] args) throws Exception {

//      JSONObject obj2 = new JSONObject("{ \n"
//              + "   \"program\": [ \n"
//              + "      { \"serie\": [\n"
//              + "         { \"NC\": 5 },\n"
//              + "         { \"Parallel\": [ \n"
//              + "                        { \"serie\": [ { \"NO\": 4 } , { \"NC\": 8 }] },\n"
//              + "                        { \"serie\": [ { \"NO\": 6 } , { \"NC\": 7 }] }\n"
//              + "                       ] },\n"
//              + "         { \"Coil\": 3 }\n"
//              + "      ] },\n"
//              + "      { \"serie\": [\n"
//              + "         { \"NO\": 5 },\n"
//              + "         { \"Parallel\": [ \n"
//              + "                        { \"serie\": [ { \"NO\": 4 } , { \"NC\": 8 }] },\n"
//              + "                        { \"serie\": [ { \"NO\": 6 } , { \"NC\": 7 }] }\n"
//              + "                       ] },\n"
//              + "         { \"Coil\": 3 }\n"
//              + "      ] }\n"
//              + "   ] }");
//
//      JSONObject obj = new JSONObject("{ \"program\": \n"
//              + "   [ { \"serie\": [ \n"
//              + "        { \"Parallel\": [ \n"
//              + "           { \"serie\": [ { \"NO\": 9 } ] }, \n"
//              + "           { \"serie\": [ { \"NC\": 50 } ] } \n"
//              + "        ] } , \n"
//              + "        { \"Parallel\": [ \n"
//              + "           { \"serie\": [ { \"NC\": 9 } ] }, \n"
//              + "           { \"serie\": [ { \"NO\": 50 } ] } \n"
//              + "        ] }, \n"
//              + "        { \"Coil\": 4 } \n"
//              + "     ] } \n"
//              + "   ] }");
//
//      ProgramBuilder builder = new ProgramBuilder((byte) 0x6, (byte) 0x6, "0987654321123456", "1234567890098765");
//      builder.loadJSON(obj);
//
////      if (true) return;
//      PortConnector connector = PortManager.getInstance().connect("/dev/ttyACM0");
//      connector.addReaderListener(new PortReaderAdapter() {
//
//         @Override
//         public void lineReceived(String line) {
//            System.out.println(line);
//         }
//      });
//      ProgramSeries serie = builder.createSerie();
//      serie.add(new NC(builder, 48));
//      serie.add(new Coil(builder, 1)); // Porta 5 - Out
//      ProgramSeries serie2 = builder.createSerie();
//      serie2.add(new NC(builder, 49));
//      serie2.add(new Coil(builder, 2)); // Porta 5 - Out
//      ProgramSeries serie3 = builder.createSerie();
//      serie3.add(new NC(builder, 50));
//      serie3.add(new Coil(builder, 3)); // Porta 5 - Out
////      ProgramSeries serie4 = builder.createSerie();
////      serie4.add(new NC(builder, 51));
////      serie4.add(new Coil(builder, 4)); // Porta 5 - Out
//
//      ProgramSeries serie5 = builder.createSerie();
////      serie5.add(new NC(builder, 9));
//
//      ParallelSeries parallel = new ParallelSeries(builder);
//      serie5.add(parallel);
////
//      ProgramSeries s1 = parallel.createSerie();
//      s1.add(new NO(builder, 9));
//      ProgramSeries s2 = parallel.createSerie();
//      s2.add(new NC(builder, 50));
//
//      ParallelSeries parallel2 = new ParallelSeries(builder);
//      serie5.add(parallel2);
////
//      ProgramSeries s12 = parallel2.createSerie();
//      s12.add(new NC(builder, 9));
//      ProgramSeries s22 = parallel2.createSerie();
//      s22.add(new NO(builder, 50));
//      serie5.add(new Coil(builder, 4)); // Porta 5 - Out
//
////      serie.add(new NC(builder, 5)); // Porta 7 - In
////      serie.add(new NO(builder, 4)); // Porta 6 - In
////      serie.add(new Coil(builder, 3)); // Porta 5 - Out
////      serie.add(new NO(builder, 5)); // Porta 7 - In
////      serie.add(new RisingEdge(builder, 20));
////      serie.add(new SetReset(builder, 3, 3));
//      builder.sendProgram(connector);
//      connector.sendCommand("config-output 3");
//      connector.sendCommand("config-output 4");
//      connector.sendCommand("config-output 5");
//      connector.sendCommand("config-output 6");

   }

}
