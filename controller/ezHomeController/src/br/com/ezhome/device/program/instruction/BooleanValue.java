/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.ezhome.device.program.instruction;

import br.com.ezhome.device.program.ProgramAddress;
import br.com.ezhome.device.program.ProgramBuilder;
import br.com.ezhome.device.program.ProgramSeriesBuilder;
import org.json.JSONObject;

/**
 *
 * @author cristofer
 */
public class BooleanValue {

   private ProgramBuilder builder;
   private ProgramAddress address;

   private boolean constant;
   private boolean value;

   public BooleanValue(ProgramBuilder builder, ProgramAddress address) {
      this.builder = builder;
      this.address = address;
      this.constant = false;
   }

   public BooleanValue(ProgramBuilder builder, boolean value) {
      this.builder = builder;
      this.constant = true;
      this.value = value;
   }

   public int getDataSize() {
      if (constant) {
         return 2;
      } else {
         return 1 + builder.getBitsPerBoolAddress();
      }
   }
   
   public void appendBytes(ProgramSeriesBuilder builder) {
      builder.getArrayBuilder().appendBit(!constant);
      if (constant) {
         builder.getArrayBuilder().appendBit(value);
      } else {
         builder.getArrayBuilder().append(address.getAddress(), this.builder.getBitsPerBoolAddress(), false, true);
      }
   }
   
   public static BooleanValue fromJSON(ProgramBuilder builder, JSONObject json, String name) {
      if (json.get(name) instanceof Integer) {
         return new BooleanValue(builder, builder.getAddress(json.getInt(name)));
      } else {
         return new BooleanValue(builder, json.getBoolean(name));
      }
   }

   public ProgramBuilder getBuilder() {
      return builder;
   }
}
