Aviator Application Architecture
============================
Aviator Core Framework provides three main features to application developers:
* Request handling
* Transaction processing
* Transaction logging

## Handling Requests and Processing Transactions
Aviator provides three ways for applications to communicate with the Hashgraph:  REST, Web Sockets and Java Sockets.  REST is widely supported by most any language that can communicate over the internet.  Web Sockets enable a more realtime communications model, and allow applications to receive information about requests at each stage in their lifecycle.  Java sockets offer all the same features as web sockets, and are useful in cases where you might want to make use of or integrate the Hashgraph with existing Java code, such as an existing JAX-RS API or a Java client application.

Aviator allows developers to expose REST and web socket endpoints on their Hashgraph nodes.  Aviator's REST feature uses standard JAX-RS annotations.  Web socket support is built on Grizzly web sockets.  The Grizzly web server included in Java Standard Edition is used in either case.  There are no dependencies on Java Enterprise Edition and you don't have to run in an application server or container.  You annotate your handler methods like you would in any other JAX-RS system, and Aviator will scan for those methods and register them with Grizzly.  Web socket support is even easier - Aviator exposes a web socket listener and automatically routes incoming messages to their handlers.

Avoatpr also allows developers to easily communicate with other applications via Java Sockets.  Sockets are TLS-encrypted and client applications are authenticated using certificates.  Aviator provides a generic message class that is passed back and forth over the socket connection and annotations for routing messages to handlers or the state based on transaction type.

Exo 2 introduced the Pipeline - a consistent lifecycle that all transactions move through regardless of how they originate or what they do.  The pipeline allows developers to add code that executes at a certain pipeline stage for a certain transaction type, which results in code that is more flexible, reusable, and maintainable.  Aviator Core Framework is built on the pipeline model.

See [Exo Application Architecture and the Pipeline](Pipeline.md) for more information about developing Aviator applications.

## Logging Transactions
Aviator provides a logging framework for writing transactions out to a persistent history.  The logger writes transactions to a blockchain data structure.  It uses a plug-in architecture to enable developers to add plug-ins for their choice of data storage.  Aviator provides a plug-in for CouchDB (more adaptors coming soon).

See [Transaction Logging](TransactionLogging.md) to learn how to set up logging. 

## JSON Configuration
Aviator is configured using a JSON-formatted configuration file.  See [Configuration using aviator-config.json](JSONConfig.md) to learn how to configure Aviator using the configuration file.

## Test Mode
Aviator supports a test mode for use with JUnit tests that test application logic.  In a typical application, there will be dependencies between components that live in the state.  When in test mode, Aviator will run without dependencies on block logging or the Swirlds Platform class.  It maintains a dummy state, and transactions submitted through PlatformLocator.createTransaction() will be processed by the dummy state.
