Build the Application's Shared State
=======================================

In a distributed ledger application we have the concept of a "shared state".  This state is shared among all nodes and is kept synchronized by the network. 

Our first step is to code the data structure that will keep track of everyone's cats.  First, we'll code a POJO (plain old Java object) that represents each cat/owner pair.  In Eclipse, right-click on the src/main/java folder in the package explorer and select New Class.  Name the class "CatOwner", and place it in a package called "com.organization.catpeople.model".  In this class, we need to track the name of the cat and its owner.  We're also going to create a method to make an independent copy of a CatOwner.  We'll talk more about this method shortly.

Our CatOwner model looks like:
```java
package com.organization.catpeople.model;

import java.io.Serializable;

public class CatOwner implements Serializable {
	
	private static final long serialVersionUID = 1243291208426370661L;
	
	public String cat;
	public String owner;
	
	public CatOwner copy() {
		CatOwner copy = new CatOwner();
		copy.cat = new String(this.cat);
		copy.owner = new String(this.owner);
		
		return copy;
	}
}
```

Next, we need a place to store our list of CatOwner instances.  We want our list of cats and their owners to live in the shared state so it's accessible across the network, and only modified according to the rules of the network.  In an Aviator application, we create our application's shared state by extending the framework's AviatorStateBase class.  In Eclipse, create another new class.  Let's call this one "CatPeopleState", and put it in the package "com.organization.catpeople.state".  Before we continue, we want this class to extend AviatorStateBase.  Click the "Browse" button next to the "superclass" field to locate it.  Click Finish.

We need to do two things in our shared state implementation.  First, we need a data structure to store our cats in.  Let's add a private property to hold our cats, and a public accessor to retrieve a reference to the list.  It's important to note that Aviator is a multithreaded environment, so we need to be sure that our accessor is synchronized.  Add the following code:

```java
private List<CatOwner> cats = new ArrayList<>();

public synchronized List<CatOwner> getCats() {
    return cats;
}
```

The second thing we need to do is provide a way to copy our state.  Under the hood, Aviator makes copies of the state in order to execute transactions "pre-consensus".  In other words, Aviator can test to see if a transaction will succeed or fail before the network comes to consensus on the order of that transaction without modifying the shared state.  In order to do that, we need to be able to make a complete copy of the application state in a way that ensures that modifications made to the copy aren't reflected in the instance that was copied. 

We implement our copying mechanism by overriding the copyFrom method on AviatorStateBase, including the code we need to copy our list of cats.  This is where the copy() method we added to our CatOwner class comes in:

```java
@Override
public synchronized void copyFrom(AviatorStateBase old) {
    super.copyFrom(old);
    
    this.cats = ((CatPeopleState) old).getCats()
            .stream()
            .map(co -> co.copy())
            .collect(Collectors.toList());
}
```
Our completed shared state implementation looks like this:
```java
package com.organization.catpeople.state;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.organization.catpeople.model.CatOwner;
import com.txmq.aviator.core.AviatorStateBase;

public class CatPeopleState extends AviatorStateBase {
	private List<CatOwner> cats = new ArrayList<>();

	public synchronized List<CatOwner> getCats() {
		return cats;
	}

	@Override
	public synchronized void copyFrom(AviatorStateBase old) {
		super.copyFrom(old);
		
		this.cats = ((CatPeopleState) old).getCats()
				.stream()
				.map(co -> co.copy())
				.collect(Collectors.toList());
	}
	
}
```

OK, so we have our transaction types identified, and our application's shared state is in place.  Now we need a way to get data in and out of the system.  Let's [create REST endpoints](RESTEndpoints.md) to allow interaction with our shared state from the outside world.