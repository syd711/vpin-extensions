package de.ggs.vpin.extensions.pinup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("pinpup")
public class PinUpPlayerResource {
  private final static Logger LOG = LoggerFactory.getLogger(PinUpPlayerResource.class);

  @GetMapping(value = "/start")
  public String start(@RequestParam("game") String game) {
    LOG.info("Pinpup Starting: " + game);
    return game;
  }

  @GetMapping(value = "/end")
  public String end(@RequestParam("game") String game) {
    LOG.info("Pinpup Starting: " + game);
    return game;
  }
}
