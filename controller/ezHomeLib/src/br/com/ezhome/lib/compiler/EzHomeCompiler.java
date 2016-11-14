package br.com.ezhome.lib.compiler;

import br.com.ezhome.lib.LibManager;
import br.com.ezhome.lib.logger.EzHomeLogger;
import br.com.ezhome.lib.program.ProgramBuilder;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author cristofer
 */
public class EzHomeCompiler {
   
   private ArrayList<EzHomeCompilerModule> modules;
   private HashMap<Integer, EzHomeCompilerConnection> connections;
   
   private EzHomeCompilerDeviceModel deviceModel;

   public EzHomeCompiler() {
      this.modules = new ArrayList<>();
      this.connections = new HashMap<>();
   }
   
   /**
    * Parses version configuration
    * @param json 
    */
   public void parse(JSONObject json) {
      JSONArray jsonModules = json.getJSONArray("modules");
      for (int i = 0; i < jsonModules.length(); i++) {
         JSONObject module = jsonModules.getJSONObject(i);
         modules.add(new EzHomeCompilerModule(this, module));
      }
   }
   
   /**
    * Validates connections and configs
    */
   public void validate() {
      
   }
   
   public void assignAddresses() {
      if (deviceModel == null) {
         throw new IllegalStateException("No DeviceModel is defined yet.");
      }
      
      // Calculates logicalAddress for all connections
      int logicalAddress = deviceModel.getDigitalAddressEnd() + 1;
      for (Integer connectionIndex : connections.keySet()) {
         connections.get(connectionIndex).setLogicalAddress(logicalAddress);
         logicalAddress++;
      }
      
      // C
   }
   
   /**
    * Compiles program into specified ProgramBuilder
    * @param builder 
    */
   public void compile(ProgramBuilder builder) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
      assignAddresses();
      for (EzHomeCompilerModule module : modules) {
         module.compile(builder);
      }
   }
   
   public EzHomeCompilerConnection createConnection(int connectionIndex) {
      if (!connections.containsKey(connectionIndex)) {
         connections.put(connectionIndex, new EzHomeCompilerConnection(connectionIndex));
      }
      return connections.get(connectionIndex);
   }

   public EzHomeCompilerDeviceModel getDeviceModel() {
      return deviceModel;
   }

   public void setDeviceModel(EzHomeCompilerDeviceModel deviceModel) {
      this.deviceModel = deviceModel;
   }
   
   

   @Override
   public String toString() {
      String result = "Modules: [\n";
      for (EzHomeCompilerModule module : modules) {
         result += "  "+module.toString()+"\n";
      }
      result += "]\n";
      result += "Connections: [\n";
      for (Integer index : connections.keySet()) {
         result += "  "+index+": "+connections.get(index)+"\n";
      }
      result += "]\n";
      return result;
   }
   
   
   
   public static void main(String[] args) throws Exception {
      EzHomeLogger.init();
      LibManager.getInstance().loadJar(new File("/home/cristofer/git/ezhome/controller/ezHomeStandardLib/dist/ezHomeStandardLib.jar"));
      
      EzHomeCompiler e = new EzHomeCompiler();
      e.parse(new JSONObject("{\n" +
"      \"modules\": [\n" +
"         {\n" +
"            \"class\": \"br.com.ezhome.standard.DigitalInputModule\",\n" +
"            \"name\": \"Kitchen outside door switch\",\n" +
"            \"ports\": [\n" +
"               {\n" +
"                  \"portType\": \"input\",\n" +
"                  \"phisicalAddress\": 1\n" +
"               },\n" +
"               {\n" +
"                  \"portType\": \"output\",\n" +
"                  \"connectionIndex\": 1\n" +
"               }\n" +
"            ]\n" +
"         },\n" +
"         {\n" +
"            \"class\": \"br.com.ezhome.standard.DigitalInputModule\",\n" +
"            \"name\": \"Kitchen inside door switch\",\n" +
"            \"ports\": [\n" +
"               {\n" +
"                  \"portType\": \"input\",\n" +
"                  \"phisicalAddress\": 2\n" +
"               },\n" +
"               {\n" +
"                  \"portType\": \"output\",\n" +
"                  \"connectionIndex\": 2\n" +
"               }\n" +
"            ]\n" +
"         },\n" +
"         {\n" +
"            \"class\": \"br.com.ezhome.standard.DigitalOutputModule\",\n" +
"            \"name\": \"Kitchen Light\",\n" +
"            \"ports\": [\n" +
"               {\n" +
"                  \"portType\": \"input\",\n" +
"                  \"connectionIndex\": 3\n" +
"               },\n" +
"               {\n" +
"                  \"portType\": \"output\",\n" +
"                  \"phisicalAddress\": 3 \n" +
"               },\n" +
"            ]\n" +
"         },\n" +
"         {\n" +
"            \"class\": \"br.com.ezhome.standard.LightningModule\",\n" +
"            \"name\": \"Kitchen Light Controller\",\n" +
"            \"ports\": [\n" +
"               {\n" +
"                  \"portType\": \"input\",\n" +
"                  \"connectionIndex\": 1\n" +
"               },\n" +
"               {\n" + 
"                  \"portType\": \"input\",\n" +
"                  \"connectionIndex\": 2\n" +
"               },\n" +
"               {\n" +
"                  \"portType\": \"output\",\n" +
"                  \"connectionIndex\": 3\n" +
"               }\n" +
"            ]\n" +
"         }\n" +
"\n" +
"      ]\n" +
"    }"));
      e.setDeviceModel(new EzHomeCompilerDeviceModel() {
         @Override
         public int getDigitalAddressStart() {
            return 0;
         }

         @Override
         public int getDigitalAddressEnd() {
            return 53;
         }

         @Override
         public int getAnalogAddressStart() {
            return 0;
         }

         @Override
         public int getAnalogAddressEnd() {
            return 15;
         }

         @Override
         public String getHexResourcePath() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
         }
         
         
      });
      e.assignAddresses();
      System.out.println(e.toString());
      
      ProgramBuilder builder = new ProgramBuilder((byte)10, (byte)10, "1234567890123456", "1234567890123456");
      e.compile(builder);
      
      System.out.println(builder.toJSON().toString(3));
      
      
//      ProgramBuilder b = new ProgramBuilder(0, 0, runtimeId, versionId)
   }
   
}
