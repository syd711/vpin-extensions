package de.ggs.vpin.extensions.resources;

import de.ggs.vpin.extensions.services.GameService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("table")
public class TableScriptsResource {
  private final static Logger LOG = LoggerFactory.getLogger(TableScriptsResource.class);

  @Autowired
  private GameService gameService;


  @GetMapping(value = "/init")
  public String start() {
    LOG.info("Table init called ");
    return "";
  }
}
