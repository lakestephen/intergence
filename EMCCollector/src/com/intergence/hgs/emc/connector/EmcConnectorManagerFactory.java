package com.intergence.hgs.emc.connector;

/**
 * Factory for building the {@code EmcConnectorManager}
 *
 * @author stephen
 */
public class EmcConnectorManagerFactory {

	private static final DefaultEmcConnectorManager INSTANCE = new DefaultEmcConnectorManager();

	public static final EmcConnectorManager getInstance() {
		return INSTANCE;
	}

}
