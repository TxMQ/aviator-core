package com.txmq.aviator.core;

import java.io.IOException;
import java.io.Serializable;

import com.txmq.aviator.messaging.AviatorMessage;

/**
 * Specifies a method signature for creating transactions.  This interface is applied 
 * as a check to extensions of AviatorBase that implement Aviator for various consensus 
 * mechanisms to ensure that they implement the required createTransaction method.
 * 
 * This may go away some day if we can get away from a static AviatorBase.
 * 
 * @author craigdrabik
 *
 */
public interface IAviator {
	
	/**
	 * Returns an instance of the application state.
	 * 
	 * @return
	 */
	public AviatorStateBase getStateImpl();
	
	/**
	 * Submits a transaction.  The implementation of this method will be specific to 
	 * underlying consensus mechanism that the extension of AviatorBase is implementing.
	 * 
	 * @param transaction
	 * @throws IOException
	 */
	public void createTransactionImpl(AviatorMessage<? extends Serializable> transaction) throws IOException;
	
	/**
	 * Platform-specific mechanism for retrieving the base port.  
	 * This is effectively only for Swirlds, as far as I know.
	 * 
	 * You can hard-code this for other environments.
	 * 
	 * TODO:  Try to eliminate this somehow?  It's only a thing for 
	 * environments that spool up multiple nodes on the same machine
	 * @return
	 */
	public int getBasePortImpl();
	
	/**
	 * Platform-specific mechanism for retrieving the node name.
	 * This is effectively only for Swirlds when running multiple
	 * nodes in the same JVM
	 * @return
	 */
	public String getNodeNameImpl();
}
