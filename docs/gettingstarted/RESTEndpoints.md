Create REST Endpoints
=====================

When we set up our Maven dependencies, we included the `AviatorRESTServer` component, which contains everything you need to host REST and Websocket APIs in an Aviator Application without any additional dependencies (no need for JEE or Spring Boot).  For our application, we'll want to set up two REST endpoints:  one to retrieve the list of cats/owners, and another to add a new cat/owner pair.  In the [previous step](BusinessLogic.md), we coded the business logic to support those operations.  Now we will code the REST services that provide access to the operations from the outside world.

## "Get Cats" REST Endpoint
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

There are two interesting bits to look at in this implementation.  First, you'll notice that the argument to our API method is `@Suspended final AsyncResponse response`.  Using `@Suspended` allows us to put the request on hold while we wait for asynchronous tasks to complete.  Keep in mind that messages in the pipeline will arrive at "transaction complete" asynchronously, so our API methods has to act accordingly.  

Second, we have a call to the subscriber manager's registerResponder method.  The subscriber manager relates a specific request to a specific message.  As that message moves through the pipeline, the framework will use the information in the subscriber manager to route the correct response from the application to the correct response in the REST API.

## "Get Cats" Subscriber
In [Planning the Implementation](Planning.md), we learned that every message handled by the pipeline ultimately ends up at the "transaction complete" stage.  We will want to hook into that stage to return the final status of each request.  When we code our subscribers, we use the again use the subscriber manager to retrieve the suspended request from our API method, and route our response to that request.  Aviator includes a class called AviatorSubscriberBase that implements the subscriber manager lookup for us, we only need extend it for our implemnetation.

In Eclipse, create a class called CatPeopleResponders in com.organization.catpeople.  Paste the following method into your new class:
```java
@AviatorSubscriber(
		namespace=CatPeopleTransactionTypes.NAMESPACE,
		transactionType=CatPeopleTransactionTypes.GET_CATS,
		events= {ReportingEvents.transactionComplete})
public void getCatsTransactionCompleted(AviatorNotification<ArrayList<CatOwner>> notification) {
	AsyncResponse responder = this.getResponder(notification);
	if (responder != null) {
		responder.resume(notification);
	}
}
```
Let's have a look at how this method works.  First, we have an `@AviatorSubscriber` annotation.  This annotation is similar to the `@AviatorHandler` annotation we used when we built our transaction handlers.  An `@AviatorHandler` annotation is used to identify business logic which runs at a particular stage (or stages), while an `@AviatorSubscriber` annotation is used to identify logic which reports status or results at a particular stage (or stages).  In this case, our `@AviatorSubscriber` anotation tells Aviator that this method should be called for our "get cats" transaction type when the "transaction complete" stage has finished running.  

Instead of receiving an AviatorMessage argument like in a handler, we get an AviatorNotification, which includes information about what happened to the transaction during processing.  We're going to have our responder just return the entire notification back to the calling application, so all we need to do is invoke `this.getResponder()` to look up the suspended request that goes with this notification and `resume()` it, which returns our notification to the calling application.  Note that you are free to return any kind of response that your application needs from your subscribers.  You do not have to return the entire notification, or even anything at all.  

One final thing to observe is that we give the generic AviatorNotification a type in our method declaration.  This is the same type that we return from the handler we set up for this transaction in the [Develop the Application's Business Logic](BusinessLogic.md) section.  When Aviator invokes our subscriber, it will inject the result from our handler into the notification passed to the subscriber.  We can then access that data from inside the subscriber if, for example, we want to transform the response before returning it.

## "Add Cats" REST Endpoint
Our implementation for "add cat" differs in two ways.  First, we're creating a message that represents an "add cat" transaction, not a "get cats" transaction.  More importantly though, in this case we create an AviatorMessage instance with the requested cat/owner pair as its payload.  When this message arrives at the "execute consensus" handler we coded in the [previous step](BusinessLogic.md), we can access that data from inside the handler.

Our "add cats" REST method looks like this:
```java
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
```

## "Add Cat" Subscriber
Just as with our "get cats" subscriber, we're going to return the entire notification from our responder to the calling application.  Our implementation is very similar, with the differences being the transaction type targeted by the `@AviatorSubscriber` annotation, and the data type encapsulated in the `AviatorNotification` instance passed into the method.

Our "add cat" subscriber looks like this:
```java
@AviatorSubscriber(
		namespace=CatPeopleTransactionTypes.NAMESPACE,
		transactionType=CatPeopleTransactionTypes.ADD_CAT,
		events= {ReportingEvents.transactionComplete})
public void addCatTransactionCompleted(AviatorNotification<CatOwner> notification) {
	AsyncResponse responder = this.getResponder(notification);
	if (responder != null) {
		responder.resume(notification);
	}
}
```

## Wrapping Up
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

...and our completed subscribers class looks like this:
```java
package com.organization.catpeople;

import java.util.ArrayList;

import javax.ws.rs.container.AsyncResponse;

import com.txmq.aviator.messaging.AviatorNotification;
import com.txmq.aviator.pipeline.ReportingEvents;
import com.txmq.aviator.pipeline.metadata.AviatorSubscriber;
import com.txmq.aviator.pipeline.subscribers.AviatorSubscriberBase;

import com.organization.catpeople.CatPeopleTransactionTypes;
import com.organization.catpeople.model.CatOwner;

public class CatPeopleResponders extends AviatorSubscriberBase<AsyncResponse> {
	
	@AviatorSubscriber(
			namespace=CatPeopleTransactionTypes.NAMESPACE,
			transactionType=CatPeopleTransactionTypes.GET_CATS,
			events= {ReportingEvents.transactionComplete})
	public void getCatsTransactionCompleted(AviatorNotification<ArrayList<CatOwner>> notification) {
		AsyncResponse responder = this.getResponder(notification);
		if (responder != null) {
			responder.resume(notification);
		}
	}
	
	@AviatorSubscriber(
			namespace=CatPeopleTransactionTypes.NAMESPACE,
			transactionType=CatPeopleTransactionTypes.ADD_CAT,
			events= {ReportingEvents.transactionComplete})
	public void addCatTransactionCompleted(AviatorNotification<CatOwner> notification) {
		AsyncResponse responder = this.getResponder(notification);
		if (responder != null) {
			responder.resume(notification);
		}
	}
}
```

We're almost there!  We have just a little bit of configuration to do, and then we can see our application in action.  Let's move on to [Configuring the Platform using aviator-config.json](Config.md).