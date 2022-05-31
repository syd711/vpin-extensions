package de.ggs.vpin.extensions.resources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("pinpup")
public class PinUpPlayerResource {
  private final static Logger LOG = LoggerFactory.getLogger(PinUpPlayerResource.class);

  @PostMapping(value = "/tablestart")
  public String start(@RequestParam("table") String table) {
    LOG.info("Pinpup Starting: " + table);
    return table;
  }

  @PostMapping(value = "/tableexit")
  public String exit(@RequestParam("table") String table) {
    LOG.info("Pinpup Starting: " + table);
    return table;
  }
}
