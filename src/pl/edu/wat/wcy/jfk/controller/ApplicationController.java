package pl.edu.wat.wcy.jfk.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ApplicationController {

  private Stage stage;

  private File file;

  private ClassPool classPool;

  @FXML
  private Button btnFileChoose;

  @FXML
  private Label lblChooseClass;

  @FXML
  private ComboBox<String> cbChooseClass;

  @FXML
  private Button btnEndEdit;

  @FXML
  private Label lblAddMethod;

  @FXML
  private Label lblMethodSignature;

  @FXML
  private TextField fldMethodSignature;

  @FXML
  private TextArea fldMethodBody;

  @FXML
  private Label lblMethodBody;

  @FXML
  private Button btnAddMethod;

  @FXML
  private Label lblInvokeMethod;

  @FXML
  private ComboBox<String> cbInvokeClass;

  @FXML
  private ComboBox<String> cbInvokeMethod;

  @FXML
  private TextField fldArguments;

  @FXML
  private Button btnExecute;

  @FXML
  private Label lblChangeHierarchy;

  @FXML
  private Label lblSuperclass;

  @FXML
  private ComboBox<String> cbSuperclass;

  @FXML
  private Button btnSuperclass;

  @FXML
  private void initialize() {
    classPool = ClassPool.getDefault();
    MethodInvoker.getInstance().setClassPool(classPool);
  }

  @FXML
  private void onBtnFileChooseClick(ActionEvent event) {
    FileChooser chooser = getChooser();
    file = chooser.showOpenDialog(stage);

    btnFileChoose.setText(file.getName());
    btnFileChoose.setDisable(true);
    lblChooseClass.setVisible(true);
    cbChooseClass.setVisible(true);


    loadFile();
    List<String> classNames = getClassNames();
    cbChooseClass.getItems().clear();
    cbChooseClass.getItems().addAll(classNames);
    cbInvokeClass.getItems().clear();
    cbInvokeClass.getItems().addAll(classNames);
  }

  @FXML
  private void onCbChooseClass(ActionEvent event) {
    if (cbChooseClass.getValue() == null) {
      return;
    }

    lblAddMethod.setVisible(true);
    btnEndEdit.setVisible(true);
    lblMethodSignature.setVisible(true);
    lblMethodBody.setVisible(true);
    fldMethodSignature.setVisible(true);
    fldMethodBody.setVisible(true);
    btnAddMethod.setVisible(true);

    lblChangeHierarchy.setVisible(true);
    lblSuperclass.setVisible(true);
    cbSuperclass.setVisible(true);
    btnSuperclass.setVisible(true);

    List<String> possibleClasses = getPossibleSuperclasses(cbChooseClass.getValue());
    cbSuperclass.getItems().clear();
    cbSuperclass.getItems().addAll(possibleClasses);

    try {
      CtClass ctClass = classPool.get(cbChooseClass.getValue());

      if (ctClass != null && ctClass.getSuperclass() != null) {
        cbSuperclass.setValue(ctClass.getSuperclass().getName());
      }
    } catch (NotFoundException e) {
      e.printStackTrace();
    }

  }

  @FXML
  private void onBtnAddMethodClick(ActionEvent event) {
    CtClass ctClass = null;
    try {
      ctClass = classPool.getCtClass(cbChooseClass.getValue());
    } catch (NotFoundException e) {
      Utils.showAlert(Alert.AlertType.ERROR, "Błąd odczytu klasy.");
      e.printStackTrace();
      return;
    }

    if (ctClass.isFrozen()) {
      Utils.showAlert(Alert.AlertType.ERROR, "Nie można modyfikować wybranej klasy.");
      return;
    }

    StringBuilder methodSb = new StringBuilder();
    methodSb.append(fldMethodSignature.getText())
            .append("{")
            .append(fldMethodBody.getText())
            .append("}");

    try {
      CtMethod ctMethod = CtNewMethod.make(methodSb.toString(), ctClass);
      ctClass.addMethod(ctMethod);

      Utils.showAlert(Alert.AlertType.INFORMATION, "Dodano metodę " + ctMethod.getName() + " do klasy " + ctClass.getName());

    } catch (CannotCompileException e) {
      Utils.showAlert(Alert.AlertType.ERROR, "Wystąpił błąd podczas dodawania metody.\n" + e.getLocalizedMessage());
      e.printStackTrace();
    }

    fldMethodSignature.clear();
    fldMethodBody.clear();
  }

  @FXML
  private void onBtnEndEditClick(ActionEvent event) {
    boolean result = Utils.showConfirm("Czy na pewno chcesz zakonczyć edycję? Nie będziesz mógł powrócić do tego trybu.");

    if (!result) {
      return;
    }

    cbChooseClass.setDisable(true);

    lblAddMethod.setVisible(false);
    lblMethodSignature.setVisible(false);
    fldMethodSignature.setVisible(false);
    lblMethodBody.setVisible(false);
    fldMethodBody.setVisible(false);
    btnAddMethod.setVisible(false);
    btnEndEdit.setVisible(false);

    lblInvokeMethod.setVisible(true);
    cbInvokeClass.setVisible(true);
    cbInvokeMethod.setVisible(true);
    fldArguments.setVisible(true);
    btnExecute.setVisible(true);

    lblChangeHierarchy.setVisible(false);
    lblSuperclass.setVisible(false);
    cbSuperclass.setVisible(false);
    btnSuperclass.setVisible(false);
  }

  @FXML
  private void onCbInvokeClass(ActionEvent event) {
    List<String> methods = getMethodNames();
    cbInvokeMethod.getItems().clear();
    cbInvokeMethod.getItems().addAll(methods);
  }

  @FXML
  private void onCbInvokeMethod(ActionEvent event) {

  }

  @FXML
  private void onBtnExecuteClick(ActionEvent event) {
    CtClass ctClass = null;
    try {
      ctClass = classPool.getCtClass(cbInvokeClass.getValue());
    } catch (NotFoundException e) {
      Alert alert = new Alert(Alert.AlertType.ERROR, "Błąd odczytu klasy.");
      alert.show();
      e.printStackTrace();

      return;
    }

    Class clazz = ClassMapper.getInstance().getClass(ctClass);
    String method = cbInvokeMethod.getValue();
    String arguments = fldArguments.getText();

    MethodInvoker.getInstance().invoke(clazz, method, arguments);
  }

  @FXML
  private void onBtnSuperclassClick(ActionEvent event) {
    String childClassString = cbChooseClass.getValue();
    String parentClassString = cbSuperclass.getValue();

    try {
      CtClass childCtClass = classPool.get(childClassString);
      CtClass parentCtClass = classPool.get(parentClassString);

      childCtClass.setSuperclass(parentCtClass);
      childCtClass.writeFile();

      Utils.showAlert(Alert.AlertType.INFORMATION, "Dokonano zmiany hierarchii dziedziczenia klasy " + childCtClass.getSimpleName() + ".");
    } catch (IOException | CannotCompileException | NotFoundException e) {
      e.printStackTrace();
    }
  }

  static FileChooser getChooser() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Wybierz plik .jar");
    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archiwum Java (.jar)", "*.jar"));

    return fileChooser;
  }

  private void loadFile() {
    try {
      //dodaj plik do class path
      classPool.appendClassPath(file.getAbsolutePath());
    } catch (NotFoundException e) {
      e.printStackTrace();
    }
  }

  private List<String> getClassNames() {
    List<String> names = new ArrayList<>();

    JarFile jarFile = null;

    try {
      jarFile = new JarFile(file);


      Enumeration<JarEntry> entries = jarFile.entries();
      while (entries.hasMoreElements()) {
        JarEntry entry = entries.nextElement();
        if (entry.getName().endsWith(".class")) {
          String name = entry.getName().substring(0, entry.getName().length() - 6).replace("/", ".");
          names.add(name);

        }
      }

    } catch (IOException e) {
      e.printStackTrace();
    }

    if (jarFile != null) {
      try {
        jarFile.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    return names;
  }

  private List<String> getMethodNames() {
    List<String> names = new ArrayList<>();

    CtClass ctClass = null;
    try {
      ctClass = classPool.getCtClass(cbInvokeClass.getValue());
    } catch (NotFoundException e) {
      Alert alert = new Alert(Alert.AlertType.ERROR, "Błąd odczytu klasy.");
      alert.show();
      e.printStackTrace();

      return null;
    }

    CtMethod[] methods = ctClass.getMethods();
    for (CtMethod method : methods) {
      StringBuilder nameSb = new StringBuilder();
      nameSb.append(method.getName());

      StringBuilder parametersSb = new StringBuilder();
      try {
        for (CtClass type : method.getParameterTypes()) {
          if (parametersSb.length() > 0) {
            parametersSb.append(", ");
          }

          parametersSb.append(type.getName());
        }
      } catch (NotFoundException e) {
        e.printStackTrace();
      }

      nameSb.append("(").append(parametersSb).append(")");

      names.add(nameSb.toString());
    }

    return names;
  }

  public List<String> getPossibleSuperclasses(String className) {
    List<String> classes = getClassNames();
    List<String> impossibleSuperclasses = new LinkedList<>();

    try {
      for (String clazz : classes) {
        CtClass ctClass = classPool.get(clazz);

        while (ctClass.getSuperclass() != null) {
          ctClass = ctClass.getSuperclass();

          if (ctClass.getName().equals(cbChooseClass.getValue())) {
            impossibleSuperclasses.add(clazz);
            break;
          }
        }
      }

    } catch (NotFoundException e) {
      e.printStackTrace();
    }

    classes.removeAll(impossibleSuperclasses);
    classes.remove(cbChooseClass.getValue()); //klasa nie może dziedziczyć sama po sobie

    //dirtyfix
    classes.add("java.lang.Object");

    return classes;
  }

  public Stage getStage() {
    return stage;
  }

  public void setStage(Stage stage) {
    this.stage = stage;
  }
}
