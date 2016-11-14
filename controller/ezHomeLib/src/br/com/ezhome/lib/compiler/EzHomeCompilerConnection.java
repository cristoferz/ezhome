package br.com.ezhome.lib.compiler;

import java.util.ArrayList;

/**
 *
 * @author cristofer
 */
public class EzHomeCompilerConnection {
   private EzHomeCompilerModulePort input;
   private ArrayList<EzHomeCompilerModulePort> outputs;
   
   private int connectionIndex;
   
   private int logicalAddress;

   public EzHomeCompilerConnection(int connectionIndex) {
      this.connectionIndex = connectionIndex;
      outputs = new ArrayList<>();
   }
   
   public void setInput(EzHomeCompilerModulePort port) {
      if (this.input != null) {
         throw new IllegalArgumentException("Connection Index: "+connectionIndex+" already have an input");
      }
      this.input = port;
   }
   
   public void addOutput(EzHomeCompilerModulePort port) {
      this.outputs.add(port);
   }

   public EzHomeCompilerModulePort getInput() {
      return input;
   }

   public ArrayList<EzHomeCompilerModulePort> getOutputs() {
      return outputs;
   }

   public int getConnectionIndex() {
      return connectionIndex;
   }

   public void setConnectionIndex(int connectionIndex) {
      this.connectionIndex = connectionIndex;
   }

   public int getLogicalAddress() {
      return logicalAddress;
   }

   public void setLogicalAddress(int logicalAddress) {
      this.logicalAddress = logicalAddress;
   }
   
   

   @Override
   public String toString() {
      return "Input: "+(input==null?"NOT_DEFINED":"DEFINED")+" #Outputs: "+outputs.size() + " Address: "+getLogicalAddress();
   }
   
   
}
