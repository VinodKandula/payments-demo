Simple scaffolding for a file ingest, message streaming app with 3
components (plus the Hello World demo):

* _ingester_: takes a file of concatenated MT103 messages (with the
  "-}" separator for the end of each message) and puts each message on
  the wire. There are two triggers: a file watcher (any new file in
  `/tmp/inputs` by default), and HTTP (multi-part form) upload with a
  really basic GUI. Runs on port 8080.

* _processor_: receieves the payments from the ingester and does some
  processing (actually basically a no-op for the demo), and hands them
  off downstream. Runs on port 8081.

* _fast-payer_: takes the processed payments from upstream and logs
  them to stdout. Runs on port 8082.

* _demo_: a really basic REST controller that you can ping and get a
  "Hello World" message.

## Pre-requisites

All apps build and run locally from the command line if you have Java
8 on your path. Use the wrapper scripts to ensure you get the right
version of Maven, e.g. `./mvnw install` to create a jar file, or
`./mvnw spring-boot:run` to run it in place. Or import the projects
into an IDE and run the main methods directly (e.g. right click on the
`IngesterApplication` and `Run As` a `Spring Boot Application` (in
[Spring Tool Suite](https://spring.io/tools)). For general orientation
on how to build and run Spring applications, please refer to the
[Getting Started Guides](https://spring.io/guides), for example
[Getting Started with Spring Boot](https://spring.io/guides/gs/spring-boot/).

All the streaming apps need to bind to RabbitMQ. Running locally you
can do that with `docker-compose` on Linux (just type `docker-compose
up` on the command line), or with `docker-machine` on Mac or Windows
using the same `docker-compose.yml`. If you use `docker-machine` the
result will be RabbitMQ running in a VM or on an external platform,
and the `spring.rabbitmq.host` will need to be set, or else you could
set up an ssh tunnel for ports 5672 and 15672 to that host. On Cloud
Foundry, just create a service of type RabbitMQ and bind to it.
