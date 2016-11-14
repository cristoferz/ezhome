package br.com.ezhome.device;

import br.com.ezhome.Controller;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;

/**
 * Efetua as escritas nas portas do Raspberry Pi
 *
 * @author cristofer
 */
public class PortWriter {

   private final DeviceImpl connector;
   private final OutputStream outputStream;

   public PortWriter(DeviceImpl connector, OutputStream outputStream) {
      this.connector = connector;
      this.outputStream = outputStream;
   }

   public void write(int value) throws IOException {
      try {
         outputStream.write(value);
      } catch (IOException ex) {
         Controller.getLogger().log(Level.WARNING, "Disconnected from {0} with runtimeId {1} because of errors", new Object[]{connector.getName(), connector.getRuntimeId()});
         connector.close();
         throw ex;
      }
   }

   public void write(byte[] value) throws IOException {
      try {
         outputStream.write(value);
      } catch (IOException ex) {
         Controller.getLogger().log(Level.WARNING, "Disconnected from {0} with runtimeId {1} because of errors", new Object[]{connector.getName(), connector.getRuntimeId()});
         connector.close();
         throw ex;
      }
   }

   public void close() throws IOException {
      outputStream.close();
   }
}
