package br.com.ezhome.device.program.instruction;

import br.com.ezhome.device.program.ProgramBuilder;
import br.com.ezhome.device.program.ProgramSeriesBuilder;

/**
 *
 * @author cristofer
 */
public abstract class ProgramInstruction {
   
   private ProgramBuilder builder;

   public ProgramInstruction(ProgramBuilder builder) {
      this.builder = builder;
   }

   public ProgramBuilder getBuilder() {
      return builder;
   }
   
   /**
    * Return bit code for the instruction
    * @return 
    */
   public abstract byte getInstructionCode();
   /**
    * Return the size for bit code instruction
    * @return 
    */
   public abstract int getInstructionSize();
   
   /**
    * Returns adictional data size for instruction
    * @return 
    */
   public abstract int getDataSize();
   
   /**
    * Appends bytes for instructions to the builder
    * @param builder 
    */
   public abstract void appendBytes(ProgramSeriesBuilder builder);
}
