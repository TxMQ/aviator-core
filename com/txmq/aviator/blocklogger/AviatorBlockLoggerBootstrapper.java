package com.txmq.aviator.blocklogger;
import java.util.List;

import com.txmq.aviator.config.AviatorConfig;
import com.txmq.aviator.config.model.BlockLoggerConfig;
import com.txmq.aviator.core.Aviator;
import com.txmq.aviator.core.annotations.AviatorShutdown;
import com.txmq.aviator.core.annotations.AviatorStartup;

public class AviatorBlockLoggerBootstrapper {

	
	/**
	 * Reference to the block logging manager
	 */
	protected static BlockLogger blockLogger = new BlockLogger();
	
	@AviatorStartup
	@SuppressWarnings("unchecked")
	public static void startup() {
		if (AviatorConfig.has("blockLoggers") && !Aviator.isTestMode()) {
			for (BlockLoggerConfig loggerConfig : (List<BlockLoggerConfig>) AviatorConfig.get("blockLoggers")) {
				try {
					Class<? extends IBlockLogger> loggerClass = (Class<? extends IBlockLogger>) Class
							.forName(loggerConfig.loggerClass);
					IBlockLogger logger = loggerClass.newInstance();
					logger.configure(loggerConfig.parameters);
					blockLogger.addLogger(logger, Aviator.getState().getMyName());
				} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
					throw new IllegalArgumentException("Error configuring block logger:  " + e.getMessage());
				}
			}
		}
	}
	
	@AviatorShutdown
	public static void shutdown() {
		getBlockLogger().flushLoggers();
	}
	
	/**
	 * Accessor for the block logging manager
	 */
	public static BlockLogger getBlockLogger() {
		return blockLogger;
	}
}
