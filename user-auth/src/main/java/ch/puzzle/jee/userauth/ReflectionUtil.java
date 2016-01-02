package ch.puzzle.jee.userauth;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.*;

public final class ReflectionUtil {
    // Ensure non-insatiability.
    private ReflectionUtil() {
    }

    public static <T> Class<T> getActualTypeArguments(Class<?> clazz, int indexOfArgument) {
        Class<T> resolvedType = getActualTypeArguments(clazz.getSuperclass().getGenericSuperclass(), indexOfArgument);
        if (resolvedType == null) {
            return getActualTypeArguments(clazz.getGenericSuperclass(), indexOfArgument);
        }
        return resolvedType;
    }

    @SuppressWarnings("unchecked")
    public static <T> Class<T> getActualTypeArguments(Type type, int indexOfArgument) {
        if (type instanceof ParameterizedType) {
            ParameterizedType paramType = (ParameterizedType) type;
            Type typeArgument = paramType.getActualTypeArguments()[indexOfArgument];
            if (typeArgument instanceof Class<?>) {
                return (Class<T>) typeArgument;
            }
        }
        return null;
    }

    public static Class<?> getGenericType(Field field) {
        ParameterizedType type = (ParameterizedType) field.getGenericType();
        return (Class<?>) type.getActualTypeArguments()[0];
    }

    public static <T> Object invokeGetter(Field field, Class<T> clazz, T targetObject) throws InvocationTargetException,
            IllegalAccessException, NoSuchMethodException, IntrospectionException {
        PropertyDescriptor descriptor = new PropertyDescriptor(field.getName(), clazz);
        if (descriptor.getReadMethod() != null) {
            return descriptor.getReadMethod().invoke(targetObject);
        } else {
            throw new NoSuchMethodException("No getter for " + descriptor.getDisplayName());
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> void invokeSetter(Field field, Class<T> clazz, T targetObject, Object value) throws IntrospectionException,
            InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        PropertyDescriptor descriptor = new PropertyDescriptor(field.getName(), clazz);
        Method setter = descriptor.getWriteMethod();
        if (setter != null) {
            Class<?> targetClass = setter.getParameterTypes()[0];

            // do we need to convert the number type?
            if (value != null && Number.class.isAssignableFrom(targetClass)) {
                Number numValue = (Number) value;
                numValue = convertNumberType(numValue, (Class<? extends Number>) targetClass);
                setter.invoke(targetObject, numValue);
            } else {
                setter.invoke(targetObject, value);
            }
        } else {
            throw new NoSuchMethodException("No setter for " + descriptor.getDisplayName());
        }
    }

    public static Constructor<?> getConstructor(Class<?> clazz, Object[] parameters) throws NoSuchMethodException {
        Class<?>[] parameterClasses = new Class<?>[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            parameterClasses[i] = parameters[i] == null ? null : parameters[i].getClass();
        }

        return clazz.getConstructor(parameterClasses);
    }

    public static Number convertNumberType(Number number, Class<? extends Number> targetType) {
        if (Byte.class.isAssignableFrom(targetType)) {
            return number.byteValue();
        } else if (Short.class.isAssignableFrom(targetType)) {
            return number.shortValue();
        } else if (Integer.class.isAssignableFrom(targetType)) {
            return number.intValue();
        } else if (Long.class.isAssignableFrom(targetType)) {
            return number.longValue();
        } else if (Float.class.isAssignableFrom(targetType)) {
            return number.floatValue();
        } else {
            return number.longValue();
        }
    }
}
