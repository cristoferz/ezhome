package br.com.ezhome.lib.compiler;

import java.util.HashMap;

/**
 *
 * @author cristofer
 */
public class EzHomeCompilerDevice {

   private EzHomeCompilerDeviceModel model;
   private HashMap<Integer, EzHomeCompilerModulePort> digitalPorts;
   private HashMap<Integer, EzHomeCompilerModulePort> analogicPorts;

   public EzHomeCompilerDevice(EzHomeCompilerDeviceModel model) {
      this.model = model;
      this.digitalPorts = new HashMap<>();
      this.analogicPorts = new HashMap<>();
   }

   public void clear() {
      digitalPorts.clear();
      analogicPorts.clear();
   }

   public void setDigitalPort(int address, EzHomeCompilerModulePort port) {
      if (digitalPorts.containsKey(address)) {
         throw new IllegalArgumentException("Digital Port address " + address + " already in use.");
      }
      digitalPorts.put(address, port);
   }
   
   public void setAnalogicPort(int address, EzHomeCompilerModulePort port) {
      if (analogicPorts.containsKey(address)) {
         throw new IllegalArgumentException("Analogic Port address " + address + " already in use.");
      }
      analogicPorts.put(address, port);
   }
   
   
}
