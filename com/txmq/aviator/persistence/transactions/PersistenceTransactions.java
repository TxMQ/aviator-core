package com.txmq.aviator.persistence.transactions;

import com.txmq.aviator.core.PlatformLocator;
import com.txmq.aviator.core.AviatorState;
import com.txmq.aviator.messaging.AviatorCoreTransactionTypes;
import com.txmq.aviator.messaging.AviatorMessage;
import com.txmq.aviator.pipeline.PlatformEvents;
import com.txmq.aviator.pipeline.metadata.AviatorHandler;

public class PersistenceTransactions {

	@AviatorHandler(	namespace="AviatorCoreTransactionTypes",
					transactionType=AviatorCoreTransactionTypes.SHUTDOWN, 
					events= {PlatformEvents.executeConsensus})
	public AviatorMessage<?> shutdown(AviatorMessage<?> message, AviatorState state, boolean consensus) {
		//If we have a block logger, then ask it to flush to the chain.
		if (PlatformLocator.getBlockLogger() != null) {
			PlatformLocator.shutdown();	
			System.out.println("It is now safe to shut down.");
		}
		
		return message;
	}
	
	@AviatorHandler(	namespace="AviatorCoreTransactionTypes",
					transactionType=AviatorCoreTransactionTypes.RECOVER_STATE, 
					events= {PlatformEvents.executeConsensus})
	public AviatorMessage<?> recoverState(AviatorMessage<?> message, AviatorState state, boolean consensus) {
		return message;
	}
}
