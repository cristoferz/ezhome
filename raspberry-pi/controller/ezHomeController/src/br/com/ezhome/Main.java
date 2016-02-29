package br.com.ezhome;

import br.com.ezhome.device.TwoWaySerialComm;
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
      new Controller();
      //TwoWaySerialComm.main(args);
   }
   
}
