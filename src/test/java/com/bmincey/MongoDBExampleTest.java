package com.bmincey;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author bmincey (blaine.mincey@gmail.com)
 * Date Created: 5/15/18
 */
public class MongoDBExampleTest {

    // Props pulled from pom.xml
    private static String MONGODB_URI;
    private static String DATABASE;
    private static String COLLECTION;
    private static boolean USE_BULK_INSERT;

    // Property names
    private static final String MONGODB_URI_PROPERTY = "MongoDBUri";
    private static final String DATABASE_PROPERTY = "database";
    private static final String COLLECTION_PROPERTY = "collection";
    private static final String USE_BULK_INSERT_PROPERTY = "useBulkInsert";

    /**
     *
     */
    @BeforeClass
    public static void beforeClass() {
        MongoDBExampleTest.MONGODB_URI = System.getProperty(MongoDBExampleTest.MONGODB_URI_PROPERTY);
        MongoDBExampleTest.DATABASE = System.getProperty(MongoDBExampleTest.DATABASE_PROPERTY);
        MongoDBExampleTest.COLLECTION = System.getProperty(MongoDBExampleTest.COLLECTION_PROPERTY);
        MongoDBExampleTest.USE_BULK_INSERT =
                Boolean.parseBoolean(System.getProperty(MongoDBExampleTest.USE_BULK_INSERT_PROPERTY));

    }


    /**
     * Rigorous Test :-)
     */
    @Test
    public void test() {
        new MongoDBExample(MongoDBExampleTest.MONGODB_URI,
                           MongoDBExampleTest.DATABASE,
                           MongoDBExampleTest.COLLECTION,
                           MongoDBExampleTest.USE_BULK_INSERT);
    }
}
