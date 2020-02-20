What's Changed In the Aviator Core Framework
============================================

## Version 1.2

### Refactored for Modularity
The core framework has been extensively refactored under the hood for better modularity.  Aviator was originally developed 
as a framework for developing Swirlds Hashgraph private ledger applications and contained a lot of functionality that was
oriented towards enabling Swirlds development.  The goal of Aviator has always been to deliver a platform that makes it 
easy for developers to build applications that use DLT without specialized knowledge or platform lock-in.  To that end, 
version 1.2 of the framework removes any framework dependency on Swirlds in the core framework code.  We have also refactored
the other components - REST, Websocket, and block logging - into their own libraries as well.

For most developers, there are only two places where you'll need to accommodate the refactor in your application code.  First, you'll need to update your project's pom.xml file to take into account that 1.2 packages each feature into a separate jar.

1.0 (abbreviated for readability)
```xml

<repositories>
    <repository>
        <id>aviator-core</id>
        <name>TxMQ Aviator Core Public Repository</name>
        <url>https://nexus.txmq.com:8080/repository/aviator-core/</url>
    </repository>
</repositories>
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>com.txmq.aviator</groupId>
            <artifactId>aviator-core-bom</artifactId>
            <version>1.2.0</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
    </dependencyManagement>
    <dependencies>
		<dependency>
			<groupId>com.txmq.aviator</groupId>
			<artifactId>AviatorCore</artifactId>
		</dependency>
		<dependency>
			<groupId>com.txmq.aviator</groupId>
			<artifactId>CouchDBBlockLogger</artifactId>
		</dependency>
```
1.2 (abbreviated for readability)
```xml
<repositories>
    <repository>
        <id>aviator-core</id>
        <name>TxMQ Aviator Core Public Repository</name>
        <url>https://nexus.txmq.com:8080/repository/aviator-core/</url>
    </repository>
</repositories>
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>com.txmq.aviator</groupId>
            <artifactId>aviator-core-bom</artifactId>
            <version>1.2.0</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
    </dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>com.txmq.aviator</groupId>
            <artifactId>AviatorCore</artifactId>
        </dependency>
        <dependency>
            <groupId>com.txmq.aviator</groupId>
            <artifactId>AviatorCoreSwirlds</artifactId>
        </dependency>
        <dependency>
            <groupId>com.txmq.aviator</groupId>
            <artifactId>AviatorBlockLoggerCouchDB</artifactId>
        </dependency>
        <dependency>
            <groupId>com.txmq.aviator</groupId>
            <artifactId>AviatorRestServer</artifactId>
        </dependency>
...
```

Note that you only need to include the libraries you're using.  If you don't need block logging or the REST/Websocket server, you can omit those components individually.  Also note the dependency on AviatorCoreSwirlds in the 1.2 pom.  This jar implements an adapter for Aviator Core Framework to Swirlds Hashgraph consensus.  There are adaptors available for Hedera Consensus Services, as well as a simulated consensus mechanism for development purposes.

Your application's main progam will change slightly as well.  In Aviator 1.0, we only had one supported consensus mechanism.  In 1.2 we have three, so your application will have to tell the framework which consensus mechanism to use and how to set it up.  That's going to be platform-specific.  For Swirlds consensus (the only kind in 1.0), the changes are as follows:

1.0
```java
@Override
public void init(Platform platform, long id) {
    this.platform = platform;
    this.selfId = id;
    //this.console = platform.createConsole(true); // create the window, make it visible
    platform.setSleepAfterSync(sleepPeriod);

    try {
        PlatformLocator.initFromConfig(platform);
    } catch (ReflectiveOperationException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }		
}
```

1.2
```java
@Override
public void init(Platform platform, long id) {
    this.platform = platform;
    this.selfId = id;
    //this.console = platform.createConsole(true); // create the window, make it visible
    platform.setSleepAfterSync(sleepPeriod);

    try {
        AviatorSwirlds.init(platform);			
    } catch (ReflectiveOperationException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }		
}
```

This change at first looks like we just renamed PlatformLocator to AviatorSwirlds, but the changes under the hood are much more 
substantial.  The way the framework is boots has been completely refactored to make it easier to attach and initialize platform 
features (including consensus) at runtime and in a way that appears consistent to the developer.  The goal here is to minimize 
platform-specific code in your application so it's easy to swap out components later on without impacting your application.

Such as...

### Hedera Consensus Services Support

Aviator Core Framework 1.2 supports HCS as its consensus mechanism.  HCS is an exciting approach to developing distributed applications
that utilizes the fast, fair, and inexpensive Hedera public ledger to provide consensus as a service to private-ledger style applications.

If you have a private Swirlds application written for Aviator 1.0, and you follow the steps above to make it 1.2 compatible, then you
can run that application with Hedera Consensus Services with two simple changes.

First, swap out the dependency on AviatorCoreSwirlds for AviatorCoreHCS in your pom.xml (abbreviated for clarity):
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
    <dependency>
        <groupId>com.txmq.aviator</groupId>
        <artifactId>AviatorCoreHCS</artifactId>
    </dependency>
</dependencies>
```

You can then simplify your application's main program as you no longer need to implement SwirldsMain:
```java
import com.hedera.hashgraph.sdk.HederaNetworkException;
import com.hedera.hashgraph.sdk.HederaStatusException;
import com.txmq.aviator.core.Aviator;
import com.txmq.aviator.core.hcs.AviatorHCSConsensus;
import com.txmq.exozoodemo.SocketDemoState;

public class ExoZooDemoMain  {
	public static void main(String[] args) {
		try {			
			AviatorHCSConsensus consensus = new AviatorHCSConsensus();
			consensus.initState(SocketDemoState.class);
			Aviator.init(consensus);
			
		} catch (ReflectiveOperationException | HederaNetworkException | HederaStatusException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
}
```

That's not abbreviated - that's the entire main!

Everything else you do in your application, if you've done it correctly, stays the same!

### More Details

For more technical information on Aviator's internal startup and configuration process, see [Framework Startup and Configuration](StartupConfig.md).