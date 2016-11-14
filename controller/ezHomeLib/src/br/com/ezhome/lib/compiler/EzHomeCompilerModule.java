package br.com.ezhome.lib.compiler;

import br.com.ezhome.lib.LibManager;
import br.com.ezhome.lib.ModulePrototype;
import br.com.ezhome.lib.modules.Module;
import br.com.ezhome.lib.modules.ModulePortType;
import br.com.ezhome.lib.program.ProgramAddress;
import br.com.ezhome.lib.program.ProgramBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author cristofer
 */
public class EzHomeCompilerModule {
   private String className, name;
   
   private EzHomeCompiler compiler;
   private ModulePrototype prototype;
   private HashMap<String, ArrayList<EzHomeCompilerModulePort>> ports;

   public EzHomeCompilerModule(EzHomeCompiler compiler) {
      this.compiler = compiler;
      this.ports = new HashMap<>();
   }
   
   
   public EzHomeCompilerModule(EzHomeCompiler compiler, JSONObject json) {
      this(compiler);
      parse(json);
   }

   public EzHomeCompiler getCompiler() {
      return compiler;
   }

   public void setCompiler(EzHomeCompiler compiler) {
      this.compiler = compiler;
   }
   
   public void parse(JSONObject json) {
      setName(json.getString("name"));
      setClassName(json.getString("class"));
      
      JSONArray jsonPorts = json.getJSONArray("ports");
      for (int i = 0; i < jsonPorts.length(); i++) {
         JSONObject port = jsonPorts.getJSONObject(i);
         EzHomeCompilerModulePort modulePort = new EzHomeCompilerModulePort(compiler, prototype, port);
         ArrayList<EzHomeCompilerModulePort> portList = ports.get(modulePort.getPortType());
         if (portList == null) {
            portList = new ArrayList<>();
            ports.put(modulePort.getPortType(), portList);
         }
         portList.add(modulePort);
      }
   }

   public String getClassName() {
      return className;
   }

   public void setClassName(String className) {
      
      // Search ModulePrototype by class
      for (ModulePrototype module : LibManager.getInstance().getModules()) {
         if (module.getClasse().getName().equals(className)) {
            this.prototype = module;
            this.className = className;
            return;
         }
      }
      
      // No Prototype found, throws exception
      throw new IllegalArgumentException("No prototype found for class "+className);
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public HashMap<String, ArrayList<EzHomeCompilerModulePort>> getPorts() {
      return ports;
   }
   
   public void compile(ProgramBuilder builder) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
      Class moduleClass = Class.forName(className);
      Module module = (Module) moduleClass.newInstance();
      for (String portType : ports.keySet()) {
         ModulePortType modulePortType = new ModulePortType(portType);
         for (EzHomeCompilerModulePort ezHomeCompilerModulePort : ports.get(portType)) {
            if (ezHomeCompilerModulePort.getConnection() != null) {
               
               modulePortType.addPortAddress(ProgramAddress.create(ezHomeCompilerModulePort.getConnection().getLogicalAddress()));
            } else {
               ProgramAddress add = getCompiler().getDeviceModel().getAddressForDigital(ezHomeCompilerModulePort.getPhisicalAddress());
               modulePortType.addPortAddress(add);
            }
         }
         module.addPortTypes(modulePortType);
      }
      module.compile(builder);
   }

   @Override
   public String toString() {
      String result = "ModuleDefs: "+getName()+": "+getClassName()+"\n  Ports: [\n";
      for (String key : ports.keySet()) {
         result += "    "+key+": "+ports.get(key)+"\n";
      }
      result += "  ]";
      
      return result;
   }
   
   
}
