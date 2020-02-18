Configuring Aviator Using a Configuration File
==========================================

You can configure Aviator for your application using a configuration file in JSON format.  All aspects of Aviator can be configured using JSON.  Although you can configure Aviator in code, there are a number of advantages to using a configuration file.  The biggest advantage to doing so is that you can easily use different configuration files for different deployment options, e.g. local, docker, development, production, etc.  

The move to Aviator from Exo has also introduced improvements in framework modularization, and those benefits can be leveraged in your applications as well.  Aviator offers startup and shutdown hooks, and the ability to extend aviator-config.json with configuration parameters specific to your application

## Initializing the Framework Using a Configuration File

Initializing Aviator using a configuration file is a one-line operation:

```java
PlatformLocator.initFromConfig(platform, "path/to/aviator-config.json");
```

If your aviator-config.json file is located in the same folder as the Hashgraph application will run in, you can omit the path parameter:
```java
PlatformLocator.initFromConfig(platform);
```
An aviator-config.json file uses the following structure:
```json
{
  "transactionProcessors": [
    "com.txmq.aviator.messaging.rest",
    "com.txmq.socketdemo.transactions"
  ],
  "socketMessaging": {
    "port": -1,
    "derivedPort": 1000,
    "handlers": [
      "com.txmq.socketdemo.socket"
    ]
  },
  "rest": {
    "port": -1,
    "derivedPort": 2000,
    "handlers": [
      "com.txmq.socketdemo.rest"
    ]
  },
  "blockLoggers": [
    {
      "loggerClass": "com.txmq.aviator.persistence.couchdb.CouchDBBlockLogger",
      "parameters": [
        { "key": "databaseName", "value": "zoo-"},
        { "key": "useAsPrefix", "value": "true"},
        { "key": "protocol", "value": "http"},
        { "key": "host", "value": "couchdb"},
        { "key": "port", "value": "5984"},
        { "key": "blockSize", "value": 5},
        { "key": "createDb", "value": "true"}
      ]
    }
  ]
}
```


## Configuring Transaction Processors
You can configure automatic transaction routing to processors by setting the "transactionProcessors" property.  Pass an array of package names containing transaction processors for Aviator to scan:
```json
"transactionProcessors": [
  "com.txmq.aviator.messaging.rest",
  "com.txmq.socketdemo.transactions"
]
```

See the (Pipeline)[Pipeline.md] documentation for more information on transaction routing.

## Configuring Java Socket Messaging
You can configure Aviator's socket messaging feature by defining the port or derived port Exo should listen on, and a list of packages for Exo to search for socket message handlers:
```json
"socketMessaging": {
  "port": -1,
  "derivedPort": 1000,
  "handlers": [
    "com.txmq.socketdemo.socket"
  ]
}
```

In this example, we've asked Aviator to calculate the port it should listen on based on the port the hashgraph node listens on.  This is typical for applications that run on the SDK where the Swirlds browser starts up multiple nodes on the same host.  The above definition will add 1000 to the hashgraph's port to determine which port to listen on.  You can also define a fixed port by setting a value other than -1 in the port property.  If the port is set, then derivedPort will be ignored.
If no socketMessaging object is defined, Aviator will not create a transaction server instance to listen for socket requests.

The example above sets up an unsecured socket - there is no encyption in transit, nor are clients authenticated.  This configuration provides no security whatsoever and should only be used for experimenting with the framework or while troubleshooting.  To set up a TLS-secured socket authenticated using X.509 certificates, we can pass in the locations and passwords for the keystores containing client and server side keys, and Aviator will configure a secured socket:
```json
"socketMessaging": {
    "port": -1,
    "derivedPort": 1000,
    "secured": true,
    "clientKeystore": {
        "path": "client.public",
        "password": "client"
    },
    "serverKeystore": {
        "path": "server.private",
        "password": "server"
    },
    "handlers": [
        "com.txmq.socketdemo.socket"
    ]
}
```

## Configuring REST Endpoints
REST endpoints are configured using the same configuration object format as socket messaging.  Set the port or derivedPort to accept requests on, and a list of packages that contain JAX-RS-annotated request handlers:
```json
"rest": {
  "port": -1,
  "derivedPort": 2000,
  "handlers": [
    "com.txmq.socketdemo.rest"
  ]
}
```
As with socket messaging, if no REST configuration is defined, then REST will be disabled.

## Configuring Block Logging
Block logging can be configured by supplying the logger class and a list of logger-specific parameters in the "blockLoggers" property.  If there is no "blockLoggers" property in the config file, logging will be disabled.  The following example shows how to initialize the CouchDB-based logger included in Aviator Core Framework:

```json
"blockLoggers": [
  {
    "loggerClass": "com.txmq.aviator.persistence.couchdb.CouchDBBlockLogger",
    "parameters": [
      { "key": "databaseName", "value": "zoo-"},
      { "key": "useAsPrefix", "value": "true"},
      { "key": "protocol", "value": "http"},
      { "key": "host", "value": "couchdb"},
      { "key": "port", "value": "5984"},
      { "key": "blockSize", "value": 5},
      { "key": "createDb", "value": "true"}
    ]
  }
]
```
Note that the parameters collection is specific to the logger implementation.  Different loggers will expect different parameters.  See the documentation for your specific logger for information on which parameters it expects.

One important change from Exo is that Aviator supports defining multiple block loggers.  This feature makes it easy for developers to set up chains for specific purposes.  For example, developers can extend block loggers to implement filtering, so that the logger captures only the transactions necessary for your application.  This feature required a change to the configuration file format.  Exo used a "blockLogger" property that was an object type.  Aviator uses a "blockLoggers" property whose type is array.

## Configuring Transaction Types
Aviator uses a different scheme for identifying transaction types than the "pseudo-enum" used in Exo.  It is no longer necessary to configure your transaction type class in the config file.  See [Transaction Types](TransactionTypes.md) for more information.
