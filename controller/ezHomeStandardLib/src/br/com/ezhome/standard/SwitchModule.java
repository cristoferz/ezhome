package br.com.ezhome.standard;

import br.com.ezhome.lib.modules.Module;
import br.com.ezhome.lib.program.ProgramBuilder;
import br.com.ezhome.lib.program.ProgramSeries;
import br.com.ezhome.lib.program.instruction.Coil;
import br.com.ezhome.lib.program.instruction.FallingEdge;
import br.com.ezhome.lib.program.instruction.NC;
import br.com.ezhome.lib.program.instruction.NO;
import br.com.ezhome.lib.program.instruction.ParallelSeries;
import br.com.ezhome.lib.program.instruction.RisingEdge;
import br.com.ezhome.lib.program.instruction.SetReset;
import org.json.JSONObject;

/**
 *
 * @author cristofer
 */
public class SwitchModule extends Module {

   @Override
   public JSONObject getConfig() {
      return new JSONObject("{ \"moduleConfig\": { \n"
              + "     \"addresses\": [\n"
              + "        { \"name\": \"state\",\n"
              + "          \"type\": \"boolean\"\n"
              + "        } ],\n"
              + "     \"portTypes\": [\n"
              + "        { \"name\": \"normal_input\",\n"
              + "          \"type\": [],\n"
              + "          \"classes\": [\"INT\"],\n"
              + "          \"min\": 0,\n"
              + "          \"max\": -1 \n"
              + "        },\n"
              + "        { \"name\": \"switch_input\",\n"
              + "          \"type\": [],\n"
              + "          \"classes\": [\"INT\"],\n"
              + "          \"min\": 0,\n"
              + "          \"max\": -1 \n"
              + "        },\n"
              + "        { \"name\": \"output\",\n"
              + "          \"type\": [],\n"
              + "          \"classes\": [\"LAM\"],\n"
              + "          \"min\": 0,\n"
              + "          \"max\": -1 \n"
              + "        },\n"
              + "        { \"name\": \"reverted_output\",\n"
              + "          \"type\": [],\n"
              + "          \"classes\": [\"LAM\"],\n"
              + "          \"min\": 1,\n"
              + "          \"max\": -1 \n"
              + "        } ]\n"
              + "   }\n"
              + "}");
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
      for (int i = 0; i < getPortType("switch_input").getPortCount(); i++) {
         ProgramSeries interruptorSerie = interruptors.createSerie();
         interruptorSerie.add(new NO(builder, getPortType("switch_input").getPortAddress(i)));
         interruptorSerie.add(new FallingEdge(builder, getReservedAddress(builder, ++addressIndex)));
      }
      // Internal control address
      inputSerie.add(new SetReset(builder, getAddress("state").getAddress(), getAddress("state").getAddress()));
      // Series for normal output
      for (int i = 0; i < getPortType("output").getPortCount(); i++) {
         ProgramSeries normalOutputSerie = builder.createSerie();
         normalOutputSerie.add(new NO(builder, getAddress("switch").getAddress()));
         normalOutputSerie.add(new Coil(builder, getPortType("output").getPortAddress(i)));
      }
      // Series for reverted output
      for (int i = 0; i < getPortType("reverted_output").getPortCount(); i++) {
         ProgramSeries revertedOutputSerie = builder.createSerie();
         revertedOutputSerie.add(new NC(builder, getAddress("switch").getAddress()));
         revertedOutputSerie.add(new Coil(builder, getPortType("reverted_output").getPortAddress(i)));
      }
   }

}
