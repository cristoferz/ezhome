package br.com.ezhome.standard.modules;

import br.com.ezhome.lib.modules.Module;
import br.com.ezhome.lib.program.ProgramBuilder;
import br.com.ezhome.lib.program.ProgramSeries;
import br.com.ezhome.lib.program.instruction.Coil;
import br.com.ezhome.lib.program.instruction.NO;
import org.json.JSONObject;

/**
 *
 * @author cristofer
 */
public class DigitalInputModule extends Module {

   @Override
   public JSONObject getConfig() {
      return new JSONObject("{ \n"
              + "     \"portTypes\": [\n"
              + "        { \"name\": \"input\",\n"
              + "          \"input\": true,\n"
              + "          \"type\": \"DIGITAL\",\n"
              + "          \"phisical\": true,\n"              
              + "          \"min\": 1,\n"
              + "          \"max\": 1 \n"
              + "        },\n"
              + "        { \"name\": \"output\",\n"
              + "          \"input\": false,\n"
              + "          \"type\": \"DIGITAL\",\n"
              + "          \"phisical\": false,\n"
              + "          \"min\": 1,\n"
              + "          \"max\": 1 \n"
              + "        } ]\n"
              + "} ");
   }

   @Override
   public void compile(ProgramBuilder builder) {
      ProgramSeries serie = builder.createSerie();
      serie.add(new NO(builder, getPortType("input").getPortAddress(0)));
      serie.add(new Coil(builder, getPortType("output").getPortAddress(0)));
   }
   
}
