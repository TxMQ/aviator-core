Define Transaction Types
========================

We have two types of transactions that we need to support:  "Add Cat", and "Get Cats."  We need to write a little code that will tell Aviator which transaction types it supports.  We do that by writing a Java class that we annotate with the `@TransactionTypes` annotation.  

In Eclipse, create a new class called CatPeopleTransactionTypes in com.organization.catpeople.

First, we add static string properties to the class that represent each of our two transaction types.  Each of these properties gets annotated with `@TransactionType`:
```java
@TransactionType
public static final String GET_CATS = "GetCats";

@TransactionType
public static final String ADD_CAT = "AddCat";
```

Every transaction type in an Aviator application belongs to a namespace, so that a single Aviator network can support multiple applications.Add another static string property that identifies the namespace that these transactions belong to:

```java
public static final String NAMESPACE = "CatPeopleTransactionTypes";
```

And finally, we annotate the class with `@TransactionTypes`.  Our completed transaction types class looks like this:
```java
package com.organization.catpeople;

import com.txmq.aviator.messaging.annotations.TransactionType;
import com.txmq.aviator.messaging.annotations.TransactionTypes;

@TransactionTypes(namespace=CatPeopleTransactionTypes.NAMESPACE, onlyAnnotatedValues=true)
public class CatPeopleTransactionTypes {
	public static final String NAMESPACE = "CatPeopleTransactionTypes";
	
	@TransactionType
	public static final String GET_CATS = "GetCats";
	
	@TransactionType
	public static final String ADD_CAT = "AddCat";
}

```

When Aviator starts up, it will scan all of the classes it knows about to find classes annotated with `@TransactionType`.  It builds a dictionary of these types and their namespaces.  When we code our pipeline handlers, we will use these transaction type values to tell the framework which transaction types each handler should be invoked for.

Now we're ready to move on to coding our [Application Shared State](SharedState.md).