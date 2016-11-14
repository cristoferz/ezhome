package br.com.ezhome.lib.compiler;

import br.com.ezhome.lib.ModulePrototype;
import org.json.JSONObject;

/**
 *
 * @author cristofer
 */
public class EzHomeCompilerModulePort {

   private EzHomeCompiler compiler;
   private ModulePrototype modulePrototype;

   public String portType;
   public EzHomeCompilerConnection connection;
   public Integer phisicalAddress;

   private EzHomeCompilerModulePort(EzHomeCompiler compiler, ModulePrototype modulePrototype) {
      this.compiler = compiler;
      this.modulePrototype = modulePrototype;
   }

   public EzHomeCompilerModulePort(EzHomeCompiler compiler, ModulePrototype modulePrototype, JSONObject json) {
      this(compiler, modulePrototype);

      parse(json);
   }

   public void parse(JSONObject json) {
      setPortType(json.getString("portType"));
      if (json.has("connectionIndex")) {
         boolean input = !modulePrototype.getPort(portType).isInput();
         setConnection(createConnection(json.getInt("connectionIndex"), input));
      } else if (json.has("phisicalAddress")) {
         setPhisicalAddress(json.getInt("phisicalAddress"));
      }
   }

   private EzHomeCompilerConnection createConnection(int connectionIndex, boolean input) {
      EzHomeCompilerConnection result = compiler.createConnection(connectionIndex);
      if (input) {
         result.setInput(this);
      } else {
         result.addOutput(this);
      }
      return result;
   }

   public String getPortType() {
      return portType;
   }

   public void setPortType(String portType) {
      if (modulePrototype.getPort(portType) == null) {
         throw new IllegalArgumentException("Invalid portType " + portType);
      }
      this.portType = portType;
   }

   public EzHomeCompilerConnection getConnection() {
      return connection;
   }

   public void setConnection(EzHomeCompilerConnection connection) {
      this.connection = connection;
   }

   public Integer getPhisicalAddress() {
      return phisicalAddress;
   }

   public void setPhisicalAddress(Integer phisicalAddress) {
      this.phisicalAddress = phisicalAddress;
   }

   @Override
   public String toString() {
      String result = "Port: " + getPortType() + " Input: " + modulePrototype.getPort(portType).isInput() + " ";
      if (connection != null) {
         result += "Connection: " + connection.getConnectionIndex();
      } else if (phisicalAddress != null) {
         result += "PhisicalAddress: " + getPhisicalAddress();
      }
      return result;
   }
}
