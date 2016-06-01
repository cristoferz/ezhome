package br.com.ezhome.scheduler;

import br.com.ezhome.Controller;
import java.util.Timer;
import java.util.logging.Level;

/**
 *
 * @author cristofer
 */
public class GlobalScheduler {

   private Timer timer;

   public GlobalScheduler() {
      this.timer = new Timer();
   }
   
   public void start() {
      Controller.getLogger().log(Level.INFO, "Starting schedulers");
      // Port monitor
      this.timer.schedule(new PortConnectorSchedulerTask(), 0/* Starts immediatelly*/, 5*1000/* 30s */);
   }
}
