package com.txmq.aviator.core;

import java.io.IOException;
import java.io.Serializable;
import java.lang.InstantiationException;

import com.txmq.aviator.blocklogger.AviatorBlockLoggerBootstrapper;
import com.txmq.aviator.messaging.AviatorMessage;

/**
 * NOOP consensus mechanism used for test/development mode
 * @author craigdrabik
 *
 */
public class AviatorTestConsensus extends Aviator implements IAviator {



	private AviatorStateBase state;
	
	
	/**
	 * Initializes the consensus mechanism with an initial shared state.
	 *
	 * @param state Initialized state 
	 * @return
	 */
	@Override
	public void initState(AviatorStateBase state) {
		this.state = state;
	}
	
	/**
	 * Initializes the consensus mechanism with an initial shared state from a class.
	 *
	 * @param state State class
	 * @return
	 */
	@Override
	public void initState(Class<? extends AviatorStateBase> stateClass) throws InstantiationException {	 	
		this.state = stateClass.newInstance();
	}
	
	@Override
	public AviatorStateBase getStateImpl() {
		return this.state;
	}

	@Override
	public void createTransactionImpl(AviatorMessage<? extends Serializable> transaction) throws IOException {
		AviatorStateBase preConsensusState = null;

		// Test mode.. Get a pre-consensus copy of the state
		try {
			preConsensusState = (AviatorStateBase) this.state.getClass().getConstructors()[0].newInstance();
		} catch (Exception e) {
			// TODO Better error handling..
			e.printStackTrace();
		}

			preConsensusState.copyFrom((AviatorStateBase) this.state);

		// We have a temporary state constructed. Run the rest of the pipeline.
		// Process message received handlers
		Aviator.getPipelineRouter().routeMessageReceived(transaction, preConsensusState);

		// If the transaction was not interrupted, simulate submission
		if (transaction.isInterrupted() == false) {
			try {
				getPipelineRouter().notifySubmitted(transaction);
				getPipelineRouter(this.state.getMyName()).routeExecutePreConsensus(transaction, preConsensusState);
				getPipelineRouter(this.state.getMyName()).routeExecuteConsensus(transaction, this.state);
				
				//TODO:  Factor this out so logging can be modularized
				if (transaction.isInterrupted()) {
					AviatorBlockLoggerBootstrapper.getBlockLogger()
						.addTransaction(transaction, this.state.getMyName());
				}
			} catch (ReflectiveOperationException e) {
				e.printStackTrace();
			}
		}	
	}
	
	@Override
	public int getBasePortImpl() {
		return 50204;
	}
	
	@Override
	public String getNodeNameImpl() {
		return this.state.getMyName();
	}
}
