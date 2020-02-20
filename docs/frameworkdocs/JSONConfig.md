Configuring Aviator Using a Configuration File
==========================================

You can configure Aviator for your application using a configuration file in JSON format.  All aspects of Aviator can be configured using JSON.  Although you can configure Aviator in code, there are a number of advantages to using a configuration file.  The biggest advantage to doing so is that you can easily use different configuration files for different deployment options, e.g. local, docker, development, production, etc.  

The move to Aviator from Exo has also introduced improvements in framework modularization, and those benefits can be leveraged in your applications as well.  Aviator offers startup and shutdown hooks, and the ability to extend aviator-config.json with configuration parameters specific to your application

## Initializing the Framework Using a Configuration File

Aviator 1.2 has deprecated programmatic configuration.  All platform configuration is now done through the aviator-config.json file.

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

One of the big changes to Aviator 1.2 under the hood is a move to a much more flexible model for including and configuring platform components at startup.  For example, in previous releases the REST/Websocket feature was baked into the platform.  You always had it, whether you wanted it or not.  Now, you include the REST server as a JAR dependency and configure it through aviator-config.json.  If your application doesn't need this functionality or you want to use a different approach, you can.  This model allows us to add new components to the platform without making changes to the core framework, and developers include only what they need.

The impact to aviator-config.json is that each component can define its own syntax for configuration, which the core framework only discovers at startup.  This document provides a high-level overview of basic configuration for common components, but isn't an exhaustive list of all of the possible parameters in an aviator-config.json file.

Please see the documentation for a particular component to learn how to configure it using aviator-config.json.

## Configuring Transaction Processors
You can configure automatic transaction routing to processors by setting the "transactionProcessors" property.  Pass an array of package names containing transaction processors for Aviator to scan:
```json
"transactionProcessors": [
  "com.txmq.aviator.messaging.rest",
  "com.txmq.socketdemo.transactions"
]
```

See the (Pipeline)[Pipeline.md] documentation for more information on transaction routing.

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
If no REST configuration is defined, then REST will be disabled.

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
