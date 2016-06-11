package br.com.ezhome;

import br.com.ezhome.device.TwoWaySerialComm;
import br.com.ezhome.device.program.ProgramBuilder;
import java.io.IOException;

/**
 *
 * @author cristofer
 */
public class Main {

   /**
    * @param args the command line arguments
    */
   public static void main(String[] args) throws Exception {
      if (args.length > 0 && args[0].equals("apply")) {
         ProgramBuilder.main(args);
      } else {
         new Controller();
      }
      //TwoWaySerialComm.main(args);
   }
   
}
