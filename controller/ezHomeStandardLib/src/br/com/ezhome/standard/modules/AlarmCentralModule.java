package br.com.ezhome.standard.modules;

import br.com.ezhome.lib.modules.Module;
import br.com.ezhome.lib.program.ProgramBuilder;
import org.json.JSONObject;

/**
 *
 * @author cristofer
 */
public class AlarmCentralModule extends Module {

   @Override
   public JSONObject getConfig() {
      return new JSONObject();
   }

   @Override
   public void compile(ProgramBuilder builder) {
      throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
   }
   
}
