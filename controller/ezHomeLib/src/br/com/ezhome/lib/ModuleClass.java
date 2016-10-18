package br.com.ezhome.lib;

import br.com.ezhome.lib.modules.Module;
import org.json.JSONObject;

/**
 *
 * @author cristofer
 */
public class ModuleClass {

   private Class<Module> classe;
   private String description;
   private JSONObject configs;

   public ModuleClass(Class<Module> classe, String description) throws InstantiationException, IllegalAccessException {
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
      
      this.configs = ((Module)classe.newInstance()).getConfig();
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

}
