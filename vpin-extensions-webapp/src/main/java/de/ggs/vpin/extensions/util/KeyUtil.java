package de.ggs.vpin.extensions.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.Locale;

import static java.awt.event.KeyEvent.*;

public class KeyUtil {
  private final static Logger LOG = LoggerFactory.getLogger(KeyUtil.class);

  public static void pressKey(String key, int delay) {
    try {
      LOG.info("Pressing key '" + key + "'");
      int keyEvent = getKeyEvent(key);
      System.setProperty("java.awt.headless", "false");
      Robot r = new Robot();
      r.keyPress(keyEvent);
      r.delay(delay);
      r.keyRelease(keyEvent);
    } catch (Exception ex) {
      LOG.error("Failed to send key event: " + ex.getMessage());
    }
  }

  private static int getKeyEvent(String character) {
    character = character.toLowerCase(Locale.ROOT);

    switch (character) {
      case "a":
        return VK_A;
      case "b":
        return VK_B;
      case "c":
        return VK_C;
      case "d":
        return VK_D;
      case "e":
        return VK_E;
      case "f":
        return VK_F;
      case "g":
        return VK_G;
      case "h":
        return VK_H;
      case "i":
        return VK_I;
      case "j":
        return VK_J;
      case "k":
        return VK_K;
      case "l":
        return VK_L;
      case "m":
        return VK_M;
      case "n":
        return VK_N;
      case "o":
        return VK_O;
      case "p":
        return VK_P;
      case "q":
        return VK_Q;
      case "r":
        return VK_R;
      case "s":
        return VK_S;
      case "t":
        return VK_T;
      case "u":
        return VK_U;
      case "v":
        return VK_V;
      case "w":
        return VK_W;
      case "x":
        return VK_X;
      case "y":
        return VK_Y;
      case "z":
        return VK_Z;
      case "`":
        return VK_BACK_QUOTE;
      case "0":
        return VK_0;
      case "1":
        return VK_1;
      case "2":
        return VK_2;
      case "3":
        return VK_3;
      case "4":
        return VK_4;
      case "5":
        return VK_5;
      case "6":
        return VK_6;
      case "7":
        return VK_7;
      case "8":
        return VK_8;
      case "9":
        return VK_9;
      case "-":
        return VK_MINUS;
      case "=":
        return VK_EQUALS;
      case "!":
        return VK_EXCLAMATION_MARK;
      case "@":
        return VK_AT;
      case "#":
        return VK_NUMBER_SIGN;
      case "$":
        return VK_DOLLAR;
      case "^":
        return VK_CIRCUMFLEX;
      case "&":
        return VK_AMPERSAND;
      case "*":
        return VK_ASTERISK;
      case "esc":
        return VK_ESCAPE;
      case "(":
        return VK_LEFT_PARENTHESIS;
      case ")":
        return VK_RIGHT_PARENTHESIS;
      case "_":
        return VK_UNDERSCORE;
      case "+":
        return VK_PLUS;
      case "\t":
        return VK_TAB;
      case "\n":
        return VK_ENTER;
      case "[":
        return VK_OPEN_BRACKET;
      case "]":
        return VK_CLOSE_BRACKET;
      case "\\":
        return VK_BACK_SLASH;
      case ";":
        return VK_SEMICOLON;
      case ":":
        return VK_COLON;
      case ",":
        return VK_COMMA;
      case ".":
        return VK_PERIOD;
      case "/":
        return VK_SLASH;
      case " ":
        return VK_SPACE;
      default:
        throw new IllegalArgumentException("Cannot type character " + character);
    }
  }
}
