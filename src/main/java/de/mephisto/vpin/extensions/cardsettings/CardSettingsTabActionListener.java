package de.mephisto.vpin.extensions.cardsettings;

import de.mephisto.vpin.GameInfo;
import de.mephisto.vpin.VPinService;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CardSettingsTabActionListener implements ActionListener {
  private final static org.slf4j.Logger LOG = LoggerFactory.getLogger(CardSettingsTabActionListener.class);

  private final CardSettingsTab cardSettingsTab;

  public CardSettingsTabActionListener(CardSettingsTab cardSettingsTab, VPinService service) {
    this.cardSettingsTab = cardSettingsTab;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    String cmd = e.getActionCommand();
    if (cmd.equals("generateCard")) {
      cardSettingsTab.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      GameInfo sampleGame = cardSettingsTab.getSampleGame();
      this.cardSettingsTab.generateSampleCard(sampleGame);
      cardSettingsTab.setCursor(null);
    }
    else if (cmd.equals("showCard")) {
      this.cardSettingsTab.showGeneratedCard();
    }
    else if (cmd.equals("generateAllCards")) {
      this.cardSettingsTab.generateAllCards();
    }
  }
}
