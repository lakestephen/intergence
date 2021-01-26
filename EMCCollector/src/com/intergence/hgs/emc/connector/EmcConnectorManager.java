package com.intergence.hgs.emc.connector;

/**
 * Manage access to the EMC system, providing a simple threading model.
 *
 * @author stephen
 */
public interface EmcConnectorManager {

	public String makeCall(String request) throws RuntimeException;
}