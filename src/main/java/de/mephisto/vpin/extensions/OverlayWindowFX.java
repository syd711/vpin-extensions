package de.mephisto.vpin.extensions;

import de.mephisto.vpin.VPinService;
import de.mephisto.vpin.extensions.generator.OverlayGenerator;
import de.mephisto.vpin.extensions.generator.OverlayGraphics;
import de.mephisto.vpin.extensions.resources.ResourceLoader;
import de.mephisto.vpin.extensions.util.Config;
import de.mephisto.vpin.popper.PopperLaunchListener;
import de.mephisto.vpin.util.KeyChecker;
import de.mephisto.vpin.util.SystemInfo;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;
import org.jnativehook.GlobalScreen;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OverlayWindowFX extends Application implements NativeKeyListener, PopperLaunchListener {
  private final static org.slf4j.Logger LOG = LoggerFactory.getLogger(OverlayGraphics.class);

  private boolean visible = false;

  private boolean initialLaunchExecuted = false;

  public static OverlayWindowFX INSTANCE;

  private Stage stage;

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    this.stage = primaryStage;
    OverlayWindowFX.INSTANCE = this;
    String hotkey = Config.getOverlayGeneratorConfig().getString("overlay.hotkey");
    if (StringUtils.isEmpty(hotkey)) {
      LOG.error("No overlay hotkey defined! Define a key binding on the overlay configuration tab and restart the service.");
    }

    Platform.setImplicitExit(false);

    FileInputStream inputstream = new FileInputStream(OverlayGenerator.GENERATED_OVERLAY_FILE);
    Image image = new Image(inputstream);
    ImageView imageView = new ImageView(image);
    imageView.setPreserveRatio(true);

    Group root = new Group(imageView);
    Screen screen = Screen.getPrimary();
    final Scene scene = new Scene(root, screen.getVisualBounds().getWidth(), screen.getVisualBounds().getHeight(), true, SceneAntialiasing.BALANCED);

    Rectangle2D bounds = screen.getVisualBounds();
    stage.setX(bounds.getMinX());
    stage.setY(bounds.getMinY());

    stage.setScene(scene);
    stage.setFullScreenExitHint("");
    stage.setFullScreen(true);
    stage.setAlwaysOnTop(true);
    stage.setTitle("Highscore Overlay");
    stage.getIcons().add(new Image(ResourceLoader.class.getResourceAsStream("logo.png")));
    stage.setHeight(screen.getVisualBounds().getWidth());
    stage.setWidth(screen.getVisualBounds().getHeight());

    GlobalScreen.registerNativeHook();
    Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
    logger.setLevel(Level.OFF);
    logger.setUseParentHandlers(false);
    GlobalScreen.addNativeKeyListener(this);

    boolean pinUPRunning = SystemInfo.getInstance().isPinUPRunning();
    if(pinUPRunning) {
      popperLaunched();
    }
    else {
      LOG.info("Added VPin service popper status listener.");
      VPinService service = VPinService.create(true);
      service.addPopperLaunchListener(this);
    }
  }

  @Override
  public void nativeKeyTyped(NativeKeyEvent nativeKeyEvent) {

  }

  @Override
  public void nativeKeyPressed(NativeKeyEvent nativeKeyEvent) {
    String hotkey = Config.getOverlayGeneratorConfig().getString("overlay.hotkey");
    String killKey = Config.getServiceConfig().getString("killswitch.key");
    KeyChecker overlaykeyChecker = new KeyChecker(hotkey);
    if (overlaykeyChecker.matches(nativeKeyEvent) || this.visible) {
      toggleView();
    }

    KeyChecker killKeyChecker = new KeyChecker(killKey);
    if(killKeyChecker.matches(nativeKeyEvent)) {
      String keyText = NativeKeyEvent.getKeyText(nativeKeyEvent.getKeyCode());
      LOG.info("Kill switch key event '" + keyText + "' (" + nativeKeyEvent.getKeyCode() + ")");
      restartPopper();
    }
  }

  private void restartPopper() {
    LOG.info("Killswitch pressed, restarting popper.");
    SystemInfo.getInstance().restartPinUPPopper();
  }

  public void toggleView() {
    this.visible = !visible;
    Platform.runLater(() -> {
      LOG.info("Toggle show");
      if (this.visible) {
        stage.show();
      }
      else {
        stage.hide();
      }
    });
  }

  @Override
  public void nativeKeyReleased(NativeKeyEvent nativeKeyEvent) {

  }

  @Override
  public void popperLaunched() {
    LOG.info("Received Popper Launch Event");
    boolean launch = Config.getOverlayGeneratorConfig().getBoolean("overlay.launchOnStartup");
    if (launch && !initialLaunchExecuted) {
      initialLaunchExecuted = true;
      int delay = Config.getOverlayGeneratorConfig().getInt("overlay.launchDelay", 0);
      if (delay > 0) {
        try {
          Thread.sleep(delay * 1000L);
        } catch (InterruptedException e) {
          LOG.error("Failed to wait for delay: " + e.getMessage(), e);
        }
      }

      if(OverlayWindowFX.INSTANCE != null) {
        OverlayWindowFX.INSTANCE.toggleView();
      }
    }
  }
}
