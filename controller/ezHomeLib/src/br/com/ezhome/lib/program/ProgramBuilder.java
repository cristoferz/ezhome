package br.com.ezhome.lib.program;

import br.com.ezhome.lib.device.Device;
import br.com.ezhome.lib.program.instruction.BooleanValue;
import br.com.ezhome.lib.program.instruction.Coil;
import br.com.ezhome.lib.program.instruction.Comparator;
import br.com.ezhome.lib.program.instruction.Counter;
import br.com.ezhome.lib.program.instruction.FallingEdge;
import br.com.ezhome.lib.program.instruction.MathOperation;
import br.com.ezhome.lib.program.instruction.NC;
import br.com.ezhome.lib.program.instruction.NO;
import br.com.ezhome.lib.program.instruction.NumericValue;
import br.com.ezhome.lib.program.instruction.ParallelSeries;
import br.com.ezhome.lib.program.instruction.ProgramInstruction;
import br.com.ezhome.lib.program.instruction.RisingEdge;
import br.com.ezhome.lib.program.instruction.SetReset;
import br.com.ezhome.lib.program.instruction.TimerOff;
import br.com.ezhome.lib.program.instruction.TimerOn;
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

      if (runtimeId.getBytes().length != GUID_LENGTH * 2) {
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

      if (versionId.getBytes().length != GUID_LENGTH * 2) {
         throw new IllegalArgumentException("Version ID must have 16 bytes");
      }
      this.versionId = versionId;
   }

   public int getProgramSize() {
      return programBytes.size();
   }

   public void writeProgram() {
      calcBitsPerAddress();
      programBytes.clear();
      appendGUID(runtimeId);
      appendGUID(versionId);

      programBytes.append(bitsPerBoolAddress, 8, true, true);
      programBytes.append(bitsPerNumericAddress, 8, true, true);
      // Build instructions
      for (int i = 0; i < series.size(); i++) {
         series.get(i).appendBytes(this);
      }
      // Appends the program end
      programBytes.append(INSTRUCTION_PROGRAM_END, 8, true, true);
   }

   private void calcBitsPerAddress() {
      Integer maxAddress = 56; // Max Address for ports
      for (Integer address : addresses.keySet()) {
         if (address > maxAddress) {
            maxAddress = address;
         }
      }
      byte perBool = 0;
      for (byte i = 0; i < 255; i++) { // Looks for the best size
         if (maxAddress < (1 << i)) {
            perBool = i;
            break;
         }
      }
      this.bitsPerBoolAddress = perBool;
   }

   public void appendGUID(String guid) {
      int[] indexes = new int[]{3, 2, 1, 0, 5, 4, 7, 6, 8, 9, 10, 11, 12, 13, 14, 15};
      for (int index : indexes) {
         programBytes.append((byte) Integer.parseInt(runtimeId.substring(index * 2, (index * 2) + 2), 16), 8, true, true);
      }
   }

   public JSONObject toJSON() {
      JSONObject result = new JSONObject();
      JSONArray seriesArray = new JSONArray();
      result.append("program", seriesArray);
      for (int i = 0; i < series.size(); i++) {
         seriesArray.put(series.get(i).toJSON());
      }
      return result;
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

   public void sendProgram(Device port) throws IOException {
      writeProgram();
      port.sendCommand("download " + getProgramSize());
      port.write(getArrayBuilder().getBytes());
   }

   public void printJSON(JSONObject json) {
      Iterator<String> keys = json.keys();
      while (keys.hasNext()) {
         String key = keys.next();
         Object value = json.get(key);
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
         return new NO(this, json);
      } else if (json.has("NC")) {
         return new NC(this, json);
      } else if (json.has("RisingEdge")) {
         return new RisingEdge(this, getAddress(json.getInt("RisingEdge")));
      } else if (json.has("FallingEdge")) {
         return new FallingEdge(this, getAddress(json.getInt("FallingEdge")));
      } else if (json.has("SetReset")) {
         if (json.getJSONObject("SetReset").get("reset") instanceof Boolean) {
            return new SetReset(this, getAddress(json.getJSONObject("SetReset").getInt("address")), json.getJSONObject("SetReset").getBoolean("reset"));
         } else {
            return new SetReset(this, getAddress(json.getJSONObject("SetReset").getInt("address")), getAddress(json.getJSONObject("SetReset").getInt("reset")));
         }
      } else if (json.has("Coil")) {
         return new Coil(this, getAddress(json.getInt("Coil")));
      } else if (json.has("Parallel")) {
         ParallelSeries parallel = new ParallelSeries(this);
         loadJSONSeries(json.getJSONArray("Parallel"), parallel);
         return parallel;
      } else if (json.has("TimerOn")) {
         return new TimerOn(this, 
                 NumericValue.fromJSON(this, json.getJSONObject("TimerOn").getJSONObject("setpointValue")), 
                 getAddress(json.getJSONObject("TimerOn").getInt("doneAddress")),
                 getAddress(json.getJSONObject("TimerOn").getInt("elapsedAddress"))
         );
      } else if (json.has("TimerOff")) {
         return new TimerOff(this, 
                 NumericValue.fromJSON(this, json.getJSONObject("TimerOff").getJSONObject("setpointValue")), 
                 getAddress(json.getJSONObject("TimerOff").getInt("doneAddress")),
                 getAddress(json.getJSONObject("TimerOff").getInt("elapsedAddress"))
         );
      } else if (json.has("Counter")) {
         return new Counter(this, 
                 json.getJSONObject("Counter").getBoolean("countDown"),
                 NumericValue.fromJSON(this, json.getJSONObject("Counter").getJSONObject("setpointValue")), 
                 BooleanValue.fromJSON(this, json.getJSONObject("Counter"), "resetValue"),
                 getAddress(json.getJSONObject("Counter").getInt("doneAddress")),
                 getAddress(json.getJSONObject("Counter").getInt("countAddress")),
                 getAddress(json.getJSONObject("Counter").getInt("oneshotStateAddress"))
         );
      } else if (json.has("Comparator")) {
         return new Comparator(this, json);
      } else if (json.has("MathOperation")) {
         return new MathOperation(this, json);
      } else {
         throw new Exception("Nenhuma instrução valida em "+json.toString(2));
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
      int i = 64;
      while (true) {
         if (!addresses.containsKey(i)) {
            return getAddress(i);
         }
         i++;
      }
   }

   public ProgramAddress getAddress(int address) {
      ProgramAddress result = new ProgramAddress(address);
      addresses.put(address, result);
      return result;
   }

   public static void main(String[] args) throws Exception {
      int v = 7;
      int result = 1;
      //for (int i = 0; i < v; i++) {
      result = result << v - 1;
      //}
      System.out.println(result);
//      for (int i = 0; i < 16; i++) {
//         
//      System.out.println(Integer.toHexString((byte)Integer.parseInt("0123456789ABCDEF".substring(i, i+1), 16)));
//      }
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
