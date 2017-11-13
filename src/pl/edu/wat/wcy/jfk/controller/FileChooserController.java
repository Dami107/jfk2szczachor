package pl.edu.wat.wcy.jfk.controller;

import javafx.stage.FileChooser;

public class FileChooserController {

  static FileChooser getChooser() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Wybierz plik .jar");
    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archiwum Java (.jar)", "*.jar"));

    return fileChooser;
  }
}
