package br.com.ezhome.comports;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Efetua as escritas nas portas do Raspberry Pi
 *
 * @author cristofer
 */
public class PortWriter {
   private OutputStream outputStream;

   public PortWriter(OutputStream outputStream) {
      this.outputStream = outputStream;
   }
   
   public void write(int value) throws IOException {
      outputStream.write(value);
   }
   
   public void write(byte[] value) throws IOException {
      outputStream.write(value);
   }
   
   public void close() throws IOException {
      outputStream.close();
   }
}
