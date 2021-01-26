package com.intergence.hgs.emc.config;

import com.google.common.base.Strings;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Singleton property access for EMC Collector
 *
 * @author stephen
 */
public class EmcCollectorProperties {

	private final Logger log = Logger.getLogger(this.getClass());

	private static final String CONFIG_PROPERTIES_FILE = "/config.properties";

	public static final String USE_CANNED_DATA = "emc.usecanneddata";

	public static final String EMC_CONNECTION_HOST = "emc.connection.host";
	public static final String EMC_CONNECTION_PORT = "emc.connection.port";
	public static final String EMC_CONNECTION_PROXY_HOST = "emc.connection.proxy.host";
	public static final String EMC_CONNECTION_PROXY_PORT = "emc.connection.proxy.port";
	public static final String EMC_CONNECTION_USERNAME = "emc.connection.username";
	public static final String EMC_CONNECTION_PASSWORD = "emc.connection.password";

	public static final String EMC_MANAGER_THREADCOUNT = "emc.manager.threadcount";
	public static final String EMC_MANAGER_TASK_TIMEOUT_SECONDS = "emc.manager.timeout.seconds";

	private final boolean useCannedData;

	private final String host;
	private final int port;
	private final String proxyHost;
	private final int proxyPort;
	private final String username;
	private final String password;

	private final int threadCount;
	private final int timeoutSeconds;

	private static final EmcCollectorProperties INSTANCE = new EmcCollectorProperties();

	public static EmcCollectorProperties getInstance() {
		return INSTANCE;
	}

	private EmcCollectorProperties()  {
		InputStream resourceAsStream = this.getClass().getResourceAsStream(CONFIG_PROPERTIES_FILE);

		Properties props = new Properties();

		// TODO remove all this default values.
		boolean useCannedData = false;
		String host = "";
		int port = 0;
		String proxyHost = null;
		int proxyPort = 0;
		String username = "";
		String password = "";
		int threadCount = 1;
		int timeoutSeconds = 30;

		if (resourceAsStream == null) {
			log.error("Property file '" + CONFIG_PROPERTIES_FILE + "' not found in the classpath. Using defaults");
		}
		else {
			try {
				props.load(resourceAsStream);
				log.info("Loaded EmcCollectorProperties [" + props + "] ");
				useCannedData = Boolean.parseBoolean(props.getProperty(USE_CANNED_DATA));
				host = Strings.emptyToNull(props.getProperty(EMC_CONNECTION_HOST));
				port = Integer.parseInt(props.getProperty(EMC_CONNECTION_PORT));
				proxyHost = Strings.emptyToNull(props.getProperty(EMC_CONNECTION_PROXY_HOST));
				proxyPort = Integer.parseInt(props.getProperty(EMC_CONNECTION_PROXY_PORT));
				username = Strings.emptyToNull(props.getProperty(EMC_CONNECTION_USERNAME));
				password = Strings.emptyToNull(props.getProperty(EMC_CONNECTION_PASSWORD));
				threadCount = Integer.parseInt(props.getProperty(EMC_MANAGER_THREADCOUNT));
				timeoutSeconds = Integer.parseInt(props.getProperty(EMC_MANAGER_TASK_TIMEOUT_SECONDS));
				log.info("Done loading props");
			} catch (IOException e) {
				log.error("Error loading property file " + CONFIG_PROPERTIES_FILE + "]", e);
			}
		}

		this.useCannedData = useCannedData;
		this.host = host;
		this.port = port;
		this.proxyHost = proxyHost;
		this.proxyPort = proxyPort;
		this.username = username;
		this.password = password;
		this.threadCount = threadCount;
		this.timeoutSeconds = timeoutSeconds;

		log.info("Properties configured as " + this);
	}

	public boolean isUseCannedData() {
		return useCannedData;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public String getProxyHost() {
		return proxyHost;
	}

	public int getProxyPort() {
		return proxyPort;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public int getThreadCount() {
		return threadCount;
	}

	public int getTimeoutSeconds() {
		return timeoutSeconds;
	}

	@Override
	public String toString() {
		return "EmcCollectorProperties{" +
				"useCannedData=" + useCannedData +
				", host='" + host + '\'' +
				", port=" + port +
				", proxyHost='" + proxyHost + '\'' +
				", proxyPort=" + proxyPort +
				", username='" + username + '\'' +
				", password='" + password + '\'' +
				", threadCount='" + threadCount + '\'' +
				", timeoutSeconds='" + timeoutSeconds + '\'' +
				'}';
	}
}
