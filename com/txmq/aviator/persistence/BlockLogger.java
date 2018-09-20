package com.txmq.aviator.persistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.txmq.aviator.messaging.AviatorMessage;

/**
 * BlockLogger is the manager class for Exo's "low rent" blockchain transaction
 * logging.  It encloses and provides access to pseudo-singleton loggers, and 
 * includes an addTransaction() utility method so code that wants to log data 
 * can do so right from the locator.
 */
public class BlockLogger {
	/**
	 * A collection of logger instances.  I kind of lied when I described the 
	 * logger as a singleton.  Static properties and methods behave strangely 
	 * in Hashgraph applications running the way the demos run.  I suspect 
	 * it's a combination of the nodes all running as children of the same 
	 * root process combined with non-synchronized accessor methods.  The symptom
	 * is that each node winds up creating a logger for itself, but they all wind 
	 * up using a single logger.  This property indexes a node's logger to the node 
	 * name to work around the problem.  It should cause minimal overhead when used 
	 * in a production setting.
	 */
	private Map<String, List<IBlockLogger>> loggers = new HashMap<String, List<IBlockLogger>>();
	
	/**
	 * Adds a logger to a node and makes it available through a static accessor.
	 */
	public void addLogger(IBlockLogger logger, String nodeName) {
		if (!loggers.containsKey(nodeName)) {
			loggers.put(nodeName, new ArrayList<IBlockLogger>());
		}
		loggers.get(nodeName).add(logger);
	}
	
	/**
	 * Retrieves the logger associated with the supplied node name
	 */
	public List<IBlockLogger> getLoggers(String nodeName) {
		if (loggers.containsKey(nodeName)) {
			return loggers.get(nodeName);
		} else {
			return new ArrayList<IBlockLogger>();
		}
		
	}
	
	/**
	 * Utility method that passes a transaction to a node's logger, 
	 * saving a call to getLogger() for the calling code.  If a logger
	 * has not been created for the node, then a null logger will be
	 * automatically registered so execution can procees.
	 */
	public void addTransaction(AviatorMessage<?> transaction, String nodeName) {
		for (IBlockLogger logger : this.getLoggers(nodeName)) {
			logger.addTransaction(transaction);
		}
	}
	
	public void flushLoggers() {
		for (List<IBlockLogger> loggers : this.loggers.values()) {
			for (IBlockLogger logger : loggers) {
				logger.flush();
			}
		}
	}
}
