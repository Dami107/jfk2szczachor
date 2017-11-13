package pl.edu.wat.wcy.jfk;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pl.edu.wat.wcy.jfk.controller.ApplicationController;

public class ApplicationGUI extends Application {

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("ApplicationGUI.fxml"));
    Parent root = loader.load();

    Scene scene = new Scene(root, 736, 500);

    primaryStage.setTitle("[JFK] Mateusz Szcząchor");
    primaryStage.setResizable(false);

    primaryStage.setScene(scene);
    primaryStage.show();

    ApplicationController controller = loader.getController();
    controller.setStage(primaryStage);
  }
}
