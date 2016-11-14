package br.com.ezhome.standard.modules;

import br.com.ezhome.lib.modules.Module;
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
import org.json.JSONObject;

/**
 *
 * @author cristofer
 */
public class LightningModule extends Module {

   @Override
   public JSONObject getConfig() {
      return new JSONObject("{ \n"
              + "     \"addresses\": [\n"
              + "        { \"name\": \"state\",\n"
              + "          \"type\": \"boolean\"\n"
              + "        } ],\n"
              + "     \"portTypes\": [\n"
              + "        { \"name\": \"input\",\n"
              + "          \"input\": true,\n"
              + "          \"type\": \"DIGITAL\",\n"
              + "          \"phisical\": false,\n"
              + "          \"min\": 0,\n"
              + "          \"max\": -1, \n"
              + "          \"comments\": \"Normally open inputs for lightning module. Invert global state on rising and falling edge.\"\n"
              + "        },\n"
              + "        { \"name\": \"pulse_input\",\n"
              + "          \"input\": true,\n"
              + "          \"type\": \"DIGITAL\",\n"
              + "          \"phisical\": false,\n"
              + "          \"min\": 0,\n"
              + "          \"max\": -1, \n"
              + "          \"comments\": \"Pulse inputs for lightning module. Invert global state on rising edge.\"\n"
              + "        },\n"
              + "        { \"name\": \"output\",\n"
              + "          \"input\": false,\n"
              + "          \"type\": \"DIGITAL\",\n"
              + "          \"phisical\": false,\n"
              + "          \"min\": 0,\n"
              + "          \"max\": -1, \n"
              + "          \"comments\": \"Normal outputs for lightning module. All outputs reflects the global state of module.\"\n"
              + "        },\n"
              + "        { \"name\": \"inverted_output\",\n"
              + "          \"input\": false,\n"
              + "          \"type\": \"DIGITAL\",\n"
              + "          \"phisical\": false,\n"
              + "          \"min\": 0,\n"
              + "          \"max\": -1, \n"
              + "          \"comments\": \"Inverted outputs for lightning module.  All outputs reflects the inverted global state of module.\"\n"
              + "        } ]\n"
              + "   }\n"
              + "");
   }

   @Override
   public void compile(ProgramBuilder builder) {
      int addressIndex = 0;

      ProgramSeries inputSerie = builder.createSerie();
      // new Parallel for interruptors
      ParallelSeries interruptors = new ParallelSeries(builder);
      inputSerie.add(interruptors);
      // loop on normal interruptor array
      if (getPortType("input") != null) {
         for (int i = 0; i < getPortType("input").getPortCount(); i++) {
            ProgramSeries interruptorSerie = interruptors.createSerie();
            interruptorSerie.add(new NO(builder, getPortType("input").getPortAddress(i)));
            ParallelSeries edges = new ParallelSeries(builder);
            interruptorSerie.add(edges);
            edges.createSerie().add(new FallingEdge(builder, getReservedAddress(builder, ++addressIndex)));
            edges.createSerie().add(new RisingEdge(builder, getReservedAddress(builder, ++addressIndex)));
         }
      }
      // loop on bell switch interruptor array
      if (getPortType("pulse_input") != null) {
         for (int i = 0; i < getPortType("pulse_input").getPortCount(); i++) {
            ProgramSeries interruptorSerie = interruptors.createSerie();
            interruptorSerie.add(new NO(builder, getPortType("pulse_input").getPortAddress(i)));
            interruptorSerie.add(new RisingEdge(builder, getReservedAddress(builder, ++addressIndex)));
         }
      }
      // Internal control address
      ProgramAddress address = getReservedAddress(builder, ++addressIndex);
//      inputSerie.add(new SetReset(builder, getAddress("state").getAddress(), getAddress("state").getAddress()));
      inputSerie.add(new SetReset(builder, address, address));

      // Creates a Serie for every output
      // Series for normal output
      if (getPortType("output") != null) {
         for (int i = 0; i < getPortType("output").getPortCount(); i++) {
            ProgramSeries normalOutputSerie = builder.createSerie();
            normalOutputSerie.add(new NO(builder, address));
            normalOutputSerie.add(new Coil(builder, getPortType("output").getPortAddress(i)));
         }
      }
      // Series for reverted output
      if (getPortType("inverted_output") != null) {
         for (int i = 0; i < getPortType("inverted_output").getPortCount(); i++) {
            ProgramSeries revertedOutputSerie = builder.createSerie();
            revertedOutputSerie.add(new NC(builder, address));
            revertedOutputSerie.add(new Coil(builder, getPortType("inverted_output").getPortAddress(i)));
         }
      }
   }

}
