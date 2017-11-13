package pl.edu.wat.wcy.jfk.controller;

import javafx.scene.control.Alert;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MethodInvoker {

  private ClassPool classPool;

  private static MethodInvoker instance;

  public void invoke(Class clazz, String methodString, String argumentsString) {
    Object object = ClassMapper.getInstance().getObject(clazz);

    String name = getMethodName(methodString);
    String[] parameters = getMethodParameters(methodString);
    String[] arguments = splitArguments(argumentsString);


    Method method = getMethod(clazz, name, parameters);
    Object[] objectArguments = convertToObjectParams(arguments, method.getParameterTypes());


    Object result = null;

    try {
      if (method.getParameterTypes().length > 0) {
        result = method.invoke(object, objectArguments);
      } else {
        result = method.invoke(object);
      }
    } catch (IllegalAccessException | InvocationTargetException e) {
      Utils.showAlert(Alert.AlertType.ERROR, "Wystąpił błąd: " + e.getMessage());
    }

    if (result != null) {
      Utils.showAlert(Alert.AlertType.INFORMATION, "Wywołanie zwróciło wartość:\n" + result.toString());
    }

  }

  private String getMethodName(String method) {
    String name = method.substring(0, method.indexOf("("));
    return name;
  }

  private String[] getMethodParameters(String method) {
    System.out.println("method = " + method + "; indexOf1 = " + method.indexOf("(") + "; indexOf2 = " + method.indexOf(")"));

    String parametersStr = method.substring(method.indexOf("(") + 1, method.indexOf(")"));

    if (parametersStr.isEmpty()) {
      return new String[0];
    }

    String[] parameters = parametersStr.split(", ");

    System.out.println("parametersStr = " + parametersStr);
    for (String param : parameters) {
      System.out.println("param = " + param);
    }

//    String[] parameterStr = arguments.split(" ");
//    Object[] parameters = new Object[parameterStr.length];
//
//    for (int i = 0; i < parameterStr.length; i++) {
//      parameters[i] = CastHelper.cast(parameterStr[i], )
//    }
//
//    return null;

    return parameters;
  }

  private Method getMethod(Class clazz, String methodName, String[] methodParameters) {
    Method[] methods = clazz.getMethods();

    for (Method method : methods) {
      System.out.println("method name = " + method.getName() + "; from param = " + methodName);
      if (method.getName().equals(methodName)) {
        Class[] parameters = method.getParameterTypes();

        if (isParamsSameType(parameters, methodParameters)) {
          return method;
        }
      }
    }

    return null;
  }

  private boolean isParamsSameType(Class[] params, String[] methodParams) {
    System.out.println("parameters length = " + params.length + "; methodParameters.length = " + methodParams.length);
    if (params.length != methodParams.length) {
      return false;
    }

    for (int i = 0; i < params.length; i++) {
      System.out.println("idx = " + i + "; parameters  = " + params[i] + "; methodParams = " + methodParams[i]);

      if (!params[i].getName().equals(methodParams[i])) {
        return false;
      }
    }

    return true;
  }

  private String[] splitArguments(String argumentsString) {
    if (argumentsString.isEmpty()) {
      return new String[0];
    }

    String[] arguments = argumentsString.split(" ");
    return arguments;
  }

  private Object[] convertToObjectParams(String[] stringParams, Class[] paramClasses) {
    System.out.println("stringParams lenght = " + stringParams.length + "; paramClasses length = " + paramClasses.length);

    Object[] objectParams = new Object[stringParams.length];

    for (int i = 0; i < stringParams.length; i++) {
      objectParams[i] = CastHelper.cast(stringParams[i], paramClasses[i]);
    }

    return objectParams;
  }

  public void test(CtClass ctClass) {
    while (ctClass != null) {
      CtMethod[] methods = ctClass.getDeclaredMethods();
      for (CtMethod method : methods) {
        System.out.println("Current method = " + method.getName());

        try {
          for (CtClass ctClass1 : method.getParameterTypes()) {
            System.out.println("Current parameter = " + ctClass1.getName());
          }
        } catch (NotFoundException e) {
          e.printStackTrace();
        }
      }
      try {
        ctClass = ctClass.getSuperclass();
      } catch (NotFoundException e) {
        e.printStackTrace();
      }
    }
  }

  public ClassPool getClassPool() {
    return classPool;
  }

  public void setClassPool(ClassPool classPool) {
    this.classPool = classPool;
  }

  public static MethodInvoker getInstance() {
    if (instance == null) {
      instance = new MethodInvoker();
    }

    return instance;
  }
}
