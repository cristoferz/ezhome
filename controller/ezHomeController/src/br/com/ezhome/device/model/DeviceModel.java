package br.com.ezhome.device.model;

import org.json.JSONObject;

/**
 *
 * @author cristofer
 */
public class DeviceModel {
   
   private int modelId;
   private String description;
   private int digitalPorts;
   private String imagePath;

   public DeviceModel(int modelId, String description, int digitalPorts, String imagePath) {
      this.modelId = modelId;
      this.description = description;
      this.digitalPorts = digitalPorts;
      this.imagePath = imagePath;
   }

   public int getModelId() {
      return modelId;
   }

   public void setModelId(int modelId) {
      this.modelId = modelId;
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public int getDigitalPorts() {
      return digitalPorts;
   }

   public void setDigitalPorts(int digitalPorts) {
      this.digitalPorts = digitalPorts;
   }

   public String getImagePath() {
      return imagePath;
   }

   public void setImagePath(String imagePath) {
      this.imagePath = imagePath;
   }
   
   
   
   public JSONObject toJSON() {
      JSONObject result = new JSONObject();
      result.put("modelId", getModelId());
      result.put("description", getDescription());
      result.put("digitalPorts", getDigitalPorts());
      result.put("imagePath", getImagePath());
      return result;
   }
}
