package br.com.ezhome.lib.program.instruction;

import br.com.ezhome.lib.program.ByteArrayBuilder;
import br.com.ezhome.lib.program.ProgramBuilder;
import br.com.ezhome.lib.program.ProgramSeries;
import br.com.ezhome.lib.program.ProgramSeriesBuilder;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author cristofer
 */
public class ParallelSeries extends ProgramInstruction implements ProgramSeriesBuilder {
   
   private ArrayList<ProgramSeries> series;

   public ParallelSeries(ProgramBuilder builder) {
      super(builder);
      this.series = new ArrayList<>();
   }
   
   @Override
   public ProgramSeries createSerie() {
      ProgramSeries serie = new ProgramSeries();
      series.add(serie);
      return serie;
   }

   @Override
   public byte getInstructionCode() {
      return 0x15;
   }

   @Override
   public int getInstructionSize() {
      return 5;
   }

   @Override
   public void appendBytes(ProgramSeriesBuilder builder) {
      builder.getArrayBuilder().append(getInstructionCode(), getInstructionSize(), false, true);
      for (ProgramSeries serie : series) {
         serie.appendBytes(builder);
      }
      builder.getArrayBuilder().append(ProgramBuilder.INSTRUCTION_PARALLEL_END, ProgramBuilder.INSTRUCTION_PARALLEL_END_SIZE, false, true);
   }
   
   @Override
   public int getDataSize() {
      // Adds the Parallel Start size
      int result = getInstructionSize();
      // Loops for series getting size
      for (ProgramSeries serie : series) {
         result += serie.size();
      }
      // Parallel End Instruction
      result += 5;
      return result;
   }

   @Override
   public ByteArrayBuilder getArrayBuilder() {
      return getBuilder().getArrayBuilder();
   }

   @Override
   public JSONObject toJSON() {
      JSONObject result = new JSONObject();
      JSONArray seriesArray = new JSONArray();
      result.put("Parallel", seriesArray);
      for (int i = 0; i < series.size(); i++) {
         seriesArray.put(series.get(i).toJSON());
      }
      return result;
   }
   
}
