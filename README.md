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
In order to provide an example of test results for comparison, I utilized an Amazon AMI (Amazon Linux) with a t2.small instance type.  I installed both Cassandra and MongoDB using the directions provide by either Datastax or MongoDB as an RPM.  Everything was setup using the default configuration on a single node.  I made the appropriate edits to the application.properties file and then created two separate executable JAR files using the directions above.  The following are my test results.

### Cassandra
```
Default constructor.
Using application.properties.
SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
SLF4J: Defaulting to no-operation (NOP) logger implementation
SLF4J: See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.
Cassandra version: 3.11.2
Connected to cluster: Test Cluster
Datacenter: datacenter1; Host: localhost/127.0.0.1; Rack: rack1
Table myKeySpace.myTable created!
Creating data... iterations = 1000
Created rows = 3000 in time = 00:00:03.854
Getting max value for sample...
Max value = Row[113.0] in time = 00:00:00.006
Getting all rows for sample...
Row[host1, m1, Fri Jun 01 21:53:49 UTC 2018, 109.0]
Row[host1, m1, Fri Jun 01 21:53:50 UTC 2018, 112.0]
Row[host1, m1, Fri Jun 01 21:53:50 UTC 2018, 113.0]
Row[host1, m1, Fri Jun 01 21:53:50 UTC 2018, 102.0]
Row[host1, m1, Fri Jun 01 21:53:50 UTC 2018, 101.0]
Row[host1, m1, Fri Jun 01 21:53:51 UTC 2018, 100.0]
Row[host1, m1, Fri Jun 01 21:53:51 UTC 2018, 93.0]
Row[host1, m1, Fri Jun 01 21:53:52 UTC 2018, 95.0]
Row[host1, m1, Fri Jun 01 21:53:52 UTC 2018, 93.0]
Row[host1, m1, Fri Jun 01 21:53:52 UTC 2018, 93.0]
Row[host1, m1, Fri Jun 01 21:53:52 UTC 2018, 94.0]
time = 00:00:00.054
Select ALL...
Got rows (without fetching) = 3000
Returned rows = 3000, total bytes = 157206, in time = 00:00:00.180

```
### MongoDB
```
Default constructor.
Using application.properties.
SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
SLF4J: Defaulting to no-operation (NOP) logger implementation
SLF4J: See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.
MongoDBUri: mongodb://localhost:27017
Database: test
Collection: myCollection
useBulkInsert: true
Creating data... iterations = 1000
Created rows = 3000 in time = 00:00:00.855
Getting max value for sample...
Max value = 122.0 in time = 00:00:00.009
Getting all rows for sample...
Document{{host=host1, metric=m1, time=Fri Jun 01 21:57:42 UTC 2018, value=107.0}}
Document{{host=host1, metric=m1, time=Fri Jun 01 21:57:42 UTC 2018, value=111.0}}
Document{{host=host1, metric=m1, time=Fri Jun 01 21:57:42 UTC 2018, value=106.0}}
Document{{host=host1, metric=m1, time=Fri Jun 01 21:57:42 UTC 2018, value=101.0}}
Document{{host=host1, metric=m1, time=Fri Jun 01 21:57:42 UTC 2018, value=117.0}}
Document{{host=host1, metric=m1, time=Fri Jun 01 21:57:42 UTC 2018, value=121.0}}
Document{{host=host1, metric=m1, time=Fri Jun 01 21:57:42 UTC 2018, value=122.0}}
Document{{host=host1, metric=m1, time=Fri Jun 01 21:57:42 UTC 2018, value=100.0}}
Document{{host=host1, metric=m1, time=Fri Jun 01 21:57:42 UTC 2018, value=95.0}}
time = 00:00:00.030
Select ALL...
Returned rows = 3000, totalBytes = 12000, in time = 00:00:00.044
```
### Notable comparisons
In creating 3000 rows, it took Cassandra 3.854 seconds and MongoDB 0.855 seconds. \
In selecting the max value for a sample, Cassandra 0.006 seconds and MongoDB 0.009 seconds. \
In getting all rows for a specific sample, Cassandra 0.054 seconds and MongoDB 0.030 seconds. \
In selecting all 3000 rows, Cassandra 0.180 seconds and MongoDB 0.044 seconds.
