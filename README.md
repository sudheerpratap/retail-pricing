# retail-pricing

The goal for this exercise is to create an end-to-end Proof-of-Concept for a products API, which will aggregate product data from multiple sources and return it as JSON to the caller. 
  
  Tech Stack :
  - Spring Boot
  - Spring data Cassandra
  - Spring Cloud Netflix Hystrix
  - DataStax Cassandra
  - Spock for Unit testing
  - Java 8
  - Groovy
  - Lombok
  - Swagger


For running the application in your local machine. Please follow the below steps:
- Install DataStax Cassandra free version and make sure after startup. Cassandra is in localhost:9042 port. 
- Install java 8 
- IntelliJ for IDE
- Gradle 
- Add Groovy frame work support to run spock tests  

After installing the above . Please run the cql statements inside the project folder resources/cql . Please run the keyspace and column familty details present in the cql files.

Navigate to the project folder and execute the below command in the terminal.
-> gradle bootRun

Above command will bring up the application. and open the below swagger link to test the apis.

http://localhost:8070/swagger-ui.html#!/


Note:
If you are running this from the main method in the java file or running the test cases seperately. Please perform the following:
- Install Lombok plugin in your IDE to suppor the @Slf4j and @Data annotations.
- Enable annotation processing in IntelliJ IDE in the perferences menu.
