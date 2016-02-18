package br.com.ezhome.comports;

import gnu.io.CommPortIdentifier;
import java.util.Enumeration;

/**
 *
 * @author cristofer
 */
public class ComPortReader {

   public static void main(String[] args) {
      Enumeration ports = CommPortIdentifier.getPortIdentifiers();
      System.out.println("teste");
      while (ports.hasMoreElements()) {
         CommPortIdentifier port = (CommPortIdentifier) ports.nextElement();
         System.out.print(port.getName() + " -> " + port.getCurrentOwner() + " -> ");
      }
   }

}
