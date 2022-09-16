package de.mephisto.vpin.popper.overlay;

import de.mephisto.vpin.popper.overlay.generator.OverlayGenerator;
import de.mephisto.vpin.popper.overlay.util.Config;
import de.mephisto.vpin.util.KeyChecker;
import org.jnativehook.GlobalScreen;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import javax.swing.*;
import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OverlayWindow extends JFrame implements NativeKeyListener {

  private final KeyChecker keyChecker;

  private boolean visible = false;


  public OverlayWindow() throws Exception {
    String hotkey = Config.getOverlayGeneratorConfig().getString("overlay.hotkey");
    keyChecker = new KeyChecker(hotkey);

    UIManager.setLookAndFeel(
        UIManager.getCrossPlatformLookAndFeelClassName());

    this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
    setLocation(0, 0);
    setUndecorated(true);
    setAlwaysOnTop(true);

    // no layout manager
    setLayout(new BorderLayout());
    ImageIcon icon = new ImageIcon(OverlayGenerator.GENERATED_OVERLAY_FILE.getAbsolutePath());
    this.add(new JLabel(icon), BorderLayout.CENTER);

    Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
    logger.setLevel(Level.OFF);
    logger.setUseParentHandlers(false);

    GlobalScreen.registerNativeHook();
    GlobalScreen.addNativeKeyListener(this);
  }

  public static void main(String[] args) throws Exception {
    new OverlayWindow();
  }

  @Override
  public void nativeKeyTyped(NativeKeyEvent nativeKeyEvent) {
  }

  @Override
  public void nativeKeyPressed(NativeKeyEvent nativeKeyEvent) {
    if (keyChecker.matches(nativeKeyEvent)) {
      this.visible = !visible;
      this.setVisible(this.visible);
      EventQueue.invokeLater(new Runnable() {
        @Override
        public void run() {
          toFront();
        }
      });
    }
  }

  @Override
  public void nativeKeyReleased(NativeKeyEvent nativeKeyEvent) {

  }
}
