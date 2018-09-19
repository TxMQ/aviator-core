package com.txmq.aviator.messaging;

import com.txmq.aviator.messaging.annotations.TransactionType;
import com.txmq.aviator.messaging.annotations.TransactionTypes;

@TransactionTypes(namespace=AviatorCoreTransactionTypes.NAMESPACE, onlyAnnotatedValues=true)
public class AviatorCoreTransactionTypes {
	public static final String NAMESPACE = "AviatorCoreTransactionTypes";
	@TransactionType
	public static final String ACKNOWLEDGE = "ACKNOWLEDGE"; 
	
	@TransactionType
	public static final String ANNOUNCE_NODE = "ANNOUNCE_NODE";
	
	@TransactionType
	public static final String LIST_ENDPOINTS = "LIST_ENDPOINTS";
	
	@TransactionType
	public static final String RECOVER_STATE = "RECOVER_STATE";
	
	@TransactionType
	public static final String SHUTDOWN = "SHUTDOWN";
	
	@TransactionType
	public static final String GET_TRANSACTION_TYPES = "GET_TRANSACTION_TYPES"; 
}
