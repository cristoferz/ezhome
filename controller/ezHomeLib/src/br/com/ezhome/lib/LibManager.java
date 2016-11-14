package br.com.ezhome.lib;

import br.com.ezhome.lib.compiler.EzHomeCompilerDeviceModel;
import br.com.ezhome.lib.config.ConfigFile;
import br.com.ezhome.lib.logger.EzHomeLogger;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.zip.ZipEntry;

/**
 *
 * @author cristofer
 */
public class LibManager {

   private static LibManager instance;

   public static LibManager getInstance() {
      if (instance == null) {
         instance = new LibManager();
      }
      return instance;
   }
   private ArrayList<ModulePrototype> modules;
   private ArrayList<EzHomeCompilerDeviceModel> deviceModels;

   private LibManager() {
      modules = new ArrayList<>();
      deviceModels = new ArrayList<>();
   }

   public void registerModule(ModulePrototype module) {
      EzHomeLogger.getLogger().log(Level.INFO, "- {0}: {1}", new Object[]{module.getDescription(), module.getClasse().getName()});
      modules.add(module);
   }
   
   public void registerDeviceModel(EzHomeCompilerDeviceModel deviceModel, String description) {
      EzHomeLogger.getLogger().log(Level.INFO, "Registering device model: {0}: {1}", new Object[] { description, deviceModel.getClass().getName() });
      deviceModels.add(deviceModel);
   }

   public void loadJar(File jar) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
      JarFile jarFile = new JarFile(jar);
      ZipEntry entry = jarFile.getEntry("META-INF/ezHome.properties");
      if (entry == null) {
         throw new IllegalArgumentException("Invalid Jar for libs");
      }
      try (InputStream is = jarFile.getInputStream(entry)) {
         Properties p = new Properties();
         p.load(is);
         EzHomeLogger.getLogger().log(Level.INFO, "Installing Jar: {0}", jar.getAbsolutePath());
         EzHomeLogger.getLogger().log(Level.INFO, "Register class: {0}", p.getProperty("LibRegister"));

         try {
            URL url = jar.toURI().toURL();

            URLClassLoader classLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
            Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            method.setAccessible(true);
            method.invoke(classLoader, url);
         } catch (MalformedURLException | NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            EzHomeLogger.getLogger().log(Level.SEVERE, "Error loading jar", ex);
         }

         Object registerObj = Class.forName(p.getProperty("LibRegister")).newInstance();
         if (!(registerObj instanceof LibRegister)) {
            EzHomeLogger.getLogger().log(Level.SEVERE, "LibRegister must be instance of br.com.ezhome.lib.LigRegister");
            return;
         }
         LibRegister register = (LibRegister) registerObj;
         register.install();
      }
   }
   
   public void install() throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
      File libDir = ConfigFile.getInstance().getLibDir();
      EzHomeLogger.getLogger().log(Level.INFO, "Loading libs from {0}", libDir.getAbsolutePath());
      if (!libDir.isDirectory()) {
         throw new IllegalArgumentException("libDir "+libDir.getAbsolutePath()+" must be a directory.");
      }

      // Loads Libs on libDir
      for (File listFile : libDir.listFiles()) {
         if (listFile.getName().toLowerCase().endsWith(".jar") &&
                 !listFile.isDirectory()) {
            try {
               loadJar(listFile);
            } catch(IllegalArgumentException ex) {
               EzHomeLogger.getLogger().log(Level.WARNING, "Invalid lib file: {0} It dont contains a valid META-INF/ezHome.properties", listFile.getAbsolutePath());
            }
         }
      }
      
   }

   public ArrayList<ModulePrototype> getModules() {
      return modules;
   }
   
   public EzHomeCompilerDeviceModel getDeviceModel(String className) throws ClassNotFoundException {
      for (EzHomeCompilerDeviceModel deviceModel : deviceModels) {
         if (deviceModel.getClass().getName().equals(className)) {
            return deviceModel;
         }
      }
      throw new ClassNotFoundException("Class "+className+" is not registered as deviceModel");
   }

   public static void main(String[] args) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
      EzHomeLogger.init();
      LibManager.getInstance().loadJar(new File("/home/cristofer/git/ezhome/controller/ezHomeStandardLib/dist/ezHomeStandardLib.jar"));
   }
}

