package br.com.ezhome.device;

/**
 *
 * @author cristofer
 */
public interface PortReaderListener {
   public void lineReceived(String line);
   public void messageReceived(String message);
}
