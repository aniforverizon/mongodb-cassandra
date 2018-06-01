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
## Sample Test Results
In order to provide an example of test results for comparison, I utilized an Amazon AMI (Amazon Linux) with a t2.small instance type.  I installed both Cassandra and MongoDB using the directions provide by either Datastax or MongoDB.  Everything was setup using the default configuration.  I made the appropriate edits to the application.properties file and then created two separate executable JAR files using the directions above.  The following are my test results.

```
Default constructor.
Using application.properties.
SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
SLF4J: Defaulting to no-operation (NOP) logger implementation
SLF4J: See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.
Cassandra version: 3.11.2
Connected to cluster: Test Cluster
Datacenter: datacenter1; Host: ##############/########; Rack: rack1
Table myKeySpace.myTable created!
Creating data... iterations = 1000
Created rows = 3000 in time = 00:00:02.706
Getting max value for sample...
Max value = Row[96.0] in time = 00:00:00.002
Getting all rows for sample...
Row[host1, m1, Fri Jun 01 21:34:42 UTC 2018, 92.0]
Row[host1, m1, Fri Jun 01 21:34:42 UTC 2018, 94.0]
Row[host1, m1, Fri Jun 01 21:34:42 UTC 2018, 96.0]
Row[host1, m1, Fri Jun 01 21:34:43 UTC 2018, 93.0]
Row[host1, m1, Fri Jun 01 21:34:43 UTC 2018, 88.0]
Row[host1, m1, Fri Jun 01 21:34:43 UTC 2018, 88.0]
Row[host1, m1, Fri Jun 01 21:34:43 UTC 2018, 95.0]
Row[host1, m1, Fri Jun 01 21:34:43 UTC 2018, 93.0]
Row[host1, m1, Fri Jun 01 21:34:43 UTC 2018, 84.0]
Row[host1, m1, Fri Jun 01 21:34:44 UTC 2018, 94.0]
Row[host1, m1, Fri Jun 01 21:34:44 UTC 2018, 96.0]
time = 00:00:00.146
Select ALL...
Got rows (without fetching) = 3000
Returned rows = 3000, total bytes = 155676, in time = 00:00:00.104

```
