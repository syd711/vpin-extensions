package de.ggs.vpin.extensions.resources;

import de.ggs.vpin.extensions.services.GameService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("pinpup")
public class PinUpPlayerResource {
  private final static Logger LOG = LoggerFactory.getLogger(PinUpPlayerResource.class);

  @Autowired
  private GameService gameService;

  @PostMapping(value = "/tablestart")
  public String start(@RequestParam("table") String table) {
    LOG.info("Pinpup start: " + table);
    gameService.initGame(table);
    return table;
  }

  @PostMapping(value = "/tableexit")
  public String exit(@RequestParam("table") String table) {
    LOG.info("Pinpup exit: " + table);
    gameService.exitGame();
    return table;
  }
}
