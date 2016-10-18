package br.com.ezhome.device;

/**
 *
 * @author cristofer
 */
public class RegisteredDevice {

   private int deviceId;
   private String runtimeId;
   private String versionId;
   private String firmwareVersion;

   private String port;

   private DeviceImpl connector;

   public RegisteredDevice() {
      this.deviceId = -1;
   }

   public int getDeviceId() {
      return deviceId;
   }

   public void setDeviceId(int deviceId) {
      this.deviceId = deviceId;
   }

   public String getRuntimeId() {
      return runtimeId;
   }

   public void setRuntimeId(String runtimeId) {
      this.runtimeId = runtimeId;
   }

   public String getVersionId() {
      return versionId;
   }

   public void setVersionId(String versionId) {
      this.versionId = versionId;
   }

   public String getFirmwareVersion() {
      return firmwareVersion;
   }

   public void setFirmwareVersion(String firmwareVersion) {
      this.firmwareVersion = firmwareVersion;
   }

   public String getPort() {
      return port;
   }

   public void setPort(String port) {
      this.port = port;
   }

   public DeviceImpl getConnector() {
      return connector;
   }

   public void setConnector(DeviceImpl connector) {
      this.connector = connector;
      if (connector != null) {
         setRuntimeId(connector.getRuntimeId());
         setPort(connector.getName());
         if (!this.equals(connector.getRegisteredDevice())) {
            connector.setRegisteredDevice(this);
         }
      } else {
         setRuntimeId(DeviceImpl.BLANK_GUID);
         setPort(null);
      }

   }

}
