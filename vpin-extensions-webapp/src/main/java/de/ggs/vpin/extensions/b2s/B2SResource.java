package de.ggs.vpin.extensions.b2s;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("b2s")
public class B2SResource {
  private final static Logger LOG = LoggerFactory.getLogger(B2SResource.class);

  @GetMapping(value = "/cmd")
  public String cmd(@RequestParam("type") String type, @RequestParam("number") String number, @RequestParam("value") String value) {
    LOG.info("B2S command: " + type + ", " + number + ", " + value);
    return type;
  }
}
