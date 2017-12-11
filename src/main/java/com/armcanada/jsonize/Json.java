package com.armcanada.jsonize;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Json library that parses objects
 */
public class Json
{
    /**
     * Null field
     */
    private static final String NULL = "null";

    /**
     * Object to serialize
     */
    private final Object object;

    /**
     * Constructor
     *
     * @param object to serialize
     */
    private Json(Object object)
    {
        this.object = object;
    }

    @Override
    public String toString()
    {
        if (this.getObject() != null) {
            try {
                return this.getJson();
            } catch (IllegalAccessException | InvocationTargetException ignored) {}
        }
        return Json.NULL;
    }

    /**
     * Gets the object as json formatted
     *
     * @return Json string
     * @throws InvocationTargetException thrown if an append method could not be called
     * @throws IllegalAccessException thrown if a field cannot be accessed
     */
    private String getJson() throws InvocationTargetException, IllegalAccessException
    {
        if (this.isBasicObject()) {
            return this.parseBasicObject();
        } else if (this.isCollectionObject()) {
            return this.parseCollectionObject();
        } else if (this.isArrayObject()) {
            return this.parseArrayObject();
        }
        return this.parseObject();
    }

    /**
     * Parse an array object into json
     *
     * @return Json string
     */
    private String parseArrayObject()
    {
        List<String> objectString = new ArrayList<>();
        for (int i = 0; i < Array.getLength(this.getObject()); i++) {
            objectString.add(Json.convert(Array.get(this.getObject(), i)));
        }
        return String.format("[%s]", String.join(",", objectString));
    }

    /**
     * Parse a complex object into json
     *
     * @return Json String
     * @throws InvocationTargetException thrown if an append method could not be called
     * @throws IllegalAccessException thrown if a field cannot be accessed
     */
    private String parseObject() throws IllegalAccessException, InvocationTargetException
    {
        List<String> properties = this.appendObjectFields();
        properties.addAll(this.appendObjectMethods());
        return String.format("{%s}", String.join(",", properties));
    }

    /**
     * Appends all the methods that are annotated with @AppendJson
     *
     * @return List of json properties
     * @throws InvocationTargetException thrown if an append method could not be called
     * @throws IllegalAccessException thrown if a field cannot be accessed
     */
    private List<String> appendObjectMethods() throws IllegalAccessException, InvocationTargetException
    {
        List<String> properties = new ArrayList<>();
        for (Method method : this.object.getClass().getMethods()) {
            if (method.isAnnotationPresent(AppendJson.class) && !method.getReturnType().equals(Void.TYPE)) {
                AppendJson annotation = method.getAnnotation(AppendJson.class);
                String key = (annotation.key().length() > 0) ? annotation.key() : method.getName();
                properties.add(this.propertyAsString(key, Json.convert(method.invoke(this.getObject()))));
            }
        }
        return properties;
    }

    /**
     * Appends all the object fields that are not annotated with @DontJson
     * @return Json String
     * @throws IllegalAccessException thrown if a field cannot be accessed
     */
    private List<String> appendObjectFields() throws IllegalAccessException
    {
        List<String> properties = new ArrayList<>();
        for (Field field : this.object.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            if (!field.isAnnotationPresent(DontJson.class)) {
                properties.add(this.propertyAsString(field.getName(), Json.convert(field.get(this.getObject()))));
            }
        }
        return properties;
    }

    /**
     * Parses a basic Java object or type
     *
     * @return Json string
     */
    private String parseBasicObject()
    {
        if (this.isStringableObject()) {
            return String.format("\"%s\"", this.toSafeString(this.object.toString()));
        }
        return String.valueOf(this.object);
    }

    /**
     * Parses collection object
     *
     * @return Json string
     */
    private String parseCollectionObject()
    {
        List<String> objectString = new ArrayList<>();
        for (Object obj : (Collection)this.getObject()) {
            objectString.add(Json.convert(obj));
        }
        return String.format("[%s]", String.join(",", objectString));
    }

    /**
     * Checks if the object is a Number
     *
     * @return true if the object is a number
     */
    private boolean isNumberObject()
    {
        return Number.class.isAssignableFrom(this.getObjectClass());
    }

    /**
     * Checks if the object can be easily converted to string
     *
     * @return true if the object is castable easily
     */
    private boolean isStringableObject()
    {
        return this.getObjectClass() == String.class || this.getObjectClass() == Date.class;
    }

    /**
     * Checks if the the object is basic type
     *
     * @return true if a basic object
     */
    private boolean isBasicObject()
    {
        return this.isStringableObject() || this.isNumberObject();
    }

    /**
     * Checks if the object is a collection
     *
     * @return true if the object can be iterated through
     */
    private boolean isCollectionObject()
    {
        return Collection.class.isAssignableFrom(this.getObjectClass());
    }

    /**
     * Checks if the object is an array
     *
     * @return true if the object is an array
     */
    private boolean isArrayObject()
    {
        return this.getObjectClass().isArray();
    }

    /**
     * Gets a safe string by replacing double quotes
     *
     * @param value to parse
     * @return safe value
     */
    private String toSafeString(String value)
    {
        return value.replace("\"", "\\\"");
    }

    /**
     * Parse a key / value pair into a Json property
     * 
     * @param key of the property
     * @param value of the property
     * @return Json string
     */
    private String propertyAsString(String key, String value)
    {
        return String.format("\"%s\":%s", key, value);
    }

    /**
     * Gets the object
     *
     * @return the object to parse
     */
    private Object getObject()
    {
        return this.object;
    }

    /**
     * Gets the object class
     *
     * @return class of the object
     */
    private Class<?> getObjectClass()
    {
        return this.getObject().getClass();
    }

    /**
     * Facade method that converts an object to json
     * @param object to parse
     * @return Json string
     */
    public static String convert(Object object)
    {
        return (new Json(object)).toString();
    }
}
