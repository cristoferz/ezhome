package br.com.ezhome.device.event;

import br.com.ezhome.device.Device;
import br.com.ezhome.device.RegisteredDevice;
import org.json.JSONObject;

/**
 *
 * @author cristofer
 */
public class DevicePortStateEvent extends DeviceEvent {

   private final Device.PortState portState;

   public DevicePortStateEvent(Device device, Device.PortState portState) {
      super(device);
      this.portState = portState;
   }

   @Override
   public JSONObject getJSON() {
      JSONObject result = new JSONObject();
      result.put("address", portState.getAddress());
      result.put("state", portState.getState());
      if (portState.isInput()) {
         result.put("input", true);
      }
      if (portState.isOutput()) {
         result.put("output", true);
      }
      if (portState.isInternalAddress()) {
         result.put("internalAddress", true);
      }
      return result;
   }

   @Override
   public String getEventType() {
      return "PortState";
   }

}
