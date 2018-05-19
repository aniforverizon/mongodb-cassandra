package com.bmincey;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author bmincey (blaine.mincey@gmail.com)
 * Date Created: 5/16/18
 */
public class ApplicationProperties {

    Properties properties = new Properties();
    String applicationPropertiesFile = "application.properties";

    // Static property names
    // Cassandra props
    public static final String contactPointProperty = "contactPoint";
    public static final String keySpaceProperty = "keySpace";
    public static final String tableProperty = "table";
    // MongoDB props
    public static final String mongoDBUriProperty = "mongoDBUri";
    public static final String databaseProperty = "database";
    public static final String collectionProperty = "collection";
    public static final String useBulkInsertProperty = "useBulkInsert";


    /**
     *
     * @throws IOException
     */
    public ApplicationProperties() throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(this.applicationPropertiesFile);

        if(inputStream != null) {
            properties.load(inputStream);
        }
        else {
            throw new FileNotFoundException("Property file not found in classpath!");
        }
    }


    /**
     *
     * @param propertyName
     * @return
     */
    public String getApplicationProperty(final String propertyName) {
        return properties.getProperty(propertyName);
    }

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        try {
            ApplicationProperties appProps = new ApplicationProperties();

            String contactPoint = appProps.getApplicationProperty(ApplicationProperties.contactPointProperty);
            System.out.println(contactPoint);
        }
        catch(IOException ioe) {
            System.err.println(ioe);
        }
    }
}
