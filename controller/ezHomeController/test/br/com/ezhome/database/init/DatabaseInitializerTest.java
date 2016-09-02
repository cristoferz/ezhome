/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.ezhome.database.init;

import br.com.ezhome.database.DatabaseConnector;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
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
public class DatabaseInitializerTest {
   
   private static DatabaseInitializer initializer;
   
   public DatabaseInitializerTest() {
   }
   
   @BeforeClass
   public static void setUpClass() throws SQLException, IOException {
      initializer = new DatabaseInitializer(DatabaseConnector.getInstance().connect());
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
    * Test of getConnection method, of class DatabaseInitializer.
    */
   @Test
   public void testGetConnection() {
      System.out.println("getConnection");
      DatabaseInitializer instance = null;
      Connection expResult = null;
      Connection result = instance.getConnection();
      assertEquals(expResult, result);
      // TODO review the generated test code and remove the default call to fail.
      fail("The test case is a prototype.");
   }

   /**
    * Test of repair method, of class DatabaseInitializer.
    */
   @Test
   public void testRepair() throws Exception {
      System.out.println("repair");
      DatabaseInitializer instance = null;
      instance.repair();
      // TODO review the generated test code and remove the default call to fail.
      fail("The test case is a prototype.");
   }

   
}
