Configuring Aviator using aviator-config.json
=============================================

Aviator is configured using a configuration file called "aviator-config.json", which it expects to find in the same directory that the application executes in.  In Eclipse, create a file called aviator-config.json in the project's root directory by right-clicking on the project and selecting New -> File.  Paste the following JSON into the file:
```json
{
    "transactionProcessors": [
		"com.organization.catpeople",
		"com.txmq.aviator.messaging.rest"
	],
	"rest": {
        "port": 8888,
        "derivedPort": 0,
        "handlers": [
			"com.organization.catpeople"
        ]
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

We can see that there are three sections to the configuration:  

1. transactionProcessors

   This block tells Aviator what packages it should look in at startup to find any pipeline handlers it should configure.  Remember that handler methods are annotated with @AviatorHandler.
2. rest

    This block tells `AviatorRESTServer` what port it should use at startup, and where it should look to find JAX-RS-annotated web service code.
3. blockLoggers
  
    This section configures Aviator's blockchain logging framework.  Thinking back to our initial requirements for the system, we wanted to have a tamper-proof audit trail of transactions accepted by our application.  We can implement this in Aviator without any code.  Specifically, this section includes Aviator's CouchDB block logger implementation, and it includes the properties necessary to connect to the CouchDB server as well as the name of the database that it should use to log transactions.

	Let's review:  We've now defined which transactions our application needs to support.  We've defined our application's data model and shared state.  We've implemented the application's business logic using Aviator handlers.  We've built a REST API so that we can connect to the application from the outside world.  We've written a configuration file that tells Aviator how it's configured and where it should look for the components we've built.

	The final step is to [code our main program and run our application](Main.md).