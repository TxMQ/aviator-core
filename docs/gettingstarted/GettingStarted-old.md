Getting Started With Aviator Core
=================================

1. Check out the [demo application](https://github.com/txmq/aviator-zoo-demo)
2. Start coding your own Aviator application

  * Make a copy of the demo application and build from there, replacing the demo code with your own custom code -or-
  * Begin from one of the Swirlds demos -or-
  * Start from scratch

## Adding Aviator Core to a Swirlds Demo or a From-Scratch Project

Adding Aviator Core is relatively straightforward.  If you don't plan to modify or extend the framework itself, then the easiest way is to pull include the framework jars via Maven.  The jars can be downloaded from TxMQ's Aviator Core Nexus repository.  All of the framework's jars include source and Javadoc.  

Add TxMQ's Nexus repository to your project POM
```xml
<repositories>
    <repository>
      <id>aviator-core</id>
      <name>TxMQ Aviator Core Public Repository</name>
      <url>https://nexus.txmq.com:8080/repository/aviator-core/</url>
    </repository>
</repositories>
```

We use a Maven bill of materials (BOM) to keep each of the framework jars' versions in sync.  Import the BOM for Aviator Core in your POM:
```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>com.txmq.aviator</groupId>
            <artifactId>aviator-core-bom</artifactId>
            <version>1.0.1</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>		
  	</dependencies>
</dependencyManagement>
```

Then, add the Aviator Core jar dependency to your POM's dependencies section:
```xml
<dependencies>
    <dependency>
        <groupId>com.txmq.aviator</groupId>
        <artifactId>AviatorCore</artifactId>
    </dependency>
...
```

Next, you'll need an [aviator-config.json](JSONConfig.md) file.  The easiest thing to do is copy the [aviator-config.json file from the demo project](https://github.com/TxMQ/aviator-zoo-demo/blob/develop/hashgraph%20workspace/hashgraph/aviator-config.json) and modify it accordingly.  If you include additional framework components, you'll need to add those to your project POM as well.  For example, the demo application uses the CouchDB Block Logger.  It includes the logger's jar in the project POM:
```xml
<dependency>
    <groupId>com.txmq.aviator</groupId>
    <artifactId>CouchDBBlockLogger</artifactId>
</dependency>
```

You only need to include the JAR dependencies for the jars you actually need.

## What if I want the source?
Not a problem.  You can either download/clone/copy the source from github, or use a git subtree to include it in your project.  You will have to take care of your own dependency management in your project's POM.  The dependencies for each component can be derived from it's POM on [TxMQ's Nexus repository](https://nexus.txmq.com:8080/service/rest/repository/browse/aviator-core/com/txmq/aviator/)