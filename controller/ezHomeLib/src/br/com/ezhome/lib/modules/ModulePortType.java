package br.com.ezhome.lib.modules;

import br.com.ezhome.lib.program.ProgramAddress;
import java.util.ArrayList;
import org.json.JSONObject;

/**
 *
 * @author cristofer
 */
public class ModulePortType {
   private String name;
   private String[] allowedClasses;
   private int min, max;
   
   private ArrayList<ProgramAddress> portAddresses;
   
   public static final int UNLIMITED = -1;

   public ModulePortType(JSONObject config) {
      this.name = config.getString("name").toUpperCase();
      this.allowedClasses = new String[0];
      this.min = config.getInt("min");
      this.max = config.getInt("max");
      this.portAddresses = new ArrayList<>();
   }

   
   public ModulePortType(String portType, String[] allowedClasses, int min, int max) {
      this.name = portType.toUpperCase();
      this.allowedClasses = allowedClasses;
      this.min = min;
      this.max = max;
   }

   public String getName() {
      return name;
   }

   public String[] getAllowedClasses() {
      return allowedClasses;
   }

   public int getMin() {
      return min;
   }

   public int getMax() {
      return max;
   }
   
   public int getPortCount() {
      return portAddresses.size();
   }
   
   public ProgramAddress getPortAddress(int index) {
      return portAddresses.get(index);
   }
}
