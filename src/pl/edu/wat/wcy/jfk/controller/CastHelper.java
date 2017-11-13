package pl.edu.wat.wcy.jfk.controller;

public class CastHelper {

  public static Object cast(String value, Class clazz) {
    Object object = null;

    if (clazz == byte.class || clazz == Byte.class) {
      object = Byte.valueOf(value);
    } else if (clazz == short.class || clazz == Short.class) {
      object = Short.valueOf(value);
    } else if (clazz == int.class || clazz == Integer.class) {
      object = Integer.valueOf(value);
    } else if (clazz == long.class || clazz == Long.class) {
      object = Long.valueOf(value);
    } else if (clazz == float.class || clazz == Float.class) {
      object = Float.valueOf(value);
    } else if (clazz == double.class || clazz == Double.class) {
      object = Double.valueOf(value);
    } else if (clazz == boolean.class || clazz == Boolean.class) {
      object = Boolean.valueOf(value);
    } else if (clazz == char.class || clazz == Character.class) {
      object = value.charAt(0);
    } else if (clazz == String.class) {
      object = value;
    }

    return object;
  }

}
