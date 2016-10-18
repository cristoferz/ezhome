package br.com.ezhome.lib.modules;

import br.com.ezhome.lib.program.ProgramAddress;
import br.com.ezhome.lib.program.ProgramBuilder;
import br.com.ezhome.lib.program.ProgramSeries;
import br.com.ezhome.lib.program.instruction.Coil;
import br.com.ezhome.lib.program.instruction.FallingEdge;
import br.com.ezhome.lib.program.instruction.NC;
import br.com.ezhome.lib.program.instruction.NO;
import br.com.ezhome.lib.program.instruction.ParallelSeries;
import br.com.ezhome.lib.program.instruction.RisingEdge;
import br.com.ezhome.lib.program.instruction.SetReset;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author cristofer
 */
public abstract class Module {

   private HashMap<String, ModuleParameter> parameters;
   private HashMap<String, ModuleAddress> addresses;
   private HashMap<String, ModulePortType> portTypes;

   private HashMap<Integer, ProgramAddress> reservedAddresses;

   public Module() {
      this.parameters = new HashMap<>();
      this.addresses = new HashMap<>();
      this.portTypes = new HashMap<>();
      this.reservedAddresses = new HashMap<>();
   }

   public abstract JSONObject getConfig();

   public void loadConfig(JSONObject config) {
      if (!config.has("moduleConfig")) {
         throw new IllegalArgumentException("Cannot find moduleConfig node.");
      }
      if (config.get("moduleConfig") instanceof JSONObject) {
         JSONObject moduleConfig = config.getJSONObject("moduleConfig");
         if (moduleConfig.has("parameters")) {
            loadParameters(moduleConfig.get("parameters"));
         } else {
            parameters.clear();
         }
         if (moduleConfig.has("addresses")) {
            loadAddresses(moduleConfig.get("addresses"));
         } else {
            addresses.clear();
         }
         if (moduleConfig.has("portTypes")) {
            loadPortTypes(moduleConfig.get("portTypes"));
         } else {
            throw new IllegalArgumentException("At least one portType must be defined");
         }
      } else {
         throw new IllegalArgumentException("Invalid format for moduleConfig node: is not an JSONObject");
      }
   }

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
   
   public ModuleParameter getParameter(String name) {
      return parameters.get(name);
   }
   
   public ModuleAddress getAddress(String name) {
      return addresses.get(name);
   }
   
   public ModulePortType getPortType(String name) {
      return portTypes.get(name);
   }

   public abstract void compile(ProgramBuilder builder);

   public static void main(String[] args) {
      String json = "{ \"moduleConfig\": { \n"
              + "     \"parameters\" : [ \n"
              + "        { \"name\": \"delay\",\n"
              + "           \"type\": \"numeric\"\n"
              + "        } ],\n"
              + "     \"addresses\": [\n"
              + "        { \"name\": \"switch\",\n"
              + "          \"type\": \"numeric\"\n"
              + "        } ],\n"
              + "     \"portTypes\": [\n"
              + "        { \"name\": \"sensors\",\n"
              + "          \"type\": [],\n"
              + "          \"classes\": [\"SEN\"],\n"
              + "          \"min\": 0,\n"
              + "          \"max\": -1 \n"
              + "        },\n"
              + "        { \"name\": \"interruptor\",\n"
              + "          \"type\": [],\n"
              + "          \"classes\": [\"INT\"],\n"
              + "          \"min\": 0,\n"
              + "          \"max\": -1 \n"
              + "        },\n"
              + "        { \"name\": \"key\",\n"
              + "          \"type\": [],\n"
              + "          \"classes\": [\"INT\"],\n"
              + "          \"min\": 0,\n"
              + "          \"max\": -1 \n"
              + "        },\n"
              + "        { \"name\": \"buzzer\",\n"
              + "          \"type\": [],\n"
              + "          \"classes\": [\"SIR\"],\n"
              + "          \"min\": 1,\n"
              + "          \"max\": -1 \n"
              + "        } ]\n"
              + "          { \"name\": \"interruptor\",\n"
              + "            \"type\": [],\n"
              + "            \"classes\": [\"INT\"],\n"
              + "            \"min\": 0,\n"
              + "            \"max\": -1\n"
              + "          } ]\n"
              + "   }\n"
              + "}";
      Module module = new Module() {

         @Override
         public JSONObject getConfig() {
            return null;
         }

         @Override
         public void compile(ProgramBuilder builder) {
            int addressIndex = 0;
            
            ProgramSeries inputSerie = builder.createSerie();
            // new Parallel for interruptors
            ParallelSeries interruptors = new ParallelSeries(builder);
            inputSerie.add(interruptors);
            // loop on normal interruptor array
            for (int i = 0; i < getPortType("normal").getPortCount(); i++) {
               ProgramSeries interruptorSerie = interruptors.createSerie();
               interruptorSerie.add(new NO(builder, getPortType("normal").getPortAddress(i)));
               ParallelSeries edges = new ParallelSeries(builder);
               interruptorSerie.add(edges);
               edges.createSerie().add(new FallingEdge(builder, getReservedAddress(builder, ++addressIndex)));
               edges.createSerie().add(new RisingEdge(builder, getReservedAddress(builder, ++addressIndex)));
            }
            // loop on bell switch interruptor array
            for (int i = 0; i < getPortType("bell_switch").getPortCount(); i++) {
               ProgramSeries interruptorSerie = interruptors.createSerie();
               interruptorSerie.add(new NO(builder, getPortType("bell_switch").getPortAddress(i)));
               interruptorSerie.add(new FallingEdge(builder, getReservedAddress(builder, ++addressIndex)));
            }
            // Internal control address
            inputSerie.add(new SetReset(builder, getAddress("switch").getAddress(), getAddress("switch").getAddress()));
            // Series for normal output
            for (int i = 0; i < getPortType("output").getPortCount(); i++) {
               ProgramSeries normalOutputSerie = builder.createSerie();
               normalOutputSerie.add(new NO(builder, getAddress("switch").getAddress()));
               normalOutputSerie.add(new Coil(builder, getPortType("output").getPortAddress(i)));
            }
            // Series for reverted output
            for (int i = 0; i < getPortType("reverted_output").getPortCount(); i++) {
               ProgramSeries normalOutputSerie = builder.createSerie();
               normalOutputSerie.add(new NC(builder, getAddress("switch").getAddress()));
               normalOutputSerie.add(new Coil(builder, getPortType("reverted_output").getPortAddress(i)));
            }
         }

      };
      module.loadConfig(new JSONObject(json));

   }

}
