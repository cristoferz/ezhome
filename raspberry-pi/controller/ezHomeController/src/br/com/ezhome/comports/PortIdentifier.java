package br.com.ezhome.comports;

import gnu.io.CommPortIdentifier;
import java.util.Enumeration;

/**
 * Utilizado para a detecção dos equipamentos conectados ao raspberry
 * 
 * @author cristofer
 */
public class PortIdentifier {

   public static Enumeration<CommPortIdentifier> listPorts() {
      return CommPortIdentifier.getPortIdentifiers();
   }
}
