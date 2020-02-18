Configuring Application Transaction Types
=========================================

In an Aviator application, each transaction submitted for consensus processing is tagged with a transaction type.  That transaction type is used to route the transaction to the business logic that processes it at the proper point in the pipeline.  Exo applications used a single "pseudo-enum" class to capture all of the transaction types for an application.  That approach had a significant shortcoming in that it could only be "extended" once.  A base set of transaction types internal to the platform was augmented with application-specific transaction types when the platform was initialized.  This approach to configuration made the framework somewhat monolithic, which was a challenge we needed to overcome for the development of the Aviator platform.  Additionally, the approach taken in Exo was very heavy when serialized, resulting in bloated payloads both over REST to clients and internally within the Hashgraph.

## Transaction Types in Aviator

Aviator replaces the Exo-style transaction type class with a metadata-based approach to identifying transaction type values.  Like in Exo, Aviator transaction types are strings, but the way they are defined, referenced, and serialized has changed.  Aviator transaction types are namespaced.  Namespaces serve two purposes:  They disambiguate transaction types that might inadvertently define the same key string, and help to modularize the Aviator Platform by ensuring that we don't need all of the possible transaction types supported by the application to be defined in the core framework.  Behind the scenes, the strings that identify the namespaces and transaction types are hashed, and those hashes are what actually get passed around in Aviator messages.  This approach lets developers choose descriptive string values for their transaction types, and the framework boils them down to two ints for serializing.  When using WebSockets to pass AviatorMessage instances to and from the paltform, you can pass either the string or int values.

## Defining Transaction Types

In Aviator, you use @TransactionTypes and @TransactionType metadata to define your application's transaction types.  The best practice is to define a transaction type class with static constants that identify the string values for your application's namespace and transaction types.  

```
@TransactionTypes(namespace=ZooDemoTransactionTypes.NAMESPACE, onlyAnnotatedValues=true)
public class ZooDemoTransactionTypes {
	public static final String NAMESPACE = "ZooDemoTransactionTypes";
	
	@TransactionType
	public static final String GET_ZOO = "GET_ZOO";
	
	@TransactionType
	public static final String ADD_ANIMAL = "ADD_ANIMAL";	
}
```

In the example above, each transaction type value is annotated with @TransactionType.  The namespace that those values belong to is identified by the @TransactionTypes metadata on the class.  Technically, the @TransactionType metadata is optional.  If the "onlyAnnotatedValues" argument is set to false, any public static String property on the class will be treated as a transaction type.  In this case, it's set to true so we can set the value of the namespace on the class as a public static String, without it being included in the list of valid transaction types.

## Using Transaction Types for Transaction Routing

The logic in your application that handles each kind of transaction is annotated with @AviatorHandler.  This annotation identifies the namespace and transaction type values that the annotated method listens for.

```
@AviatorHandler(namespace=ZooDemoTransactionTypes.NAMESPACE,
				transactionType=ZooDemoTransactionTypes.GET_ZOO, 
				events={PlatformEvents.messageReceived})
public Zoo getZoo(AviatorMessage<?> message, SocketDemoState state) {
    Zoo zoo = new Zoo();
    zoo.setLions(state.getLions());
    zoo.setTigers(state.getTigers());
    zoo.setBears(state.getBears());
    
    message.interrupt();
    return zoo;
}
```

In the above example, we use the static strings defined in the ZooDemoTransactionTypes class to indicate that the getZoo method listens for "GET_ZOO" transactions from the "ZooDemoTransactionTypes" namespace.