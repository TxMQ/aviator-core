Create REST Endpoints
=====================

When we set up our Maven dependencies, we included the `AviatorRESTServer` component, which contains everything you need to host REST and Websocket APIs in an Aviator Application without any additional dependencies (no need for JEE or Spring Boot).  For our application, we'll want to set up two REST endpoints:  one to retrieve the list of cats/owners, and another to add a new cat/owner pair.  In the [previous step](BusinessLogic.md), we coded the business logic to support those operations.  Now we will code the REST services that provide access to the operations from the outside world.

In [Planning the Implementation](Planning.md), we learned that every message handled by the pipeline ultimately ends up at the "transaction complete" stage.  We will want to hook into that stage to return the final status of each request.  Keep in mind that transactions will arrive at "transaction complete" asynchronously - transactions that modify the state are submitted to the consensus mechanism (which takes some amount of time to process) and arrive back at some indeterminate point in the future.  We can accommodate that behavior in our services by using suspended requests and an Aviator utility called the subscriber manager.

Let's implement the API method for querying the list of cats/owners:
```java
@Path("/catpeople")
public class CatPeopleAPI {

	private AviatorSubscriberManager subscriberManager = new AviatorSubscriberManager();
	
	@GET
	@Path("/cats")
	@Produces(MediaType.APPLICATION_JSON)
	public void getCats(@Suspended final AsyncResponse response) {
		AviatorMessage<Serializable> message = new AviatorMessage<>(
				new AviatorTransactionType(CatPeopleTransactionTypes.NAMESPACE, CatPeopleTransactionTypes.GET_CATS),
				null
		);
		
		this.subscriberManager.registerResponder(message, ReportingEvents.transactionComplete, response);
		
		try {
			message.submit();
		} catch (IOException e) {
			response.resume(Response.serverError().entity(e).build());
		}
	}
}
```
This method performs the following tasks:
1. Creates an AviatorMessage with a null payload for a "get cats" transaction.
2. Tells the subscriber manager to route the response from the pipline's "transaction complete" stage back to the calling program.
3. Submits the message into the pipeline

The interesting bit is the call to the subscriber manager's registerResponder method.  At runtime, the framework will use this information to route the correct response from the application to the calling program.

Our implementation for "add cat" differs in two ways.  First, we're creating a message that represents an "add cat" transaction, not a "get cats" transaction.  More importantly though, in this case we create an AviatorMessage instance with the requested cat/owner pair as its payload.  When this message arrives at the "execute consensus" handler we coded in he [previous step](BusinessLogic.md), we can access that data from inside the handler.

Our completed REST API class with both methods defined looks like this:

```java
package com.organization.catpeople;

import java.io.IOException;
import java.io.Serializable;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.organization.catpeople.model.CatOwner;
import com.txmq.aviator.messaging.AviatorMessage;
import com.txmq.aviator.messaging.AviatorTransactionType;
import com.txmq.aviator.pipeline.ReportingEvents;
import com.txmq.aviator.pipeline.subscribers.AviatorSubscriberManager;

@Path("/catpeople")
public class CatPeopleAPI {

	private AviatorSubscriberManager subscriberManager = new AviatorSubscriberManager();
	
	@GET
	@Path("/cats")
	@Produces(MediaType.APPLICATION_JSON)
	public void getCats(@Suspended final AsyncResponse response) {
		AviatorMessage<Serializable> message = new AviatorMessage<>(
				new AviatorTransactionType(CatPeopleTransactionTypes.NAMESPACE, CatPeopleTransactionTypes.GET_CATS),
				null
		);
		
		this.subscriberManager.registerResponder(message, ReportingEvents.transactionComplete, response);
		
		try {
			message.submit();
		} catch (IOException e) {
			response.resume(Response.serverError().entity(e).build());
		}
	}
	
	@POST
	@Path("/cats")
	@Produces(MediaType.APPLICATION_JSON)
	public void addCat(CatOwner data, @Suspended final AsyncResponse response) {
		AviatorMessage<CatOwner> message = new AviatorMessage<>(
				new AviatorTransactionType(CatPeopleTransactionTypes.NAMESPACE, CatPeopleTransactionTypes.ADD_CAT),
				data
		);
		
		this.subscriberManager.registerResponder(message, ReportingEvents.transactionComplete, response);
		
		try {
			message.submit();
		} catch (IOException e) {
			response.resume(Response.serverError().entity(e).build());
		}
	}
}
```

We're almost there!  We have just a little bit of configuration to do, and then we can see our application in action.  Let's move on to [Configuring the Platform using aviator-config.json](Config.md).