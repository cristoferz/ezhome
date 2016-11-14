package br.com.ezhome.device.program;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import br.com.ezhome.lib.program.ByteArrayBuilder;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author cristofer
 */
public class ByteArrayBuilderTest {
   private static ByteArrayBuilder arrayBuilder;
   
   public ByteArrayBuilderTest() {
      
   }
   
   @BeforeClass
   public static void setUpClass() {
      arrayBuilder = new ByteArrayBuilder();
      
   }
   
   @AfterClass
   public static void tearDownClass() {
   }
   
   @Before
   public void setUp() {
   }
   
   @After
   public void tearDown() {
   }

    // TODO add test methods here.
   // The methods must be annotated with annotation @Test. For example:
   //
   // @Test
   // public void hello() {}
   @Test
   public void appendBitTest() {
      arrayBuilder.appendBit(false);
      arrayBuilder.appendBit(false);
      Assert.assertEquals("Byte[0] esperado", 0x00, arrayBuilder.getByte(0));
      arrayBuilder.appendBit(false);
      arrayBuilder.appendBit(false);
      arrayBuilder.appendBit(true);
      Assert.assertEquals("Byte[0] esperado", 0x08, arrayBuilder.getByte(0));
      arrayBuilder.appendBit(false);
      arrayBuilder.appendBit(true);
      arrayBuilder.appendBit(false);
      Assert.assertEquals("Byte[0] esperado", 0x0A, arrayBuilder.getByte(0));
      
      // from now, have to create another byte
      arrayBuilder.appendBit(true);
      Assert.assertEquals("Quantidade de bytes no builder", 2, arrayBuilder.size());
      System.out.println(arrayBuilder.byteToBinaryString(arrayBuilder.getByte(1)));
      Assert.assertEquals("Byte[1] esperado", (byte)0x80, arrayBuilder.getByte(1));
      
   }
   
   @Test
   public void appendByteTest() {
      arrayBuilder.clear();
      arrayBuilder.append((byte)0x88, 8, true, true);
      Assert.assertEquals("Byte[0] esperado", (byte)0x88, arrayBuilder.getByte(0));
      
      arrayBuilder.clear();
      arrayBuilder.append((byte)0x52, 8, true, true);
      Assert.assertEquals("Byte[0] esperado", (byte)0x52, arrayBuilder.getByte(0));
      
      arrayBuilder.clear();
      arrayBuilder.appendBit(false);
      arrayBuilder.appendBit(true);
      arrayBuilder.appendBit(true);
      arrayBuilder.append((byte)0x50, 5, true, true);
      Assert.assertEquals("Byte[0] esperado", (byte)0x6A, arrayBuilder.getByte(0));
      Assert.assertEquals("Quantidade de Bytes no buffer", 1, arrayBuilder.size());
      
      arrayBuilder.clear();
      arrayBuilder.appendBit(false);
      arrayBuilder.appendBit(true);
      arrayBuilder.appendBit(true);
      arrayBuilder.append((byte)0x54, 7, true, true);
      Assert.assertEquals("Byte[0] esperado", (byte)0x6A, arrayBuilder.getByte(0));
      Assert.assertEquals("Quantidade de Bytes no buffer", 2, arrayBuilder.size());
      Assert.assertEquals("Byte[1] esperado", (byte)0x80, arrayBuilder.getByte(1));
   }
   
   @Test
   public void appendByteTestFromRight() {
      arrayBuilder.clear();
      arrayBuilder.append((byte)0x05, 3, false, true);
      Assert.assertEquals("Byte[0] esperado", (byte)0xA0, arrayBuilder.getByte(0));
      
      arrayBuilder.clear();
      arrayBuilder.append((byte)0x52, 8, false, true);
      Assert.assertEquals("Byte[0] esperado", (byte)0x52, arrayBuilder.getByte(0));
//      
      arrayBuilder.clear();
      arrayBuilder.appendBit(false);
      arrayBuilder.appendBit(true);
      arrayBuilder.appendBit(true);
      arrayBuilder.append((byte)0x05, 4, false, true);
      Assert.assertEquals("Byte[0] esperado", (byte)0x6A, arrayBuilder.getByte(0));
      Assert.assertEquals("Quantidade de Bytes no buffer", 1, arrayBuilder.size());

   }
}
