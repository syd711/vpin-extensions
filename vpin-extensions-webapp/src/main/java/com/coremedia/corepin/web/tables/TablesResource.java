package com.coremedia.corepin.web.tables;

import com.coremedia.corepin.web.PinballInfo;
import com.coremedia.corepin.web.PinballService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
public class TablesResource implements InitializingBean, ApplicationContextAware {
  private final static Logger LOG = LoggerFactory.getLogger(TablesResource.class);

  @Autowired
  private PinballService pinballService;

  @Value("${highscore.refresh.time.seconds}")
  private long refreshInterval;

  private static PinballInfo info;
  private ApplicationContext applicationContext;

  @GetMapping(value = "/info", produces = MediaType.APPLICATION_JSON_VALUE)
  public PinballInfo info() {
    LOG.info("Info called");
    return getPinballInfo();
  }

  @PostMapping(value = "/game", produces = MediaType.APPLICATION_JSON_VALUE)
  public PinballInfo game(@RequestParam("game") String game, @RequestParam("rom") String rom) {
    LOG.info("Game " + game + " called [" + rom + "]");

//    new Thread() {
//      public void run() {
//        try {
//          System.setProperty("java.awt.headless", "false");
//          Thread.sleep(2000);
//          Robot robot = new Robot();
//          // Simulate a key press
//          robot.keyPress(KeyEvent.VK_3);
//          robot.keyRelease(KeyEvent.VK_3);
//        }
//        catch (Exception e) {
//          e.printStackTrace();
//        }
//      }
//    }.start();
    return getPinballInfo();
  }

  @GetMapping(value = "/b2s")
  public String b2s(@RequestParam("type") String type, @RequestParam("number") String number, @RequestParam("value") String value) {
    LOG.info("B2S command: " + type + ", " + number + ", " + value);
    return type;
  }

  @GetMapping(value = "/data", produces = MediaType.APPLICATION_JSON_VALUE)
  public PinballInfo data() {
    return buildInfo();
  }

  @PostMapping(value= "/setTable", produces = MediaType.APPLICATION_JSON_VALUE)
  public void setTable(@RequestParam("rom") String rom) {
    pinballService.setTableOfTheMonth(rom);
  }

  private synchronized PinballInfo getPinballInfo() {
    return info;
  }

  @Override
  public void afterPropertiesSet() {
//    ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
//    LOG.info("Initializing refresh thread with interval or {} seconds.", refreshInterval);
//        executorService.scheduleAtFixedRate(() -> {
//          TablesResource.info = buildInfo();
//        }, 0, refreshInterval, TimeUnit.SECONDS);
  }

  private synchronized PinballInfo buildInfo() {
    long start = System.currentTimeMillis();
    List<TableInfo> tables = pinballService.getTables();
    PinballInfo pinballInfo = new PinballInfo(tables);
    pinballInfo.setModificationDate(new Date().toString());

    String tableOfTheMonthRom = pinballService.getTableOfTheMonthRom();
    if(tableOfTheMonthRom != null ) {
      for (TableInfo table : tables) {
        if(table.getRomName() != null) {
          if(table.getRomName().equalsIgnoreCase(tableOfTheMonthRom)) {
            pinballInfo.setPinballOfTheMonth(table);
            break;
          }
        }
      }
    }

    long duration = System.currentTimeMillis() - start;
    LOG.info("===============================================================================================");
    LOG.info("Scan finished: " + pinballInfo.getTables().size() + " tables scanned, took " + duration + " ms.");
    LOG.info("===============================================================================================");
    return pinballInfo;
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }
}
