package com.intergence.hgsrest.runner;

import com.intergence.hgsrest.collection.DiscoveryExecutor;
import com.intergence.hgsrest.restcomms.HyperglanceCallManager;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * Created by stephen on 27/01/2015.
 */
public class CollectorRunner implements Runnable {

	private Logger log = Logger.getLogger(this.getClass());

	private final List<DiscoveryExecutor> discoveryExecutors = new ArrayList<DiscoveryExecutor>();
	private HyperglanceCallManager hyperglanceCallManager;
	private Set<String> reservedAttributeNames = new HashSet<String>();
	private String datasourceName;

	@Override
	public void run() {

		Collections.sort(discoveryExecutors, DiscoveryExecutor.BY_EXECUTION_ORDER);

		log.info("Will run discovery for [" + discoveryExecutors.size() + "] collectors");
		log.debug("Will run discovery in order [" + discoveryExecutors + "]");

		if (discoveryExecutors.size() > 0 ) {
			Topology topology = new Topology(datasourceName, reservedAttributeNames);

			for (DiscoveryExecutor discoveryExecutor : discoveryExecutors) {
				try {
					log.info("Running discovery [" + discoveryExecutor + "]");
					discoveryExecutor.execute(topology);

				} catch (Exception e) {
					log.error("", e);
				}
			}

			try {
				hyperglanceCallManager.replaceTopology(topology);
			} catch (Exception e) {
				log.error("", e);
			}

		}
	}

	public void addDiscoveryExecutor(DiscoveryExecutor discoveryExecutor) {
		log.info("Registering collector [" + discoveryExecutor + "]");
		this.discoveryExecutors.add(discoveryExecutor);
	}

	public void setHyperglanceCallManager(HyperglanceCallManager hyperglanceCallManager) {
		this.hyperglanceCallManager = hyperglanceCallManager;
	}

	public void setDatasourceName(String datasourceName) {
		this.datasourceName = datasourceName;
	}

	public void setReservedAttributeNames(String[] reservedAttributeNames) {
		for (String reservedAttributeName : reservedAttributeNames) {
			this.reservedAttributeNames.add(reservedAttributeName);
		}
	}
}
