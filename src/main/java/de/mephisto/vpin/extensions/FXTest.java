package de.mephisto.vpin.extensions;

import de.mephisto.vpin.extensions.resources.ResourceLoader;
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

public class FXTest extends Application  {
  private Stage stage;

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    this.stage = primaryStage;
    Screen screen = Screen.getPrimary();

    Rectangle2D bounds = screen.getVisualBounds();
    stage.setX(bounds.getMinX());
    stage.setY(bounds.getMinY());
    Platform.setImplicitExit(true);


    Image image = new Image(ResourceLoader.class.getResourceAsStream("splash.jpg"));
    ImageView imageView = new ImageView(image);
    imageView.setPreserveRatio(true);

    Group root = new Group(imageView);
    final Scene scene = new Scene(root, screen.getVisualBounds().getWidth(), screen.getVisualBounds().getHeight(), true, SceneAntialiasing.BALANCED);

    stage.setScene(scene);
    stage.setFullScreen(false);
    stage.setAlwaysOnTop(true);
    stage.setX(300);
    stage.setWidth(700);
    stage.setHeight(450);
    stage.setY(300);
    stage.setTitle("Highscore Overlay");
    stage.getIcons().add(new Image(ResourceLoader.class.getResourceAsStream("logo.png")));
    stage.show();
  }
}
