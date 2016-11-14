package br.com.ezhome.lib;

import org.json.JSONObject;

/**
 *
 * @author cristofer
 */
public class ModulePrototypePort {
   private int minPorts, maxPorts;
   private boolean input;
   private String name, type;
   
   private ModulePrototype modulePrototype;

   private ModulePrototypePort(ModulePrototype modulePrototype) {
      this.modulePrototype = modulePrototype;
   }
   
   protected ModulePrototypePort(ModulePrototype modulePrototype, JSONObject json) {
      this(modulePrototype);
      parse(json);
   }
   
   public void parse(JSONObject json) {
      setName(json.getString("name"));
      setInput(json.getBoolean("input"));
      setType(json.getString("type"));
      setMinPorts(json.getInt("min"));
      setMaxPorts(json.getInt("max"));
   }

   public int getMinPorts() {
      return minPorts;
   }

   public void setMinPorts(int minPorts) {
      this.minPorts = minPorts;
   }

   public int getMaxPorts() {
      return maxPorts;
   }

   public void setMaxPorts(int maxPorts) {
      this.maxPorts = maxPorts;
   }

   public boolean isInput() {
      return input;
   }

   public void setInput(boolean input) {
      this.input = input;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getType() {
      return type;
   }

   public void setType(String type) {
      this.type = type;
   }

   public ModulePrototype getModulePrototype() {
      return modulePrototype;
   }

   public void setModulePrototype(ModulePrototype modulePrototype) {
      this.modulePrototype = modulePrototype;
   }
   
   
   
   
}
