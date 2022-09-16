package de.mephisto.vpin.popper.overlay.util;

import de.mephisto.vpin.util.PropertiesStore;

/**
 * Utility for accessing the different config files.
 */
public class Config {
  private final static String GENERATOR_CONFIG_FILENAME = "overlay-generator.properties";
  private final static String CARD_CONFIG_FILENAME = "card-generator.properties";
  private final static String COMMAND_CONFIG_FILENAME = "commands.properties";

  private static PropertiesStore generatorConfig;
  private static PropertiesStore cardConfig;
  private static PropertiesStore commandConfig;

  public static PropertiesStore getCardGeneratorConfig() {
    if (cardConfig == null) {
      cardConfig = PropertiesStore.create(CARD_CONFIG_FILENAME);
    }
    return cardConfig;
  }

  public static PropertiesStore getOverlayGeneratorConfig() {
    if (generatorConfig == null) {
      generatorConfig = PropertiesStore.create(GENERATOR_CONFIG_FILENAME);
    }
    return generatorConfig;
  }

  public static PropertiesStore getCommandConfig() {
    if (commandConfig == null) {
      commandConfig = PropertiesStore.create(COMMAND_CONFIG_FILENAME);
    }
    return commandConfig;
  }
}
