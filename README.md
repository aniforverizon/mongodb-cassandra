# mongodb-cassandra
Sample command line applications to compare the performance of Cassandra v MongoDB.  The code emulates IOT devices reporting metrics to be persisted in a datastore.  By default, 3 separate random measurments are made through 1000 iterations.  In all, through each execution, 3000 rows should be created in both Cassandra and MongoDB.  Both of the main classes have almost exactly the same logic except using the appropriate driver when required, i.e. the Datastax JDBC driver for Cassandra and the MongoDB Java driver for MongoDB.

The applications can be run using two separate methods: via maven or building an executable jar.

Requirements for using this repository include Maven and Java.

## Run the tests using Maven Test
1.  Modify the appropriate settings in pom.xml.  Modify the properties for Cassandra, notably the Cassandra URL or Contact Point.  Additionally, make similar modifications for MongoDB.  As a reference, the Cassandra code by default is using Java PreparedStatements in order to improve performance.  However, with MongoDB, you have the option of using the Bulk Insert operators.

``` 
<plugin>
        <artifactId>maven-surefire-plugin</artifactId>
        <configuration>
          <systemProperties>
            <!-- ############################ -->
            <!-- Cassandra Testing Properties -->
            <!-- ############################ -->
            <property>
              <name>contactPoint</name>
              <value>yourCassandraUrl</value>
            </property>
            <property>
              <name>keySpace</name>
              <value>myKeySpace</value>
            </property>
            <property>
              <name>table</name>
              <value>myTable</value>
            </property>
            <!-- ########################## -->
            <!-- MongoDB Testing Properties -->
            <!-- ########################## -->
            <property>
              <name>MongoDBUri</name>
              <value>mongodb://localhost:27017</value>
            </property>
            <property>
              <name>database</name>
              <value>test</value>
            </property>
            <property>
              <name>collection</name>
              <value>myCollection</value>
            </property>
            <property>
              <name>useBulkInsert</name>
              <value>true</value>
            </property>
          </systemProperties>
        </configuration>
      </plugin>
      
```

2.  After making the modifications to the Maven properties for both Cassandra and MongoDB in the pom.xml, you can then execute the tests.  At this point, you can run ` mvn test ` and both sets of tests will run in a single step with the output to the console.  However, if you would like to perform each test individually, the following set of steps should be performed: \
``` mvn compile -DskipTests ``` \
Then, to run specifically the Cassandra test: \
``` mvn -Dtest=CassandraExampleTest test ``` \
To run specifically the MongoDB test: \
``` mvn -Dtest=MongoDBExampleTest test ``` 

## Create executable JARs for both Cassandra and MongoDB
1.  Modify the appropriate properties contained in <application-root-dir>/src/main/resources/application.properties:
```
# Cassandra Properties
contactPoint=yourCassandraUrl
keySpace=myKeySpace
table=myTable

# MongoDB Properties
mongoDBUri=mongodb://localhost:27017
database=test
collection=myCollection
useBulkInsert=true

```
2.  After making the necessary edits to the application.properties file, simply 
``` 
mvn -DskipTests package
```

3.  This results in two separate executable JAR files: RunCassandraExample.jar and RunMongoDBExample.jar.  These files are built with all dependencies so they can be individually copied to wherever they need to be run.  Once the JAR file is in the correct environment, simply
```
java -jar RunMongoDBExample.jar
```
or
```
java -jar RunCassandraExample.jar
```

