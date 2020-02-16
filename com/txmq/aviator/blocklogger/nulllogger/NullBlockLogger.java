package com.txmq.aviator.blocklogger.nulllogger;

import org.apache.commons.collections4.keyvalue.DefaultKeyValue;

import com.txmq.aviator.blocklogger.Block;
import com.txmq.aviator.blocklogger.IBlockLogger;
import com.txmq.aviator.messaging.AviatorMessage;

/**
 * A null implementation of a block logger.  Does nothing, but prevents 
 * Exo from falling down if applications don't need logging.
 * @author craigdrabik
 *
 */
public class NullBlockLogger implements IBlockLogger {

	@Override
	public void addTransaction(AviatorMessage<?> transaction) {
		return;
	}

	@Override
	public void save(Block block) {
		return;
	}

	@Override
	public void configure(DefaultKeyValue<String, String>[] parameters) {
		return;
	}

	@Override
	public void flush() {
		return;
	}
}
