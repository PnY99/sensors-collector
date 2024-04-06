package utils;

import exceptions.PropertyNotFoundException;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Configuration {
    private static Properties properties;
    public static void load(String path) throws IOException {
        properties = new Properties();
        properties.load(new FileInputStream(path));
    }

    public static boolean isDebug() {
        if(!properties.containsKey("debug")) {
            return false;
        }
        return properties.getProperty("debug").equals("true");
    }

    public static int getInt(String property) throws PropertyNotFoundException {
        if(!properties.containsKey(property)) {
            throw new PropertyNotFoundException("Property "+property+" not defined");
        }

        return Integer.parseInt(properties.getProperty(property));
    }

    public static String getString(String property) throws PropertyNotFoundException {
        if(!properties.containsKey(property)) {
            throw new PropertyNotFoundException("Property "+property+" not defined");
        }

        return properties.getProperty(property);
    }

    public static boolean getBoolean(String property) throws PropertyNotFoundException {
        if(!properties.containsKey(property)) {
            throw new PropertyNotFoundException("Property "+property+" not defined");
        }

        return Boolean.parseBoolean(properties.getProperty(property));
    }
}
