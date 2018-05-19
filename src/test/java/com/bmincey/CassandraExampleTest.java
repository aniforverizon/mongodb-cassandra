package com.bmincey;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class CassandraExampleTest
{
    // Props pulled from pom.xml
    private static String CONTACT_POINT;
    private static String KEYSPACE;
    private static String TABLE;

    // Property names
    private static final String CONTACT_POINT_PROPERTY = "contactPoint";
    private static final String KEYSPACE_PROPERTY = "keySpace";
    private static final String TABLE_PROPERTY = "table";

    /**
     *
     */
    @BeforeClass
    public static void beforeClass() {
        CassandraExampleTest.CONTACT_POINT = System.getProperty(CassandraExampleTest.CONTACT_POINT_PROPERTY);
        CassandraExampleTest.KEYSPACE = System.getProperty(CassandraExampleTest.KEYSPACE_PROPERTY);
        CassandraExampleTest.TABLE = System.getProperty(CassandraExampleTest.TABLE_PROPERTY);
    }


    /**
     * Rigorous Test :-)
     */
    @Test
    public void test()
    {
        new CassandraExample(CassandraExampleTest.CONTACT_POINT,
                             CassandraExampleTest.KEYSPACE,
                             CassandraExampleTest.TABLE);
    }
}
