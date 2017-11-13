package pl.edu.wat.wcy.jfk.controller;

import javafx.scene.control.Alert;
import javassist.CannotCompileException;
import javassist.CtClass;

import java.util.HashMap;

public class ClassMapper {

  private HashMap<CtClass, Class> classes = new HashMap<>();
  private HashMap<Class, Object> objects = new HashMap<>();

  private static ClassMapper instance;

  public Class getClass(CtClass ctClass) {
    Class clazz = classes.get(ctClass);

    try {
      if (clazz == null) {
        clazz = ctClass.toClass();
        classes.put(ctClass, clazz);
      }
    } catch (CannotCompileException e) {
      Utils.showAlert(Alert.AlertType.ERROR, "Wystąpił błąd: " + e.getMessage());
    }

    return clazz;
  }

  public Object getObject(Class clazz) {
    Object object = objects.get(clazz);

    try {
      if (object == null) {
        object = clazz.newInstance();
        objects.put(clazz, object);
      }
    } catch (IllegalAccessException | InstantiationException e) {
      Utils.showAlert(Alert.AlertType.ERROR, "Wystąpił błąd: " + e.getMessage());
    }

    return object;
  }

  public void addClass(CtClass ctClass, Class clazz) {
    classes.put(ctClass, clazz);
  }

  public static ClassMapper getInstance() {
    if (instance == null) {
      instance = new ClassMapper();
    }

    return instance;
  }

}
