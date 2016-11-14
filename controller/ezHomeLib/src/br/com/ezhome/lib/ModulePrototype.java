package br.com.ezhome.lib;

import br.com.ezhome.lib.modules.Module;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author cristofer
 */
public class ModulePrototype {

   private Class<Module> classe;
   private String description;
   private JSONObject configs;
   private HashMap<String, ModulePrototypePort> ports;

   public ModulePrototype(Class<Module> classe, String description) throws InstantiationException, IllegalAccessException {
      ports = new HashMap<>();
      setClasse(classe);
      this.description = description;
   }

   public Class<Module> getClasse() {
      return classe;
   }

   public void setClasse(Class<Module> classe) throws InstantiationException, IllegalAccessException {
      if (!Module.class.isAssignableFrom(classe)) {
         throw new IllegalArgumentException("Invalid class: Must be instance of br.com.ezhome.lib.modules.Module");
      }
      this.classe = classe;

      loadConfig(((Module) classe.newInstance()).getConfig());
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public JSONObject getConfigs() {
      return configs;
   }

   private void loadConfig(JSONObject json) {
      this.configs = json;

      // load ports
      if (json.has("portTypes")) {
         JSONArray portTypes = json.getJSONArray("portTypes");
         for (int i = 0; i < portTypes.length(); i++) {
            JSONObject portType = portTypes.getJSONObject(i);
            ModulePrototypePort port = new ModulePrototypePort(this, portType);
            ports.put(port.getName(), port);
         }
      }
   }

   public ModulePrototypePort getPort(String portType) {
      return ports.get(portType);
   }

}
