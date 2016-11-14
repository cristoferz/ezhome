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
   
   private ArrayList<ProgramAddress> portAddresses;
   
   public static final int UNLIMITED = -1;

   public ModulePortType(JSONObject config) {
      this(config.getString("name"));
      
   }

   
   public ModulePortType(String portType) {
      this.name = portType;
      this.portAddresses = new ArrayList<>();
   }

   public String getName() {
      return name;
   }

   public int getPortCount() {
      return portAddresses.size();
   }
   
   public ProgramAddress getPortAddress(int index) {
      return portAddresses.get(index);
   }
   
   public void addPortAddress(ProgramAddress address) {
      portAddresses.add(address);
   }
}
