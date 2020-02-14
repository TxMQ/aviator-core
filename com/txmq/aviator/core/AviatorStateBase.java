package com.txmq.aviator.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AviatorStateBase {
	/**
	 * Node name that this state belongs to.  Tracked for disambiguation 
	 * purposes when multiple nodes run in the same JVM (e.g. swirlds).
	 */
	protected String myName;

	public String getMyName() {
		return this.myName;
	}
	
	/**
	 * List of endpoints reported through the Endpoints API
	 * 
	 * This kind of shouldn't be here, but I don't see a way around it without some mechanism for adding properties 
	 * to the state through annotation scanning.  Yuck.  At worst though this is unused code if not using REST.
	 */
	protected List<String> endpoints = Collections.synchronizedList(new ArrayList<String>());

	/** @return all the strings received so far from the network */
	public synchronized List<String> getEndpoints() {
		return endpoints;
	}
	
	/**
	 * Public accessor method used by the endpoints API to add available endpoints to the state.
	 */
	public synchronized void addEndpoint(String endpoint) {
		this.endpoints.add(endpoint);
	}

	//TODO: Ideally we don't want cruft from the REST package polluting AviatorStateBase
	/**
	 * This is used in the base framework to make a copy of a state for pre-consensus processing.
	 * @param old
	 */
	public synchronized void copyFrom(AviatorStateBase old) {
		endpoints = Collections.synchronizedList(new ArrayList<String>(old.endpoints));
		myName = old.myName;
	}
}
