package br.com.ezhome.lib.program;

/**
 *
 * @author cristofer
 */
public class ProgramAddress {
   private int address;
   
   public static ProgramAddress create(int address) {
      return new ProgramAddress(address);
   }

   protected ProgramAddress(int address) {
      this.address = address;
   }

   public int getAddress() {
      return address;
   }
   
   
}
