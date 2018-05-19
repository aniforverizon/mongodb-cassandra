package com.bmincey;

import com.datastax.driver.core.*;
import org.apache.commons.lang3.time.StopWatch;

import java.io.IOException;

/**
 * Modified from https://www.instaclustr.com/hello-cassandra-java-client-example/
 *
 */
public class CassandraExample {


    private String CONTACT_POINT;
    private String KEYSPACE;
    private String TABLE;

    private Cluster cluster;
    private Session session;
    private ResultSet rs;


    /**
     * Default constructor
     */
    public CassandraExample() {
        System.out.println("Default constructor.");
        System.out.println("Using application.properties.");

        try {
            ApplicationProperties appProps = new ApplicationProperties();

            this.CONTACT_POINT = appProps.getApplicationProperty(ApplicationProperties.contactPointProperty);
            this.KEYSPACE = appProps.getApplicationProperty(ApplicationProperties.keySpaceProperty);
            this.TABLE = appProps.getApplicationProperty(ApplicationProperties.tableProperty);

            this.init();
        }
        catch(IOException ioe) {
            System.err.println(ioe);
        }

    }

    /**
     * Overloaded constructor
     *
     * @param contactPoint
     * @param keySpace
     * @param table
     */
    public CassandraExample(String contactPoint, String keySpace, String table) {
        System.out.println("Overloaded constructor.");

        this.CONTACT_POINT = contactPoint;
        this.KEYSPACE = keySpace;
        this.TABLE = table;

        this.init();
    }

    /**
     *
     */
    public void init() {
        this.dbInit();
        this.printMetadata();
        this.createTable();
        this.generateSampleData();
        this.cleanUp();
    }

    /**
     *
     */
    private void dbInit() {
        cluster = null;
        try {
            cluster = Cluster.builder()
                    .addContactPoint(this.CONTACT_POINT)
                    .build();
            session = cluster.connect();
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    /**
     *
     */
    private void printMetadata() {
        // Print Cassandra release version
        rs = session.execute("select release_version from system.local");
        Row row = rs.one();
        System.out.println("Cassandra version: " + row.getString("release_version"));

        // Print Cassandra metadata
        Metadata metadata = cluster.getMetadata();
        System.out.printf("Connected to cluster: %s\n", metadata.getClusterName());

        for (Host host : metadata.getAllHosts()) {
            System.out.printf("Datacenter: %s; Host: %s; Rack: %s\n", host.getDatacenter(), host.getAddress(), host.getRack());
        }
    }

    /**
     *
     */
    private void createTable() {

        // Create keyspace
        rs = session.execute("CREATE KEYSPACE IF NOT EXISTS " + this.KEYSPACE +
                " WITH replication = {'class': 'SimpleStrategy', 'replication_factor' : 1}");

        // Drop table
        rs = session.execute("DROP TABLE IF EXISTS " + this.KEYSPACE + "." + this.TABLE);

        // Create table
        rs = session.execute("CREATE TABLE " + this.KEYSPACE + "." + this.TABLE +
                "(host text, metric text, time timestamp, value double, " +
                "PRIMARY KEY ((host, metric), time) ) WITH CLUSTERING ORDER BY (time ASC)");

        System.out.println("Table " + this.KEYSPACE + "." + this.TABLE + " created!");
    }

    /**
     *
     */
    private void generateSampleData() {
        StopWatch stopWatch = new StopWatch();

        // Create prepared statement
        PreparedStatement prepared = session.prepare("INSERT INTO " + this.KEYSPACE + "." + this.TABLE + " " +
                "(host, metric, time, value) values (?, ?, ?, ?)");

        double startValue = 100; // start value for random walk
        double nextValue = startValue; // next value in random walk, initially startValue
        int numHosts = 100; // how many host names to generate
        int toCreate = 1000; // how many times to pick a host name and create all metrics for it

        stopWatch.start();
        System.out.println("Creating data... iterations = " + toCreate);
        for (int r = 1; r <= toCreate; r++) {

            long now = System.currentTimeMillis();
            java.util.Date date = new java.util.Date(now);

            // generate a random host name
            String hostname = "host" + Math.round((Math.random() * numHosts));

            // do a random walk to produce realistic data
            double rand = Math.random();

            if (rand < 0.5) {
                // 50% chance that value doesn't change
            } else if (rand < 0.75) {
                // 25% chance that value increases by 1
                nextValue++;
            } else {
                // 25% chance that value decreases by 1
                nextValue--;
            }

            // never go negative
            if (nextValue < 0) {
                nextValue = 0;
            }

            // Execute prepared statements
            session.execute(prepared.bind(hostname , "m1", date, nextValue));
            session.execute(prepared.bind(hostname , "m2", date, nextValue * 10));
            session.execute(prepared.bind(hostname , "m3", date, nextValue * 100));

        }

        stopWatch.stop();
        System.out.println("Created rows = " + toCreate*3 + " in time = " + stopWatch.toString());

        // find the max value for a sample
        System.out.println("Getting max value for sample...");
        stopWatch.reset();

        stopWatch.start();
        rs = session.execute("select max(value) from " + this.KEYSPACE + "." +
                this.TABLE + " where host='host1' and metric='m1'");
        stopWatch.stop();

        Row row = rs.one();
        System.out.println("Max value = " + row.toString() + " in time = " + stopWatch.toString());

        // get all the values for a sample
        System.out.println("Getting all rows for sample...");
        stopWatch.reset();

        stopWatch.start();
        rs = session.execute("select * from " + this.KEYSPACE + "." + this.TABLE +
                " where host='host1' and metric='m1'");

        for (Row rowN : rs) {
            System.out.println(rowN.toString());
        }
        stopWatch.stop();

        System.out.println("time = " + stopWatch.toString());


        // Note that SELECT * will return all results without limit
        // (even though the driver might use multiple queries in the background).
        // To handle large result sets, you use a LIMIT clause in your CQL query,
        // or use one of the techniques described in the paging documentation.
        System.out.println("Select ALL...");
        stopWatch.reset();

        stopWatch.start();
        rs = session.execute("select * from " + this.KEYSPACE + "." + this.TABLE);
        System.out.println("Got rows (without fetching) = " + rs.getAvailableWithoutFetching());

        int i = 0;
        long numBytes = 0;
        // example use of the data: count rows and total bytes returned.
        for (Row rowN : rs)
        {
            i++;
            numBytes += rowN.toString().length();
        }
        stopWatch.stop();

        System.out.println("Returned rows = " + i + ", total bytes = " + numBytes +
                ", in time = " + stopWatch.toString());
    }

    /**
     *
     */
    private void cleanUp() {
        try {
            session.close();
            cluster.close();
        } finally {

            if (session != null) {
                session.close();
            }


            if (cluster != null) {
                cluster.close();
            }
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        new CassandraExample();
    }
}
