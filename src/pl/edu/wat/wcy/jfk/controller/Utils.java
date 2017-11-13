package pl.edu.wat.wcy.jfk.controller;


import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class Utils {

  public static void showAlert(Alert.AlertType alertType, String message) {
    Alert alert = new Alert(alertType, message);
    alert.show();
  }

  public static boolean showConfirm(String message) {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.YES, ButtonType.NO);
    alert.showAndWait();

    if (alert.getResult() == ButtonType.YES) {
      return true;
    } else {
      return false;
    }
  }
}
