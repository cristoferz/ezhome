/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.ezhome.device;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class TwoWaySerialComm {

   void connect(String portName) throws Exception {
      CommPortIdentifier portIdentifier = CommPortIdentifier
              .getPortIdentifier(portName);
      if (portIdentifier.isCurrentlyOwned()) {
         System.out.println("Error: Port is currently in use");
      } else {
         int timeout = 2000;
         CommPort commPort = portIdentifier.open(this.getClass().getName(), timeout);

         if (commPort instanceof SerialPort) {
            SerialPort serialPort = (SerialPort) commPort;
            serialPort.setSerialPortParams(115200,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);

            InputStream in = serialPort.getInputStream();
            OutputStream out = serialPort.getOutputStream();

            (new Thread(new SerialReader(in))).start();
            (new Thread(new SerialWriter(out))).start();

         } else {
            System.out.println("Error: Only serial ports are handled by this example.");
         }
      }
   }

   public static class SerialReader implements Runnable {

      InputStream in;

      public SerialReader(InputStream in) {
         this.in = in;
      }

      public void run() {
         byte[] buffer = new byte[1024];
         int len = -1;
         try {
            while ((len = this.in.read(buffer)) > -1) {
               System.out.print(new String(buffer, 0, len));
            }
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
   }

   public static class SerialWriter implements Runnable {

      OutputStream out;

      public SerialWriter(OutputStream out) {
         this.out = out;
      }

      public void run() {
         try {
            int c = 0;
            while ((c = System.in.read()) > -1) {
//               if((char)c == '\n') {
//                  this.out.write('\r');
//               }
               this.out.write(c);
            }
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
   }

   public static void main(String[] args) {
      try {
         (new TwoWaySerialComm()).connect("/dev/ttyACM0");
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
}