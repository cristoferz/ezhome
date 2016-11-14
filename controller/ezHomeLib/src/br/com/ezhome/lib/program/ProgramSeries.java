package br.com.ezhome.lib.program;

import br.com.ezhome.lib.program.instruction.ProgramInstruction;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author cristofer
 */
public class ProgramSeries extends ArrayList<ProgramInstruction> {

   public void appendBytes(ProgramSeriesBuilder builder) {
      for (int i = 0; i < size(); i++) {
         get(i).appendBytes(builder);
      }
      builder.getArrayBuilder().append(ProgramBuilder.INSTRUCTION_SERIES_END, ProgramBuilder.INSTRUCTION_SERIES_END_SIZE, false, true);
   }
   
   public int getDataSize() {
      int result = 0;
      for (int i = 0; i < size(); i++) {
         result += get(i).getDataSize();
      }
      result += ProgramBuilder.INSTRUCTION_SERIES_END_SIZE;
      return result;
   }
   
   public JSONObject toJSON() {
      JSONObject result = new JSONObject();
      JSONArray instructions = new JSONArray();
      result.put("serie", instructions);
      for (int i = 0; i < size(); i++) {
         instructions.put(get(i).toJSON());
      }
      return result;
   }
}
