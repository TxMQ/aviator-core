Exo Framework for Hashgraph Application Development
===================================================

Exo is a framework for developing applications on the Swirlds Hashgraph platform.  Primarily, Exo offers features and an application architecture for getting transactions into a Hashgraph, processing those transactions once they've been accepted, and logging transactions out to persistent storage.

Exo was developed to provide the features that Swirlds' SDK lacks that I knew I would need to develop real-world applications.  I'm sure other folks have needs and ideasthat aren't on my radar yet, so I would encourage feature requests and development submissions from other Hashgraph developers.  The framework will only improve for everyone as more people get involved.

Version 2 of the Exo Framework is designed to be more flexible, easier to build on, and offers a better ability to monitor transactions as they move through the processing lifecycle.  

Features
--------

Exo implements the following feature set:
- Socket-based messaging for Java applications (in-progress)
- Web socket-based messaging for anything that can make use of a web socket.
- REST-based messaging for anything that can make HTTP requests
- An event-driven framework for invoking processing logic on transactions at each step in the transaction life cycle
- A framework for logging processed transactions to a blockchain-based transaction log (blockchain the data structure, not blockchain the platform)

Before You Begin
----------------

Start by reading the [How a Swirld Works](SwirldsApplications.md) and [Exo Application Architecture](ApplicationArchitecture.md) documentation.  These two documents will give you a foundation in how a Swirlds application works, the challenges you'll encounter when developing on the Alpha SDK, and how Exo helps you overcome those challenges.

Pardon Our Dust
---------------

We're still working through the documentation for Exo 2.  JSON configuration and transaction logging are holdovers from version 1 of the framework, and there have been some changes.
