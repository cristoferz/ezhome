/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.ezhome.jwt;

import org.json.JSONObject;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author cristofer
 */
public class JWTTest {
   
   private static byte[] secret;
   
   public JWTTest() {
   }
   
   @BeforeClass
   public static void setUpClass() {
      secret = "Test".getBytes();
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

   /**
    * Test of getNotBeforeMillis method, of class JWT.
    */
   @Test
   public void testGetNotBeforeMillis() {
      int expResult = 500;
      JWT instance = new JWT(secret);
      instance.setNotBeforeMillis(expResult);
      int result = instance.getNotBeforeMillis();
      assertEquals(expResult, result);
   }

   /**
    * Test of setNotBeforeMillis method, of class JWT.
    */
   @Test
   public void testSetNotBeforeMillis() {
      int notBeforeMillis = 6545;
      JWT instance = new JWT(secret);
      instance.setNotBeforeMillis(notBeforeMillis);
      assertEquals(notBeforeMillis, instance.getNotBeforeMillis());
   }

   /**
    * Test of getValidUntilMillis method, of class JWT.
    */
   @Test
   public void testGetValidUntilMillis() {
      int expResult = 12345;
      JWT instance = new JWT(secret);
      instance.setValidUntilMillis(expResult);
      int result = instance.getValidUntilMillis();
      assertEquals(expResult, result);
   }

   /**
    * Test of setValidUntilMillis method, of class JWT.
    */
   @Test
   public void testSetValidUntilMillis() {
      int validUntilMillis = 54312;
      JWT instance = new JWT(secret);
      instance.setValidUntilMillis(validUntilMillis);
      assertEquals(validUntilMillis, instance.getValidUntilMillis());
   }

   /**
    * Test of sign method, of class JWT.
    */
   @Test
   public void testSign() throws Exception {
      JSONObject data = new JSONObject();
      data.put("test", "OK");
      JWT instance = new JWT(secret);
      String result = instance.sign(data);
      assertNotNull(result);
   }

   /**
    * Test of validate method, of class JWT.
    */
   @Test
   public void testValidate() throws Exception {
      JSONObject expResult = new JSONObject();
      expResult.put("test", "OK");
      JWT instance = new JWT(secret);
      String token = instance.sign(expResult);
      
      JSONObject result = instance.validate(token);
      assertEquals(expResult.toString(), result.getJSONObject("data").toString());
      
      // Testing not before
      instance = new JWT(secret);
      instance.setNotBeforeMillis(5000);
      token = instance.sign(expResult);
      
      try {
         result = instance.validate(token);
         fail("Token has to be invalid: not before");
      } catch (JWTValidityException ex) {
         assertTrue(true);
      }
      
      // Testing not after
      instance = new JWT(secret);
      instance.setValidUntilMillis(100);
      token = instance.sign(expResult);
      
      Thread.sleep(200);
      try {
         result = instance.validate(token);
         fail("Token has to be invalid: not after");
      } catch (JWTValidityException ex) {
         assertTrue(true);
      }
      
      // Testing secret
      instance = new JWT(secret);
      token = instance.sign(expResult);
      
      instance = new JWT("INVALID SECRET".getBytes());
      
      try {
         result = instance.validate(token);
         fail("Token has to be invalid: invalid signature");
      } catch (JWTSignatureException ex) {
         assertTrue(true);
      }
      
   }
   
}
