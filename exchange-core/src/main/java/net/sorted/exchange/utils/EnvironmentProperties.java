package net.sorted.exchange.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class EnvironmentProperties {

    private static String ENV_PROPERTY = "env";

    /**
     * Returns a Properties object populated with environment properties.
     *
     * Properties are loaded from a file defined in a System Property named 'env'
     * Typically, these are passed at startup using -Denv=foo.properties
     */
    public static Properties setSystemFromEnvironment() {
        String filename = System.getProperty(ENV_PROPERTY);
        if (filename == null) {
            throw new IllegalArgumentException("Environment property file is not specified, use '-D" + ENV_PROPERTY + "=foo.properties'");
        }

        Properties properties = new Properties();

        try (InputStream is = ClassLoader.getSystemResourceAsStream(filename)) {

            if (is == null) {
                throw new RuntimeException("Unable to find '" + filename + "' in classpath");
            }

            // merge existing system properties with env properties
            properties.load(is);
            properties.putAll(System.getProperties());

            return properties;
        } catch (IOException e) {
            throw new RuntimeException("Unable to read environment properties from '" + filename + "'", e);
        }
    }
}
