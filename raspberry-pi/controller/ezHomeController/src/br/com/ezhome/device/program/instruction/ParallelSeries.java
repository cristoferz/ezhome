package br.com.ezhome.device.program.instruction;

import br.com.ezhome.device.program.ByteArrayBuilder;
import br.com.ezhome.device.program.ProgramBuilder;
import br.com.ezhome.device.program.ProgramSeries;
import br.com.ezhome.device.program.ProgramSeriesBuilder;
import java.util.ArrayList;

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
   
}
