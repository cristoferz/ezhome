package br.com.ezhome.modules;

import br.com.ezhome.device.program.ProgramAddress;
import org.json.JSONObject;

/**
 *
 * @author cristofer
 */
public class ModuleAddress {
   
   private String name;
   private int type;
   
   private ProgramAddress address;
   
   public static final int BOOLEAN = 1, NUMERIC = 2;

   public ModuleAddress(JSONObject config) {
      this.name = config.getString("name").toUpperCase();
      this.type = string2Type(config.getString("type"));
   }
   
   public ModuleAddress(String name, int type) {
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
   
   public ProgramAddress getAddress() {
      return address;
   }
}
