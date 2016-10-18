package br.com.ezhome.lib.modules;

import org.json.JSONObject;

/**
 *
 * @author CristoferZ
 */
public class ModuleParameter {

   private String name;
   private int type;
   
   private Object value;

   public static final int BOOLEAN = 1, NUMERIC = 2;
   

   public ModuleParameter(JSONObject config) {
      this.name = config.getString("name").toUpperCase();
      this.type = string2Type(config.getString("type"));
   }

   public ModuleParameter(String name, int type) {
      if (this.name == null) {
         throw new IllegalArgumentException("Parameter name cannot be null");
      }
      this.name = name;
      if (this.type != BOOLEAN && this.type != NUMERIC) {
         throw new IllegalArgumentException("Invalid parameter type");
      }
      this.type = type;
   }
   
   

   public String getName() {
      return name;
   }

   public int getType() {
      return type;
   }
   
   public static int string2Type(String type) {
      if (type == null) {
         throw new IllegalArgumentException("Type cannot be null");
      } else if (type.equalsIgnoreCase("Boolean")) {
         return BOOLEAN;
      } else if (type.equalsIgnoreCase("numeric")) {
         return NUMERIC;
      } else {
         throw new IllegalArgumentException("Invalid type "+type);
      }
   }

   public Object getValue() {
      return value;
   }

   public void setValue(Object value) {
      this.value = value;
   }

   
}
