Develop the Application's Business Logic
========================================

In our system, we need to support two operations:
* View the list of cats and their owners registered in the system.
* Claim a cat name.  We want to be sure that each cat name can be claimed by only one owner.

In an Aviator application, we need to control interaction with the shared state in order to avoid threading issues and unauthorized modification of the state.  It is critical that our application is constructed in such a way that the state isn't modified unless the network tells us it's OK.  The pipeline implements the necessary guardrails to ensure that state management is properly handled, as long as you stay within certain guidelines - the "golden rules".

> Rule #1:  Only access the shared state when it has been provided by the pipeline.

Aviator applications work by moving messages (AviatorMessage instances) through the pipeline.  At each stage, Aviator looks for any handlers that have been defined for that stage and transaction type.  When that handler is invoked, Aviator supplies the message and a copy of the application state to the handler.  As long as you only access the state supplied by the framework inside of your handler, you're safe.  Once the handler exits, the framework assumes that no further access or modification will be made to the state.

## Retrieving the List of Cats and Owners
Let's start by implementing the business logic that retrieves the list of cats and their owners from the shared state.  We will need to code a pipeline handler to return the list of cats and their owners.  We will attach this handler to the "message received" pipeline stage, because a request doesn't need to be processed by the entire network if it only reads data from the state.

Let's create a class called "CatPeopleTransactions" in the "com.organization.catpeople" package.  Inside this class, we'll code a handler for the submitted event:

```java
@AviatorHandler(
        namespace=CatPeopleTransactionTypes.NAMESPACE,
        transactionType=CatPeopleTransactionTypes.GET_CATS,
        events= {PlatformEvents.messageReceived}
)
public List<CatOwner> getCats(AviatorMessage<?> message, CatPeopleState state) {
    List<CatOwner> copy = 
            state.getCats().stream().map(co -> co.copy()).collect(Collectors.toList());
    
    message.interrupt();
    return copy;
}
```

The implementation is very simple.  When the handler is invoked, the framework passes in the message that triggered the invokation, and a copy of the state.  
1. Make a copy of the list of cats and their owners from the state.  
2. Interrupt the message.  Recall from [Planning the Implementation](Planning.md) that interrupting tells Aviator that this message should not be processed further.  Aviator will move this message directly to the "transaction complete" stage of the pipeline.
3. Return the list of cats and owners.  

When this method is executed, Aviator will attach the return value (our list of cats/owners) to the message and move it directly to the "transaction complete" stage of the pipeline.

## Adding a Cat/Owner to the Shared State
We need to be careful that we only modify data in the shared state under certain circumstances.  This brings us to our second "golden rule":
>Rule #2: Only modify the state in "execute pre-consensus" and "execute consensus" handlers.

Following this rule ensures that we don't make modifications to the state that aren't reflected in other copies of the state.  In our case, we want to assign cat names to an owner only after consensus, which means that we will code a handler for the "execute consensus" pipeline stage.

```java
@AviatorHandler(
        namespace=CatPeopleTransactionTypes.NAMESPACE,
        transactionType=CatPeopleTransactionTypes.ADD_CAT,
        events= {PlatformEvents.executeConsensus}
)
public void addCat(AviatorMessage<CatOwner> message, CatPeopleState state) {
    //Check to see if the cat name has already been claimed
    Optional<CatOwner> existingRecord = 
            state.getCats().stream().filter(co -> co.cat.equals(message.payload.cat)).findFirst();
    
    if (existingRecord.isPresent()) {
        throw new IllegalStateException(
                existingRecord.get().owner + " already owns a cat named " + existingRecord.get().cat
        );
    } else {
        state.getCats().add(message.payload);
    }
}
```

Here, we first check to see if we already have a cat with that name registered with the system.  If we do find a cat with the requested name, we throw an exception indicating that the transaction has failed.  The pipeline will catch this exception, attach the exception to the message, and move the transaction to the "transaction complete" pipeline stage.  Note that we don't have to explicitly call interrupt() - the framework does that automatically in the case of an exception.  If the cat's name hasn't already been claimed, we add the cat/owner pairing to the shared state.

At this point we have a functioning application.  We've defined our transaction types, set up a shared state, and set up pipeline event handlers to implement our application's business logic.  What we're missing is a way for the outside world to interact with our application.  Next, we'll [create REST endpoints](RESTEndpoints.md) to allow for other applications to interact with ours.