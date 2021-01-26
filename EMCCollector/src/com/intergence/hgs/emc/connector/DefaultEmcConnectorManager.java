package com.intergence.hgs.emc.connector;

import com.emc.celerra.api.connector.client.*;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.intergence.hgs.emc.config.EmcCollectorProperties;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.concurrent.*;

/**
 * Manage access to the EMC system, providing a simple threading model.
 *
 * @author stephen
 */
public class DefaultEmcConnectorManager implements EmcConnectorManager {

	private final Logger log = Logger.getLogger(this.getClass());

	private final ThreadLocal<CelerraConnector> connectorThreadLocal = new ThreadLocal<CelerraConnector>() {
		protected CelerraConnector initialValue() {
			try {
				CelerraConnector connector = new CelerraConnector(
						EmcCollectorProperties.getInstance().getHost(),
						EmcCollectorProperties.getInstance().getPort(),
						EmcCollectorProperties.getInstance().getProxyHost(),
						EmcCollectorProperties.getInstance().getProxyPort(),
						EmcCollectorProperties.getInstance().getUsername(),
						EmcCollectorProperties.getInstance().getPassword());
				connector.listen(new LoggingListener());
				return connector;
			} catch (IOException e) {
				log.error("", e);
			} catch (CelerraAuthenticationException e) {
				log.error("", e);
			}

			return null;
		}
	};
	private final ExecutorService executor;

	protected DefaultEmcConnectorManager() {
		// Build using EmcConnectorManagerFactory
		int threadCount = EmcCollectorProperties.getInstance().getThreadCount();

		log.info("Building thread pool size [" + threadCount + "] for accessing EMC System");

		ThreadFactory threadFactory = new ThreadFactoryBuilder()
				.setNameFormat("EmcConnectorManager-%d")
				.setDaemon(true)
				.build();
		executor = new ThreadPoolExecutor(threadCount, threadCount,
											0L, TimeUnit.MILLISECONDS,
											new LinkedBlockingQueue<Runnable>(),
											threadFactory);
	}

	@Override
	public String makeCall(final String request) throws RuntimeException {
		try {
			Callable<CelerraResponse> task = buildCallTask(request);
			Future<CelerraResponse> taskFuture= executor.submit(task);
			CelerraResponse celerraResponse = taskFuture.get(EmcCollectorProperties.getInstance().getTimeoutSeconds(), TimeUnit.SECONDS);
			// TODO handle response code encoded in CelerraResponse
			String response = new String(celerraResponse.getContent());
			if (log.isTraceEnabled()) {
				log.trace(response);
			}
			return response;
		} catch (InterruptedException e) {
			log.error("", e);
			throw new RuntimeException("InterruptedException while making Celerra call", e);
		} catch (ExecutionException e) {
			log.error("", e);
			throw new RuntimeException("InterruptedException while making Celerra call", e);
		} catch (TimeoutException e) {
			log.error("", e);
			throw new RuntimeException("InterruptedException while making Celerra call", e);
		}
	}

	private Callable<CelerraResponse> buildCallTask(final String request) {
		return new Callable<CelerraResponse>() {
				@Override
				public CelerraResponse call() throws Exception {
					CelerraConnector connector = connectorThreadLocal.get();
					CelerraResponse response = connector.call(request);
					return response;
				}
			};
	}

	class LoggingListener implements CelerraIndicationListener {

		@Override
		public void processIndication(CelerraIndication ind) {
			log.info("processIndication [" + ind.getTransportSequenceNumber() + "], [" + new String(ind.getContent()) + "]");
		}

		@Override
		public void connectionTerminated(String message) {
			log.info("connectionTerminated [" + message + "]");

		}
	}

}
