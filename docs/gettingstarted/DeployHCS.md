Deploying to Hedera Consensus Service
=====================================

Aviator's primary goal is to provide an application development model that isolates developers from the mechanics of the underlying consensus mechanism.  You don't write an Ethereum Dapp or Hyperledger Fabric application or a Hedera Consensus Service application.  You write an Aviator application that runs on your chosen consensus mechanism.  It isn't possible to accommodate every nuance of every distributed ledger platform, but we strive to minimize the impact of the consensus mechanism on your application's codebase.

You might expect that moving from a test consensus mechanism to a real consensus mechanism like Hedera Consensus Service (HCS) would be complicated, but in Aviator it isn't.  We need to make only three changes - one in our pom.xml file, one in our application's main program, and one in our aviator-config.json fle.

First, we need to add the Hedera Consensus Service plug-in jar to our pom.xml file.  Open your pom and add the AviatorCoreHCS dependency to the dependencies section:
```xml
  <dependencies>
	<dependency>
		<groupId>com.txmq.aviator</groupId>
		<artifactId>AviatorCore</artifactId>
	</dependency>
	<dependency>
		<groupId>com.txmq.aviator</groupId>
		<artifactId>AviatorBlockLoggerCouchDB</artifactId>
	</dependency> 
	<dependency>
		<groupId>com.txmq.aviator</groupId>
		<artifactId>AviatorRestServer</artifactId>
	</dependency>
	<!-- Add this dependency to use HCS for consensus -->
	<dependency>
		<groupId>com.txmq.aviator</groupId>
		<artifactId>AviatorCoreHCS</artifactId>
	</dependency>
  </dependencies>
```

Right-click on your pom.xml file and select Run As -> Maven install to ask Maven to download the new dependency jar.

Next, let's look at our main program.

```java
import com.organization.catpeople.state.CatPeopleState;
import com.txmq.aviator.core.Aviator;
import com.txmq.aviator.core.AviatorTestConsensus;

public class CatPeople {

	/**
	 * All we need to do here is configure our copy 
	 * of the shared state and the core framework.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			AviatorTestConsensus consensus = new AviatorTestConsensus();
			consensus.initState(CatPeopleState.class);
			Aviator.init(consensus);
		} catch (ReflectiveOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
```

Here, we need to change the type of consensus plug-in we instantiate.  We also need to add to our catch block in order to catch exceptions of the type `com.hedera.HederaStatusException`.

```
import com.hedera.hashgraph.sdk.HederaStatusException;
import com.organization.catpeople.state.CatPeopleState;
import com.txmq.aviator.core.Aviator;
import com.txmq.aviator.core.hcs.AviatorHCSConsensus;

public class CatPeople {

	/**
	 * All we need to do here is configure our copy 
	 * of the shared state and the core framework.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			//AviatorTestConsensus consensus = new AviatorTestConsensus();
			AviatorHCSConsensus consensus = new AviatorHCSConsensus();
			consensus.initState(CatPeopleState.class);
			Aviator.init(consensus);
		} catch (ReflectiveOperationException | HederaStatusException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
```
That's it for code changes.  We do need to add to our aviator-config.json file in order to configure the HCS consensus plug-in.  The plug-in requires a Hedera account ID and private key to sign and pay for transactions submitted through HCS.  It also needs a mirror node streaming API to listen for our transactions.  Finally, we have a couple additional properties that tell the plug-in whether it's connecting to testnet or mainnet, whether it should create a new topic on launch, and what topic ID it should connect to if not.

Add the following block to your aviator-config.json file, making sure you fill out your own account ID and key in your aviator-config.json file:
```json
"hcs": {
    "useMainnet": false,
    "mirrorNodeAddress": "api.testnet.kabuto.sh:50211",
    "operatorID": "<-- Your Testnet ID -->",
    "operatorKey": "<-- Your Testnet Private Key -->",
    "createTopic": true,
    "hcsTopicID": null
},
```    

Your completed aviator-config.json file will look like this:
```json
{
    "transactionProcessors": [
		"com.organization.catpeople"
	],
	"rest": {
        "port": 8888,
        "derivedPort": 0,
        "handlers": [
			"com.organization.catpeople"
        ]
    },
    "hcs": {
    	"useMainnet": false,
    	"mirrorNodeAddress": "api.testnet.kabuto.sh:50211",
    	"operatorID": "<-- Your Testnet ID -->",
    	"operatorKey": "<-- Your Testnet Private Key -->",
    	"createTopic": true,
    	"hcsTopicID": null
    },
    "blockLoggers": [
    	{
			"loggerClass": "com.txmq.aviator.blocklogger.couchdb.CouchDBBlockLogger",
			"parameters": [
				{ "key": "databaseName", "value": "catpeople"},
				{ "key": "useAsPrefix", "value": "false"},
				{ "key": "protocol", "value": "http"},
				{ "key": "host", "value": "localhost"},
				{ "key": "port", "value": "5984"},
				{ "key": "blockSize", "value": 5},
				{ "key": "createDb", "value": "true"}
			]
		}
	]
}
```

>Note that embedding your private key in a plain-text configuration file isn't a best practice!  Aviator will soon add support for keeping your key in a secured keystore.

Those are the only changes required to move from an application running Aviator's simulated "test" consensus and Hedera Consensus Service.

Now, we can run our application again, and try our tests.  Using cURL, let's add Karen's cat:

cURL:
```
curl --header "Content-Type: application/json" \
--request POST \
--data '{"cat":"Oliver", "owner":"Karen"}' \
http://localhost:8888/catpeople/cats

...

{
    "transactionType": {
        "value": 12493411,
        "ns": -1024444599
    },
    "payload": null,
    "uuid": "197a2f5f-68a4-4810-a394-09d7906c72f2",
    "interrupted": false,
    "triggeringMessage": {
        "transactionType": {
            "value": 12493411,
            "ns": -1024444599
        },
        "payload": {
            "cat": "Oliver",
            "owner": "Karen"
        },
        "uuid": "72e0aa91-2c8a-4e13-9e5b-44e7294e2cd0",
        "interrupted": false
    },
    "event": "transactionComplete",
    "status": "COMPLETED",
    "nodeName": null
}
```

You'll notice that the request takes much longer to execute.  This is due to the latency introduced by routing the transaction through Hedera Consensus Service for ordering.

Congratulations!  You've built your first application on Hedera Consensus Service!

Next: [Closing Thoughts](Closing.md)