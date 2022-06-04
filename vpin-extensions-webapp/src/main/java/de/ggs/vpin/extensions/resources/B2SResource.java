package de.ggs.vpin.extensions.resources;

import de.ggs.vpin.extensions.services.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("b2s")
public class B2SResource {

  @Autowired
  private GameService gameService;

  @GetMapping(value = "/cmd")
  public B2SEvent cmd(@RequestParam("type") String type, @RequestParam("number") int number, @RequestParam("value") int value) {
    B2SEvent event = new B2SEvent(type, number, value != 0);
    gameService.notifyB2SEvent(event);
    return event;
  }
}
