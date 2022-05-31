package de.ggs.vpin.extensions.util;

public class CommandResultParser {

  public static String extractStandardKeyValue(String output) throws Exception {
    String result = output;
    result = result.replace("\n", "").replace("\r", "").trim();

    String[] value = result.split("    ");
    String[] values = value[3].split(" ");
    String path = values[0];
    return path.replaceAll("\"","");
  }

  public static String extractRegistryValue(String output) throws Exception {
    String result = output;
    result = result.replace("\n", "").replace("\r", "").trim();

    String[] s = result.split("    ");
    return s[3];
  }
}
