/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.ezhome.lib.device;

import java.io.IOException;

/**
 *
 * @author cristofer
 */
public abstract class Device {

   /**
    * Default command timeout 2s
    */
   public static final int COMMAND_TIMEOUT = 2000;

   public abstract String sendCommand(String command, int timeout) throws IOException;

   public abstract void write(byte[] value) throws IOException;

   /**
    * Send a command to device and waits for response until default
    * COMMAND_TIMEOUT expires
    *
    * @param command command to be send
    * @return device response or null on COMMAND_TIMEOUT
    * @throws IOException if a connection errors occur
    */
   public String sendCommand(String command) throws IOException {
      return sendCommand(command, COMMAND_TIMEOUT);
   }
}
