Aviator Core Framework for Distributed Ledger Application Development
===================================================

[Aviator](http://aviatordlt.com) is a distributed ledger (DLT) application framework developed by [TxMQ, Inc.](https://txmq.com).  The goal of the Aviator platform is to enable DLT application development using the tools and languages commonly used by enterprise application developers.  Aviator provides a number of features including smart contract engines in multiple languages, data connectors, developer tooling, and DLT network management capabilities.  

The Aviator Core Framework is a Java-based application development framework that makes it easy for Java developers to quickly build applications using blockchain/DLT technologies.  Aviator Core Framework is designed to be modular.  You can mix and match the pieces you need and swap them out with minimal changes required to your application code. It isolates developers from the mechanics of any specific blockchain or DLT mechanics, shortening the learning curve typically associated with learning how to build applications on this technology.

See our [Getting Started](gettingstarted/GettingStarted.md) page to learn how to build applications on Aviator Core Framework.

Goals
-----
* Make it easy for Java developers used to building enterprise applications to integrate with or build on distributed ledgers.
* Make it easy to adapt the platform to an organization's needs.  Your people, your infrastructure, your data store.
* Make it easy to adapt to change, with minimal impact to application code.  Change the underlying consensus model or data storage without having to change your application code.

Features and Benefits
---------------------

Aviator Core Framework implements the following feature set:
- An application programming framework for developing private ledger applications or Hedera AppNets.  The "pipeline" is an event-driven framework for invoking processing logic on transactions at each step in the transaction life cycle.
- Web socket-based messaging for anything that can make use of a web socket.
- REST-based messaging for anything that can make HTTP requests.
- A framework for logging processed transactions to a blockchain-based transaction log (blockchain the data structure, not blockchain the platform)

Aviator Core Framework delivers the following benefits:
- Significantly easier for developers get up to speed than similar environments, (e.g. Hyperledger Fabric)
- Lower risk of platform lock-in.
- Easy to customize and integrate
- Easy to manage and scale using the Aviator platform.

Version 1.2
-----------
Aviator Core Framework 1.2 is the latest release.  It follows Aviator 1.0's lead in further modularizing the framework so that 
the framework's components can be easily included or excluded via Maven, and set up through the configuration file.
Aviator 1.0 moved the CouchDB block logger to its own JAR.  Aviator 1.2 externalizes the remaining framework
components: the REST/WebSocket server, and Swirlds consensus.

This modularization enables ACF 1.2's big new feature:  Aviator Core 1.2 supports Hedera Consensus Services as the underlying consensus mechanism.  What's more, an application written on Aviator Core 1.2 can be run on private Swirlds Hashgraph consensus, the public Hedera Consensus Service consensus, or in a development mode using simulated consensus (similar to Hyperledger Fabric's solo consensus).  Nearly any consensus mechanism could be supported by Aviator in the future, with little to no impact on application code.

A Short History
---------------
The Aviator Core Framework grew out of the Exo framework for developing applications on the Swirlds Hashgraph platform.  Hashgraph is a high-performance asynchronous Byzantine fault tolerant (ABFT) consensus mechanism that enables private ledger applications to achieve the kinds of throughput that previously were only available by sacrificing security for the sake of performance.  The Swirlds SDK provides only the consensus mechanism.  Many challenges - structuring an application's architecture, exposing functionality to the outside world, and persisting data - remain up to the developer to solve for.  Aviator Core provides many of those features to developers, and the Aviator Platform provides even more.

Aviator Core Framework version 1.2 is a milestone release in that it represents the transformation of Aviator Core from a Swirlds Hashgraph development framework to a multi-DLT development framework.  Going forward, our focus will be to develop Aviator as a cross-chain/cross-platform development framework.  It's designed to be easy to plug into any kind of consensus model and to adapt to any organization's environment.

Aviator Core was originally developed to provide the features that Swirlds' SDK lacks that I knew I would need to develop real-world applications.  I'm sure other folks have needs and ideas that aren't on my radar yet, so I would encourage feature requests and development submissions from other DLT developers.  The framework will only improve for everyone as more people get involved.


What's the Difference Between Aviator Core Framework and the Aviator Platform?
------------------------------------------------------------------------------

The Aviator Core Framework is the base framework upon which the Aviator Platform has been built.  Aviator Core is complete enough for developers to build Hashgraph private ledger applications on today.  The Aviator Platform itself consists of three components:  

The "core platform", tentatively called "Navigator", consists of everything that runs on nodes:  smart contracts, data integration components, and a swappable consensus mechanism that will support additional consensus models besides Hashgraph.

A suite of development tools, tentatively called "Runway", which includes code generators for automatically building boilerplate Aviator code from open specifications such as Open API, and tooling for testing and deploying Java business logic and smart contracts.  The long term vision for Runway is that it will be delivered as a suite of plug-ins for popular IDEs like VS Code and Eclipse, or perhaps as an IDE in its own right.

A suite of network management tools, tentatively called "Control Tower", which includes tooling for configuring, deploying, monitoring, and managing Aviator networks.  The long term vision for Control Tower is that it will again be delivered as plugins for popular IDEs, or as a standalone application.

We recognize that there is ambiguity in the terminology between Aviator, Aviator Core Framework, Aviator Core Platform, "the framework", "the platform", and all the rest.  We're working on it :)

We have big plans for Aviator as a platform, but we want to stress that **the Aviator Core Framework is free and open source, and will continue to be free and open source.**  

If your organization is interested in interested in the Aviator Platform, please visit (http://aviatordlt.com) and fill out the contact form to start a conversation!

### Comparison Between Aviator Core Framework and Aviator Platform Components

| Feature | Aviator Core Framework | Aviator Navigator | Aviator Runway | Aviator Control Tower |
| -------                                                               |:-----:|:-----:|:-----:|:-----:|
| Hedera Consensus Service Consensus                                    | *     | *     |       |       |
| Hashgraph Consensus                                                   | *     | *     |       |       |
| Kafka Consensus                                                       |       | *     |       |       |
| Plug-in Consensus Model                                               | *     | *     |       |       |
| &nbsp; | | | |
| Pipeline Architecture                                                 | *     | *     |       |       |
| Event-Driven Transaction Routing                                      | *     | *     |       |       |
| Compiled-in Java Programming Model                                    | *     | *     |       |       |
| Plug-in Smart Contract Engine Model                                   |       | *     |       |       |
| Javascript Smart Contract Engine                                      |       | *     |       |       |
| Java Smart Contract Engine                                            |       | *     |       |       |
| EVM Smart Contract Engine                                             |       | *     |       |       |
| Chaincode Smart Contract Engine                                       |       | *     |       |       |
| Smart Contract Debugging Tools                                        |       | *     |       |       |
| Smart Contract Deployment and Management Tools                        |       |       | *     | *     |
| &nbsp; | | | |
| Plug-In Blockchain Transaction Logger Model                           | *     | *     |       |       |
| CouchDB Block Logger Driver                                           | *     | *     |       |       |
| MongoDB Block Logger Driver                                           | O     | *     |       |       |
| RDBMS (JPA) Block Logger Driver                                       | O     | *     |       |       |
| Block Explorer Tool                                                   |       |       | *     | *     |
| Plug-In Datastore Connector Model                                     |       | *     |       |       |
| CouchDB Datastore Connector Driver                                    |       | *     |       |       |
| MongoDB Datastore Connector Driver                                    |       | *     |       |       |
| RDBMS (JPA) Datastore Connector Driver                                |       | *     |       |       |
| Data Management Tools                                                 |       |       | *     | *     |
| &nbsp; | | | |
| REST Socket APIs                                                      | *     | *     |       |       |
| Binary Socket APIs                                                    | O     | *     |       |       |
| Web Socket APIs                                                       | *     | *     |       |       |
| OpenAPI Code Generators for Aviator                                   |       |       | *     |       |
| &nbsp; | | | |
| PKI-Based Security Components                                         |       | *     | *     | *     |
| Integration with LDAP and Active Directory                            |       | *     | *     | *     |
| &nbsp; | | | |
| Visual Network Design and Monitoring Tool                             |       |       |       | *     |
| Network Design Scripting Language                                     |       |       |       | *     |
| "One Click" Network Deployment and Scaling                            |       |       |       | *     |
| Monitoring Plug-Ins for Popular Enterprise Network Management Tools   |       |       |       | *     |
| Data Integrity Monitoring and Management                              |       |       | *     | *     |
| Network Security and PKI Management                                   |       |       | *     | *     |
| Smart Contract Management                                             |       |       |       | *     |
| Multi-Headed SSH/SFTP                                                 |       |       | *     | *     |

| Legend | |
|---|---|
| * | Included |
| O | Coming Soon |
|  | Not Included | 

### More Information
There's more to learn about building Aviator applications.  First, check out the (Aviator Zoo Demo Application)[https://github.com/txmq/aviator-zoo-demo] and see how the demo is put together.  It illustrates all of the concepts currently supported by Exo, and is dockerized so you can easily run the application stack.

When you're ready to dive into development, check the [docs folder](docs/README.md) for additional README files that describe how to use each feature in more detail.

If your organization is interested in interested in the Aviator Platform, please visit (http://aviatordlt.com) and fill out the contact form to start a conversation!