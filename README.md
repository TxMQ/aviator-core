Aviator Core Framework for Hashgraph Application Development
===================================================

[Aviator](http://aviatordlt.com) is a distributed ledger (DLT) application framework developed by [TxMQ, Inc.].  The goal of the Aviator platform is to enable DLT application development using tools and languages commonly used in enterprise application development.  Aviator provides a number of features including smart contract engines in multiple languages, data connectors, developer tooling, and DLT network management capabilities.  

The Aviator Core Framework grew out of the Exo framework for developing applications on the Swirlds Hashgraph platform.  Hashgraph is a high-performance asynchronous Byzantine fault tolerant (ABFT) consensus mechanism that enables private ledger applications to achieve the kinds of throughput that previously were only available by sacrificing security for the sake of performance.  The Swirlds SDK provides only the consensus mechanism.  Many challenges - structuring an application's architecture, exposing functionality to the outside world, and persisting data for example - remain up to the developer to solve for.  Aviator Core provides many of those features to developers, and the Aviator Platform provides even more.

Aviator Core was developed to provide the features that Swirlds' SDK lacks that I knew I would need to develop real-world applications.  I'm sure other folks have needs and ideasthat aren't on my radar yet, so I would encourage feature requests and development submissions from other Hashgraph developers.  The framework will only improve for everyone as more people get involved.

Features
--------

Aviator Core implements the following feature set:
- Socket-based messaging for Java applications (in-progress)
- Web socket-based messaging for anything that can make use of a web socket.
- REST-based messaging for anything that can make HTTP requests
- An event-driven framework for invoking processing logic on transactions at each step in the transaction life cycle
- A framework for logging processed transactions to a blockchain-based transaction log (blockchain the data structure, not blockchain the platform)

### More Information
There's more to learn about building Aviator applications.  First, check out the (Aviator Zoo Demo Application)[https://github.com/txmq/aviator-zoo-demo] and see how the demo is put together.  It illustrates all of the concepts currently supported by Exo, and is dockerized so you can easily run the application stack.

When you're ready to dive into development, check the [docs folder](docs/README.md) for additional README files that describe how to use each feature in more detail.
