package br.com.ezhome.lib.modules;

import br.com.ezhome.lib.compiler.EzHomeCompilerModule;
import br.com.ezhome.lib.compiler.EzHomeCompilerModulePort;
import br.com.ezhome.lib.program.ProgramAddress;
import br.com.ezhome.lib.program.ProgramBuilder;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author cristofer
 */
public abstract class Module {

   private final HashMap<String, ModuleParameter> parameters;
   private final HashMap<String, ModuleAddress> addresses;
   private final HashMap<String, ModulePortType> portTypes;

   private final HashMap<Integer, ProgramAddress> reservedAddresses;
   
   private EzHomeCompilerModule compilerModule;

   public Module() {
      this.parameters = new HashMap<>();
      this.addresses = new HashMap<>();
      this.portTypes = new HashMap<>();
      this.reservedAddresses = new HashMap<>();
   }

   public EzHomeCompilerModule getCompilerModule() {
      return compilerModule;
   }

   public void setCompilerModule(EzHomeCompilerModule compilerModule) {
      this.compilerModule = compilerModule;
   }

   public abstract JSONObject getConfig();

   private void loadParameters(Object config) {
      if (config instanceof JSONArray) {
         JSONArray params = (JSONArray) config;
         for (int i = 0; i < params.length(); i++) {
            ModuleParameter param = new ModuleParameter(params.getJSONObject(i));
            parameters.put(param.getName(), param);
         }
      } else {
         throw new IllegalArgumentException("Invalid parameters: has to be a JSONArray");
      }
   }

   private void loadAddresses(Object config) {
      if (config instanceof JSONArray) {
         JSONArray addrs = (JSONArray) config;
         for (int i = 0; i < addrs.length(); i++) {
            ModuleAddress addr = new ModuleAddress(addrs.getJSONObject(i));
            addresses.put(addr.getName(), addr);
         }
      } else {
         throw new IllegalArgumentException("Invalid parameters: has to be a JSONArray");
      }
   }

   private void loadPortTypes(Object config) {
      if (config instanceof JSONArray) {
         JSONArray types = (JSONArray) config;
         for (int i = 0; i < types.length(); i++) {
            ModulePortType portType = new ModulePortType(types.getJSONObject(i));
            portTypes.put(portType.getName(), portType);
         }
      } else {
         throw new IllegalArgumentException("Invalid parameters: has to be a JSONArray");
      }
   }

   public final ProgramAddress getReservedAddress(ProgramBuilder builder, int index) {
      if (reservedAddresses.containsKey(index)) {
         return reservedAddresses.get(index);
      } else {
         ProgramAddress address = builder.getFreeAddress();
         reservedAddresses.put(index, address);
         return address;
      }
   }
//   
//   public ModuleParameter getParameter(String name) {
//      return parameters.get(name);
//   }
//   
//   public ModuleAddress getAddress(String name) {
//      return addresses.get(name);
//   }
   
   public void clearPortTypes() {
      portTypes.clear();
   }
   
   public void addPortTypes(ModulePortType portType) {
      this.portTypes.put(portType.getName(), portType);
   }
   
   public ModulePortType getPortType(String name) {
      return portTypes.get(name);
   }

   public abstract void compile(ProgramBuilder builder);
  
}
