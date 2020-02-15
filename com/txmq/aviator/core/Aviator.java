package com.txmq.aviator.core;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import com.txmq.aviator.config.AviatorConfig;
import com.txmq.aviator.core.annotations.AviatorShutdown;
import com.txmq.aviator.core.annotations.AviatorStartup;
import com.txmq.aviator.messaging.AviatorMessage;
import com.txmq.aviator.messaging.AviatorTransactionType;
import com.txmq.aviator.pipeline.routers.AviatorPipelineRouter;

public class Aviator {
	
	protected static IAviator implementor;
	
	/**
	 * Pipeline routers
	 */
	protected static Map<String, AviatorPipelineRouter> pipelineRouters = new HashMap<String, AviatorPipelineRouter>();
	
	/**
	 * Places Aviator in test mode, using the passed-in instance of a state. This is
	 * useful for automated testing where you may want to configure an application
	 * state manually and run a series of tests against that known state.
	 */
	public static void enableTestMode(AviatorStateBase state) {
		AviatorTestConsensus impl = new AviatorTestConsensus();
		impl.initState(state);
		implementor = impl;
	}
	
	/**
	 * Places Exo in test mode. Exo will create an instance of the supplied type to
	 * service as a mock state.
	 * 
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public static void enableTestMode(Class<? extends AviatorStateBase> stateClass)
			throws InstantiationException, IllegalAccessException {
		AviatorTestConsensus impl = new AviatorTestConsensus();
		impl.initState(stateClass.newInstance());
		implementor = impl;
	}

	/**
	 * Tests if the application is running in test mode
	 */
	public static boolean isTestMode() {
		return (implementor instanceof AviatorTestConsensus);
	}
	

	/**
	 * Indicates that the node should shut down
	 */
	protected static boolean shouldShutdown = false;

	public static boolean shouldShutdown() {
		return shouldShutdown;
	}

	public static void shutdown() {
		shouldShutdown = true;

		Reflections reflections = new Reflections(
			new ConfigurationBuilder()
				.setUrls(ClasspathHelper.forPackage("com.txmq.aviator"))
				.setScanners(new MethodAnnotationsScanner())
		);
			
		Set<Method> startupMethods = reflections.getMethodsAnnotatedWith(AviatorShutdown.class);
		for (Method method : startupMethods) {
			if (Modifier.isStatic(method.getModifiers())) {
				try {
					method.invoke(null, null);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		// Sleep 60 seconds to make sure we allow time for transactions to finish
		// processing.
		try {
			Thread.sleep(60000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Initializes the platform from an aviator-config.json file located in the same
	 * directory as the application runs in.
	 * 
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public static synchronized void init(Class<? extends IAviator> implementingClass) 
			throws ReflectiveOperationException 
	{
		
		try {
			implementor = implementingClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new IllegalArgumentException("Error configuring Aviator:  " + e.getMessage());
		}
		
		//Load transaction types
		AviatorTransactionType.initialize();
		
		//Initialize the pipeline
		initPipelineRouter((List<String>) AviatorConfig.get("transactionProcessors"));
		
		// Find any static routines annotated with @AviatorStartup and invoke them
		Reflections reflections = new Reflections(
			new ConfigurationBuilder()
				.setUrls(ClasspathHelper.forPackage("com.txmq.aviator"))
				.setScanners(new MethodAnnotationsScanner())
		);

		Set<Method> startupMethods = reflections.getMethodsAnnotatedWith(AviatorStartup.class);
		for (Method method : startupMethods) {
			if (Modifier.isStatic(method.getModifiers())) {
				//TODO:  Might have a problem passing an empty array of arguments.
				System.out.println(method.toString());
				method.invoke(null, null);
			}
		}
	}

	/**
	 * Initializes the pipeline router for this node This is more complicated than
	 * it needs to be to account for multiple nodes running in the same JVM.
	 */
	protected static synchronized void initPipelineRouter(List<String> packages) {
		AviatorPipelineRouter pipelineRouter = new AviatorPipelineRouter();
		pipelineRouter.init(packages);
		String nodeName = implementor.getNodeNameImpl();
		pipelineRouters.put(nodeName, pipelineRouter);
	}

	/**
	 * Accessor for the Exo pipeline router.
	 * 
	 * @see com.txmq.aviator.pipeline.routers.AviatorPipelineRouter
	 */
	public static synchronized AviatorPipelineRouter getPipelineRouter() {
		String nodeName = implementor.getNodeNameImpl();
		return pipelineRouters.get(nodeName);
	}

	/**
	 * Accessor for the Exo pipeline router.
	 * 
	 * @see com.txmq.aviator.pipeline.routers.AviatorPipelineRouter
	 */
	public static synchronized AviatorPipelineRouter getPipelineRouter(String nodeName) {
		return pipelineRouters.get(nodeName);
	}
	
	/**
	 * Accessor for Aviator state. Developers must call init() to initialize 
	 * the locator before calling getState().
	 * 
	 * This method delegates acquiring a copy of state to the consensus implementation.
	 */
	public static AviatorStateBase getState() throws IllegalStateException {
		return implementor.getStateImpl();
	}
	
	public static void createTransaction(AviatorMessage<? extends Serializable> transaction) throws IOException {
		implementor.createTransactionImpl(transaction);
	}
	
	public static int getBasePort() {
		return implementor.getBasePortImpl();
	}
	
	public static String getNodeName() {
		return implementor.getNodeNameImpl();
	}
}
