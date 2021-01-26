package com.intergence.hgsrest.emc.connector;

import com.emc.celerra.api.connector.client.*;
import com.google.common.base.Strings;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.concurrent.*;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * Manage access to the EMC system, providing a simple threading model.
 *
 * @author stephen
 */
public class DefaultEmcConnectorManager implements EmcConnectorManager {

	private final Logger log = Logger.getLogger(this.getClass());

	private String host;
	private int port;
	private String proxyHost;
	private int proxyPort;
	private String username;
	private String password;

	private int threadCount;

	private int timeoutSeconds;

	private ThreadLocal<CelerraConnector> connectorThreadLocal;
	private ExecutorService executor;

	public void init() {
		log.info("Initialising EMC System logging");
		com.emc.celerra.api.connector.util.Logger.init();

		log.info("Building thread pool size [" + threadCount + "] for accessing EMC System");

		checkState(executor == null, "Only call init() once");
		checkState(connectorThreadLocal == null, "Only call init() once");
		checkState(threadCount > 0, "ThreadCount should be greater than 0. Currently [{}]", threadCount);
		checkState(timeoutSeconds > 0, "Timeout should be greater than 0. Currently [{}]", timeoutSeconds);

		connectorThreadLocal = new ThreadLocal<CelerraConnector>() {
			protected CelerraConnector initialValue() {
				checkNotNull(host);
				checkState(port > 0);
				try {
					CelerraConnector connector = new CelerraConnector(
							host, port,
							proxyHost, proxyPort,
							username, password);
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
		checkNotNull(executor, "Call init() before making a call");

		try {
			Callable<CelerraResponse> task = buildCallTask(request);
			Future<CelerraResponse> taskFuture= executor.submit(task);
			CelerraResponse celerraResponse = taskFuture.get(timeoutSeconds, TimeUnit.SECONDS);
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

	public void setHost(String host) {
		this.host = Strings.emptyToNull(host);
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setProxyHost(String proxyHost) {
		this.proxyHost = Strings.emptyToNull(proxyHost);
	}

	public void setProxyPort(int proxyPort) {
		this.proxyPort = proxyPort;
	}

	public void setUsername(String username) {
		this.username = Strings.emptyToNull(username);
	}

	public void setPassword(String password) {
		this.password = Strings.emptyToNull(password);
	}

	public void setThreadCount(int threadCount) {
		this.threadCount = threadCount;
	}

	public void setTimeoutSeconds(int timeoutSeconds) {
		this.timeoutSeconds = timeoutSeconds;
	}
}
