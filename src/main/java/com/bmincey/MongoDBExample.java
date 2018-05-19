package com.bmincey;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.BulkWriteOptions;
import com.mongodb.client.model.InsertOneModel;
import com.mongodb.client.model.WriteModel;
import org.apache.commons.lang3.time.StopWatch;
import org.bson.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Projections.*;
import static com.mongodb.client.model.Sorts.descending;

/**
 * @author bmincey (blaine.mincey@gmail.com)
 * Date Created: 5/14/18
 */
public class MongoDBExample {

    private String MONGODB_URI;
    private String DATABASE;
    private String COLLECTION;

    private boolean useBulkInsert;

    private MongoDatabase mongoDatabase = null;
    private MongoCollection<Document> mongoCollection = null;

    /**
     *
     */
    public MongoDBExample() {
        System.out.println("Default constructor.");
        System.out.println("Using application.properties.");

        try {
            ApplicationProperties appProps = new ApplicationProperties();

            this.MONGODB_URI = appProps.getApplicationProperty(ApplicationProperties.mongoDBUriProperty);
            this.DATABASE = appProps.getApplicationProperty(ApplicationProperties.databaseProperty);
            this.COLLECTION = appProps.getApplicationProperty(ApplicationProperties.collectionProperty);
            this.useBulkInsert =
                    Boolean.parseBoolean(appProps.getApplicationProperty(ApplicationProperties.useBulkInsertProperty));

            this.init();
        }
        catch(IOException ioe) {
            System.err.println(ioe);
        }
    }

    /**
     * @param mongoDBUri
     * @param database
     * @param collection
     * @param useBulkInsert
     */
    public MongoDBExample(String mongoDBUri, String database, String collection, boolean useBulkInsert) {
        System.out.println("Using overloaded constructor.");

        this.MONGODB_URI = mongoDBUri;
        this.DATABASE = database;
        this.COLLECTION = collection;
        this.useBulkInsert = useBulkInsert;

        this.init();
    }



    /**
     *
     */
    private void init() {
        this.dbInit();
        this.printMetadata();
        this.generateSampleData();
    }

    /**
     *
     */
    private void dbInit() {
        MongoClientURI uri = new MongoClientURI(this.MONGODB_URI);
        MongoClient mongoClient = new MongoClient(uri);

        this.mongoDatabase = mongoClient.getDatabase(this.DATABASE);

        if (this.mongoDatabase.listCollectionNames().into(new ArrayList<String>()).contains(this.COLLECTION)) {
            this.mongoCollection = this.mongoDatabase.getCollection(this.COLLECTION);
            this.mongoCollection.drop();
        }

        this.mongoCollection = this.mongoDatabase.getCollection(this.COLLECTION);


    }

    /**
     *
     */
    public void printMetadata() {
        System.out.println("MongoDBUri: " + this.MONGODB_URI);
        System.out.println("Database: " + this.DATABASE);
        System.out.println("Collection: " + this.COLLECTION);
        System.out.println("useBulkInsert: " + useBulkInsert);
    }

    /**
     *
     */
    private void generateSampleData() {
        StopWatch stopWatch = new StopWatch();

        double startValue = 100; // start value for random walk
        double nextValue = startValue; // next value in random walk, initially startValue
        int numHosts = 100; // how many host names to generate
        int toCreate = 1000; // how many times to pick a host name and create metrics

        stopWatch.start();

        System.out.println("Creating data... iterations = " + toCreate);
        for (int r = 1; r <= toCreate; r++) {
            long now = System.currentTimeMillis();
            java.util.Date date = new java.util.Date(now);

            // generate a random host name
            String hostname = "host" + Math.round((Math.random() * numHosts));

            // do a random walk to produce realistic data
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

            Document document1 = new Document("host", hostname)
                    .append("metric", "m1")
                    .append("time", date)
                    .append("value", nextValue);

            Document document2 = new Document("host", hostname)
                    .append("metric", "m2")
                    .append("time", date)
                    .append("value", nextValue * 10);

            Document document3 = new Document("host", hostname)
                    .append("metric", "m3")
                    .append("time", date)
                    .append("value", nextValue * 100);

            if (useBulkInsert) {
                List<WriteModel<Document>> writes = new ArrayList<WriteModel<Document>>();
                writes.add(new InsertOneModel<>(document1));
                writes.add(new InsertOneModel<>(document2));
                writes.add(new InsertOneModel<>(document3));

                this.mongoCollection.bulkWrite(writes, new BulkWriteOptions().ordered(true));
            } else {

                this.mongoCollection.insertOne(document1);
                this.mongoCollection.insertOne(document2);
                this.mongoCollection.insertOne(document3);
            }

        }

        stopWatch.stop();
        System.out.println("Created rows = " + toCreate * 3 + " in time = " + stopWatch.toString());

        // find the max value for a sample
        System.out.println("Getting max value for sample...");
        stopWatch.reset();
        stopWatch.start();

        Iterable<Document> documents =
                this.mongoCollection.find((and(eq("host", "host1"),
                        eq("metric", "m1"))))
                        .projection(fields(include("host", "metric", "time", "value"), excludeId()))
                        .sort(descending("value"))
                        .limit(1);

        Document maxValueDocument = documents.iterator().next();

        stopWatch.stop();

        System.out.println("Max value = " + maxValueDocument.get("value") + " in time = " + stopWatch.toString());

        // get all the values for a sample
        System.out.println("Getting all rows for sample...");
        stopWatch.reset();
        stopWatch.start();

        Iterable<Document> sampleDocuments =
                this.mongoCollection.find((and(eq("host", "host1"),
                        eq("metric", "m1"))))
                        .projection(fields(include("host", "metric", "time", "value"), excludeId()));


        for (Document document : sampleDocuments) {
            System.out.println(document.toString());
        }

        stopWatch.stop();
        System.out.println("time = " + stopWatch.toString());

        // select all
        System.out.println("Select ALL...");
        stopWatch.reset();
        stopWatch.start();

        Iterable<Document> allDocuments =
                this.mongoCollection.find()
                        .projection(fields(include("host", "metric", "time", "value"), excludeId()));

        int counter = 0;
        long numBytes = 0;

        for (Document document : allDocuments) {
            counter++;
            numBytes += document.size();

        }

        stopWatch.stop();
        System.out.println("Returned rows = " + counter +
                ", totalBytes = " + numBytes + ", in time = " + stopWatch.toString());
    }

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        new MongoDBExample();
    }
}
