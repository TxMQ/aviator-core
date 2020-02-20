Aviator Core Framework REST/Websocket Server
============================================

> There is more documentation to come - please bear with us while we update documentation for Aviator 1.2

The REST/Websocket Feature is a full-featured JAX-RS web service container and Websocket container that uses the Grizzly HTTP server.
Any JAX-RS annotated web service can be hosted by Grizzly, and Grizzly includes support for websockets as well.  The demo applications
use the feature to expose APIs to web clients using both websockets and REST over HTTP.

During the application's startup phase, Aviator will scan any "handler" packages described in aviator-config.json and automatically 
register JAX-RS-annotated endpoints with Grizzly.  No Aviator-specific code or annotations are required.

### How do I configure my application to use the REST/Websocket Server Feature?

1.  Add a dependency to your pom.xml as follows:
```xml
<dependency>
    <groupId>com.txmq.aviator</groupId>
    <artifactId>AviatorRestServer</artifactId>
</dependency>
```
2.  Add a configuration block to aviator-config.json:
```json
"rest": {
        "port": 8888,
        "derivedPort": 0,
        "handlers": [
			"com.txmq.exozoodemo.rest"
        ]
    }
```

See [Configuring Aviator Using a Configuration File](JSONConfig.md) for more information.

### Do I need to use the REST/Websocket Server Feature?

If you're not using Swirlds Hashgraph consensus, then the short answer is "probably not."  The responder classes used in the demo 
applications to track requests so that the framework can provide responses are part of the core framework.  If you want to use these
for example with JEE or Spring Boot, you can without including any additional dependencies.

You probably want to use the REST/Websocket Feature in the following situations:
* You're using Swirlds Hashgraph consensus.  Swirlds is very opinionated about how applications initialize,
and the REST/Websocket Server Feature was designed around these constraints.  
* You might someday want to run an application that uses websockets on Swirlds consensus.  In that case, you'll wind up having to use 
the feature anyways, so you might as well start with that and Grizzly websockets instead of having to port your app back later.
* You just want a quick and easy solution to allowing integration through REST or Web Sockets.


