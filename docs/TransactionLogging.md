Aviator Transaction Logging
=======================

Aviator provides an easy-to-use transaction logger that can automatically log transactions to persistent storage in a blockchain data structure.  The logger groups transactions into blocks and writes those blocks to storage.  Each block is signed with an SHA-256 hash, and the block incorporates the hash of the previous block to maintain data integrity in the chain.

The logger uses a plug-in mechanism to allow the logger to work with any type of data storage.  A CouchDB plugin is provided with more implementations on the way.

## Initializing the logger

### Using aviator-config.json
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

The config file is the recommended method for configuring block logging.

### In Code
To initialize the logger in code, you create an instance of your storage-specific logger plugin and pass it to the logger.  The logger should be initialized in the init() method of your SwirldMain and can be done as part of the platform initialization:

```Java
String[] transactionProcessorPackages = {"com.txmq.aviator.messaging.rest", "com.txmq.socketdemo.transactions"};
CouchDBBlockLogger blockLogger = new CouchDBBlockLogger(
        "zoo-" + platform.getAddress().getSelfName().toLowerCase(),
        "http",
        "couchdb",
        5984);
AviatorPlatformLocator.init(platform, 
                            SocketDemoTransactionTypes.class, 
                            transactionProcessorPackages, 
                            blockLogger);
```

It can also be initialized separately, but should still be done in the init() method of your SwirldMain:
```java
AviatorPlatformLocator.getBlockLogger.setLogger(
    blockLogger, 
    "zoo-" + platform.getAddress().getSelfName().toLowerCase()
);
```

## Building Transaction Logger Plug-ins
If you need to support a logging target other than CouchDB, you can implement your own logger plugin.  Logger plugins implement the IBlockLogger interface.

```java
/**
 * Interface that defines what a storage-specific block logger 
 * has to implement.  Aviator is written to deal with IBlockLoggers, 
 * not concrete instances of storage-specific loggers.
 */
public interface IBlockLogger {

    /**
     * Adds a transaction to the next block
     */
    public void addTransaction(AviatorMessage transaction);
    
    /**
     * Asks the logger to persist a block to storage
     */
    public void save(Block block);
    
}
```

Your plug-in is responsible for managing its internal transaction list and the mechanics for saving the block to whatever storage your plug-in uses.  Note that even though the save method is public, it is not automatically invoked by the logger.  Your plug-in should decide when to invoke the save method.  The CouchDB plugin writes blocks when a block contains a certain number of transactions, but you could implement a timeout-based mechanism or calculate the size of the data in the block to trigger writes, for example.