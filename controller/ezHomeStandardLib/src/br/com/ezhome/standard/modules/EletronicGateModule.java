package br.com.ezhome.standard.modules;

import br.com.ezhome.lib.modules.Module;
import br.com.ezhome.lib.program.ProgramBuilder;
import org.json.JSONObject;

/**
 *
 * @author cristofer
 */
public class EletronicGateModule extends Module {

   @Override
   public JSONObject getConfig() {
      return new JSONObject("{ \n"
              + "     \"portTypes\": [\n"
              + "        { \"name\": \"closed_sensor\",\n"
              + "          \"input\": true,\n"
              + "          \"type\": \"DIGITAL\",\n"
              + "          \"phisical\": true,\n"              
              + "          \"min\": 1,\n"
              + "          \"max\": 1, \n"
              + "          \"comments\": \"Phisical sensor of totally closed gate\"\n"
              + "        },\n"
              + "        { \"name\": \"opened_sensor\",\n"
              + "          \"input\": true,\n"
              + "          \"type\": \"DIGITAL\",\n"
              + "          \"phisical\": true,\n"              
              + "          \"min\": 1,\n"
              + "          \"max\": 1, \n"
              + "          \"comments\": \"Phisical sensor of totally opened gate\"\n"
              + "        },\n"
              + "        { \"name\": \"pulse_switch\",\n"
              + "          \"input\": true,\n"
              + "          \"type\": \"DIGITAL\",\n"
              + "          \"phisical\": false,\n"              
              + "          \"min\": 1,\n"
              + "          \"max\": 1, \n"
              + "          \"comments\": \"Input for gate interaction. Opens/close/stop gate in a cycle.\"\n"
              + "        },\n"
              + "        { \"name\": \"open_relay\",\n"
              + "          \"input\": false,\n"
              + "          \"type\": \"DIGITAL\",\n"
              + "          \"phisical\": true,\n"
              + "          \"min\": 1,\n"
              + "          \"max\": 1,\n"
              + "          \"comments\": \"Phisical output to activate the relay to open the gate\"\n"
              + "        }, "
              + "        { \"name\": \"close_relay\",\n"
              + "          \"input\": false,\n"
              + "          \"type\": \"DIGITAL\",\n"
              + "          \"phisical\": true,\n"
              + "          \"min\": 1,\n"
              + "          \"max\": 1,\n"
              + "          \"comments\": \"Phisical output to activate the relay to close the gate\"\n"
              + "        }, "
              + "        { \"name\": \"interaction_pulse\",\n"
              + "          \"input\": false,\n"
              + "          \"type\": \"DIGITAL\",\n"
              + "          \"phisical\": false,\n"
              + "          \"min\": 0,\n"
              + "          \"max\": 1,\n"
              + "          \"comments\": \"Generates a pulse for every interaction with the gate. Can be used to turn on a light, for example\"\n"
              + "        } ]\n"
              + " }");
   }

   @Override
   public void compile(ProgramBuilder builder) {
      throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
   }
   
}
