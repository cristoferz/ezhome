package br.com.ezhome.lib;

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
import java.util.jar.JarFile;
import java.util.logging.Level;

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

   private ArrayList<ModuleClass> modules;

   private LibManager() {
      modules = new ArrayList<>();
   }

   public void registerModule(ModuleClass module) {
      EzHomeLogger.getLogger().log(Level.INFO, "- {0}: {1}", new Object[]{module.getDescription(), module.getClasse().getName()});
      modules.add(module);
   }

   public void loadJar(File jar) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
      JarFile jarFile = new JarFile(jar);
      try (InputStream is = jarFile.getInputStream(jarFile.getEntry("META-INF/ezHome.properties"))) {
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

   public static void main(String[] args) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
      EzHomeLogger.init();
      LibManager.getInstance().loadJar(new File("/home/cristofer/git/ezhome/controller/ezHomeStandardLib/dist/ezHomeStandardLib.jar"));
   }

   public ArrayList<ModuleClass> getModules() {
      return modules;
   }
}
