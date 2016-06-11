package br.com.ezhome.device;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * A Firmware uploader for Arduino devices
 *
 * @author cristofer
 */
public class FirmwareUploader {

   private String avrDevice;
   private int baudRate;
   private String programmer;
   private String port;
   private boolean autoErase;
   private File hexFile;
   private File configFile;

   public static final int ARDUINO_UNO = 1;
   public static final int ARDUINO_MEGA = 2;
   public static final int ARDUINO_NANO = 3;

   /**
    * Initializes a Firmware upload instance with default parameters
    *
    */
   public FirmwareUploader() {
      this.programmer = "wiring";
      this.baudRate = 115200;
      this.autoErase = false;
      this.avrDevice = null;
      this.port = null;
      this.hexFile = null;
      this.configFile = null;
   }

   /**
    * Return the current AvrDevice from this FirmwareUploader. AvrDevice is the
    * microcontroller model from Arduino
    *
    * @return A String with the microcontroller model from Arduino
    */
   public String getAvrDevice() {
      return avrDevice;
   }

   /**
    * Sets the current AvrDevice for this FirmwareUploader.
    *
    * @param avrDevice
    * @see #getAvrDevice()
    */
   public void setAvrDevice(String avrDevice) {
      this.avrDevice = avrDevice;
   }

   /**
    * Returns the current BaudRate for this FirmwareUploader.
    *
    * @return
    */
   public int getBaudRate() {
      return baudRate;
   }

   /**
    * Sets the BaudRate for this FirmwareUpload. The default is most accepted by
    * AVRs, so set only if necessary
    *
    * @param baudRate
    */
   public void setBaudRate(int baudRate) {
      this.baudRate = baudRate;
   }

   /**
    * Returns current programmer for this FirmwareUploader. Defaults to arduino
    *
    * @return
    */
   public String getProgrammer() {
      return programmer;
   }

   /**
    * Sets the programmer for this FirmwareUploader. Defaults to arduino
    *
    * @param programmer
    */
   public void setProgrammer(String programmer) {
      this.programmer = programmer;
   }

   /**
    * Return the serial port for this FirmwareUploader.
    *
    * @return
    */
   public String getPort() {
      return port;
   }

   /**
    * Sets the serial port for this FirmwareUploader
    *
    * @param port
    */
   public void setPort(String port) {
      this.port = port;
   }

   public boolean isAutoErase() {
      return autoErase;
   }

   public void setAutoErase(boolean autoErase) {
      this.autoErase = autoErase;
   }

   public File getHexFile() {
      return hexFile;
   }

   public void setHexFile(File hexFile) {
      this.hexFile = hexFile;
   }

   public File getConfigFile() {
      return configFile;
   }

   public void setConfigFile(File configFile) {
      this.configFile = configFile;
   }

   public void preloadConfig(int type) throws IOException {
      InputStream is;
      switch (type) {
         case ARDUINO_UNO:
            is = getClass().getResourceAsStream("/br/com/ezhome/device/hex/Master.ino.standard.hex");
            setAvrDevice("atmega328p");
            break;
         case ARDUINO_MEGA:
            is = getClass().getResourceAsStream("/br/com/ezhome/device/hex/Master.ino.mega.hex");
            setAvrDevice("atmega2560");
            break;
         case ARDUINO_NANO:
            is = getClass().getResourceAsStream("/br/com/ezhome/device/hex/Master.ino.eightanaloginputs.hex");
            setAvrDevice("atmega328p");
            break;
         default:
            throw new IllegalArgumentException("Invalid type " + type);
      }
      try {
         File file = File.createTempFile("ezHome-", ".hex");
         file.deleteOnExit();
         FileOutputStream fos = new FileOutputStream(file);
         try {
            int len;
            byte[] buf = new byte[1024];
            while ((len = is.read(buf)) != -1) {
               fos.write(buf, 0, len);
            }
         } finally {
            fos.close();
         }
         setHexFile(file);
      } finally {
         is.close();
      }

   }

   public UploadResult upload() throws IOException, InterruptedException {
      ArrayList<String> parameters = new ArrayList<>();
      parameters.add("avrdude");
      if (getConfigFile() != null) {
         parameters.add("-C");
         parameters.add(getConfigFile().getAbsolutePath());
      }
      // TODO sets parameters according to configurations
      parameters.add("-p" + getAvrDevice());
      parameters.add("-c" + programmer);
      parameters.add("-P" + getPort());
      parameters.add("-b" + getBaudRate());
      parameters.add("-D");
      parameters.add("-Uflash:w:" + getHexFile().getAbsolutePath() + ":i");
      parameters.add("-q");

      try {
         DeviceManager.getInstance().pause();
         Process p = Runtime.getRuntime().exec(parameters.toArray(new String[parameters.size()]));

         StringBuilder sb = new StringBuilder();
         try (InputStream es = p.getErrorStream()) {
            int len;
            byte[] buf = new byte[1024];
            while ((len = es.read(buf)) != -1) {
               sb.append(new String(buf, 0, len));
            }
         }
         return new UploadResult(sb.toString(), p.waitFor());
      } finally {
         DeviceManager.getInstance().resume();
      }
   }

   public class UploadResult {

      private final String output;
      private final int exitStatus;

      public UploadResult(String output, int exitStatus) {
         this.output = output;
         this.exitStatus = exitStatus;
      }

      public String getOutput() {
         return output;
      }

      public int getExitStatus() {
         return exitStatus;
      }

   }

   public static void main(String[] args) throws IOException, InterruptedException {
      FirmwareUploader f = new FirmwareUploader();
//      f.setConfigFile(new File("/home/cristofer/git/ezhome/arduino/conf/avrdude.conf"));
//      f.setHexFile(new File("/home/cristofer/git/ezhome/arduino/Master/Master.ino.standard.hex"));
//      f.setHexFile(new File());
//      InputStream is = FirmwareUploader.class.getResourceAsStream("/br/com/ezhome/device/hex/Master.ino.standard.hex");
//      try {
//         File file = File.createTempFile("ezHome-", ".hex");
//         file.deleteOnExit();
//         FileOutputStream fos = new FileOutputStream(file);
//         try {
//            int len;
//            byte[] buf = new byte[1024];
//            while ((len = is.read(buf)) != -1) {
//               fos.write(buf, 0, len);
//            }
//         } finally {
//            fos.close();
//         }
//         f.setHexFile(file);
//      } finally {
//         is.close();
//      }
//      f.setAvrDevice("atmega328p");
      f.preloadConfig(ARDUINO_UNO);
      f.setPort("/dev/ttyACM0");

      System.out.println(f.upload().getOutput());
   }
}
