# COmanage Changelog Provisioner AMQP Event Stream Source

  The COmanage [Changelog Provisioning Plugin](https://spaces.at.internet2.edu/display/COmanage/Changelog+Provisioning+Plugin) 
  writes JSON representations of [COmanage Registry](https://www.internet2.edu/products-services/trust-identity/comanage/) 
  provisioning events to a log file.  This application tails that log file, parses the events, and sends  CoPerson and 
  CoGroup updates to a [RabbitMQ](https://www.rabbitmq.com/) AMQP exchange.  The intent is to provide all COmanage user 
  and group updates  to an IdM system via event bus messages.  This is authored in Java using the 
  [Spring Framework](https://spring.io/projects/spring-framework).

  Using [Maven](https://maven.apache.org/) this application builds to one jar file and one property file.  In the 
  property file you define the full path to your changelog and you specify the RabbitMQ server plus exchange you would 
  like to publish to.  Within COmanage add the Changelog Provisioning Plugin to any CO that you would like to include 
  events from, and configure each plugin to write to the log file this application monitors. 
  
  This application is built using the [Spring Cloud Stream](https://spring.io/projects/spring-cloud-stream) library 
  which combines [Spring Boot](https://spring.io/projects/spring-boot) and 
  [Spring Integration](https://spring.io/projects/spring-integration).  In this application Java classes were only 
  written to define the event source and custom filtering.  The rest of the configuration is specified in the property 
  file and is configurable at run-time.  No Spring XML configuration was used.  Spring Cloud Stream automatically builds 
  a Spring Integration flow bound to the AMQP server specified in the property file.
 
This is a Spring Boot microservice with support added in the Maven POM file for direct deployment over ssh to target 
hosts/containers.  It also includes [jasypt](http://www.jasypt.org/features.html) support for strong encryption of
property file values.  If you have credentials to encrypt for use with this application you can do so with 
`components/StartUpTasks.java` by uncommenting a few lines. This application could be re-purposed to monitor any sort of
 log file.  Spring Cloud Stream supports multiple messaging systems.

## Requirements

* To build you will need Java 8 or above and Maven.
* To execute you will need Java 8 or above.

## Testing

If you want to test locally you will need to publish messages.  The below steps will launch a local RabbitMQ Server Docker container to publish to, then compile the application, then 
start the application.  This assumes you have Docker installed, and that both java and maven are on your path.

* Change directory to the project root
* `docker-compose up -d`

* `./mvnw clean package -P packageOnly`

* `./target/comanage-changelog-amqp-event-stream-source-1.0.jar`

Now you may open the local RabbitMQ admin interface to monitor events. `http://localhost:15672` You should already see 
that the exchange you specified in the property file has been created.  You might create a test queue and bind it to this 
exchange to collect messages during your testing.

Init scripts are included to run this as a service but you just launched it interactively.  So from another shell you 
can now test echoing lines to the simulated provisioner log file (./target/allCO_changelog.log)
* `echo testmessage >> ./target/allCO_changelog.log`

Once you are done testing `ctrl-c` will exit the application and `docker-compose down` will shutdown the RabbitMQ 
container.

## Deploying

Ssh deployment via [Maven Wagon](https://maven.apache.org/wagon/index.html) requires that you have created server entries in
your local `${user.home}/.m2/settings.xml` that specify authentication.  The ids of the these `settings.xml`server 
definition stanzas should match the <ssh.deploy.profile> ids in this project's pom.xml file.
  
The below will compile the application and file copy it to the path on the localhost specified in the profile with id 
`localhost` specified in the pom.xml file.  The property file used will be the application.properties file in the 
props/`localhost` directory.
* `./mvnw clean package -P localhost`

The below will compile the application and scp it to the host specified in the profile with id 
`development` specified in the pom.xml file.  The property file used will be the application.properties file in the 
props/`development` directory.
* `./mvnw clean package -P development`

The below will compile the application and scp it to the host specified in the profile with id 
`production` specified in the pom.xml file.  The property file used will be the application.properties file in the 
props/`production` directory.
* `./mvnw clean package -P production`

## Linux init scripts

  Included are init scripts for two popular init systems so that this application may be run as a system service.  If 
  you use either script you should edit it to specify the path to your Java JRE.  Java does not need to be in the path 
  of the account that executes this application.

#####Adding System V init script:

`sudo cp comanage-changelog-processor.systemV.init /etc/init.d/comanage-changelog-processor`

`sudo chmod +x /etc/init.d/comanage-changelog-processor`

`sudo chkconfig --add comanage-changelog-processor`

#####Adding systemd init script:

`sudo cp comanage-changelog-processor.systemd.init /etc/systemd/system/comanage-changelog-processor.service`

`sudo systemctl daemon-reload`

`sudo systemctl enable comanage-changelog-processor`
