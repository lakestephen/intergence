package com.intergence.hgs.emc.collector;

import com.intergence.hgs.emc.config.EmcCollectorProperties;
import com.intergence.hgs.emc.connector.EmcConnectorManagerFactory;
import com.intergence.hgs.emc.data.bean.DiskInfo;
import com.intergence.hgs.emc.data.bean.RaidGroupInfo;
import com.intergence.hgs.emc.data.dao.CelerraEmcDao;
import com.intergence.hgs.emc.data.dao.EmcDao;
import com.realstatus.hgs.collection.BatchGroup;
import com.realstatus.hgs.collection.CollectorPluginDescriptor;
import com.realstatus.hgs.collection.PerformanceExecutor;
import com.realstatus.hgs.model.lookup.ModelLookup;
import com.realstatus.hgs.model.metric.*;
import com.realstatus.hgs.model.update.PerformanceModelUpdate;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Extract the performance details
 *
 * @author Stephen
 */
public class EmcPerformanceExecutor implements PerformanceExecutor {

	private final Logger log = Logger.getLogger(this.getClass());

	private final EmcDao emcDao = new CelerraEmcDao(EmcCollectorProperties.getInstance().isUseCannedData()?null: EmcConnectorManagerFactory.getInstance());

	// trivial example ACME API data request stubs
	private EmcDiskPerformanceCollectionRequest diskPerformanceCollector = new EmcDiskPerformanceCollectionRequest();

	// trivial example value cache
	private Map<String, Collection<MetricValue>> performanceValuesByWidgetName = new ConcurrentHashMap<String, Collection<MetricValue>>();

	@Override
	public Collection<BatchGroup> getBatchGroups(CollectorPluginDescriptor pluginDescriptor, ModelLookup lookup) {

		log.info("Call getBatchGroups " + pluginDescriptor + ",    " + lookup);

		Collection<BatchGroup> batches = new ArrayList<BatchGroup>();

		batches.add( // disks
				new BatchGroup(
						diskPerformanceCollector,
						lookup.getNodes(pluginDescriptor.getDatasourceName())));
		return batches;
	}

	private static final MetricDescriptor USED_CAPACITY_DESCRIPTOR = new DefaultMetricDescriptor(
			null,							// sourced from
			null, 							// group
			null, 							// distinguisher
			"average",		 				// rollup (e.g. "average", "min", "max")
			"used capacity",		 		// name
			"bytes",						// unit
			(int)300);						// interval: how often we expect to sample the metric
	private static final MetricDescriptor REMAINING_PERCENT_DESCRIPTOR = new DefaultMetricDescriptor(
			null,							// sourced from
			null, 							// group
			null, 							// distinguisher
			"average",		 			    // rollup (e.g. "average", "min", "max")
			"remaining percent",	 		// name
			"percent",						// unit
			(int)300);						// interval: how often we expect to sample the metric
	private static final MetricDescriptor USED_PERCENT_DESCRIPTOR = new DefaultMetricDescriptor(
			null,							// sourced from
			null, 							// group
			null, 							// distinguisher
			"average",		 				// rollup (e.g. "average", "min", "max")
			"used percent",		 		    // name
			"percent",						// unit
			(int)300);						// interval: how often we expect to sample the metric

	@Override
	public void collect(Long executionTime, BatchGroup batch, ModelLookup lookup) {

		log.info("Call collect " + executionTime + ",    " + batch + ",    " + lookup);

		/*
		 * Phase 2 - Invoked multiple times in parallel, once per fine-grained BatchGroup (may be more
		 * fine grained than the groups defined in getBatchGroups() as batch groups are partitioned according to the
		 * "collectionEntityBatchSize" specified in the collector's descriptor)
		 *
		 * Process the contents of the BatchGroup and accumulate the results in a thread-safe manner.
		 */

		List<DiskInfo> allDisks = emcDao.getAllDisks();
		long time = new Date().getTime();

		for (DiskInfo diskInfo : allDisks) {
			Collection < MetricValue > diskStats = new ArrayList<MetricValue>();

			double usedCapacity = Double.parseDouble(diskInfo.getUsedCapacity());
			double capacity = Double.parseDouble(diskInfo.getCapacity());
			double usedPercent = (usedCapacity/capacity) * 100.0;
			double remainingPercent = 100 - usedPercent;

			diskStats.add(new DefaultMetricValue(USED_CAPACITY_DESCRIPTOR, new TimedValue(usedCapacity, time)));
			diskStats.add(new DefaultMetricValue(USED_PERCENT_DESCRIPTOR, new TimedValue(usedPercent, time)));
			diskStats.add(new DefaultMetricValue(REMAINING_PERCENT_DESCRIPTOR, new TimedValue(remainingPercent, time)));

			String diskDisplayName = "Disk" + diskInfo.getName(); //Has to match what is configured in topology discoverer
			performanceValuesByWidgetName.put(diskDisplayName, diskStats);
		}

		List<RaidGroupInfo> allRaidGroups = emcDao.getAllRaidGroups();
		for (RaidGroupInfo raidGroup : allRaidGroups) {
			Collection < MetricValue > raidStats = new ArrayList<MetricValue>();

			double usedCapacity = Double.parseDouble(raidGroup.getUsedCapacity());
			double capacity = Double.parseDouble(raidGroup.getLogicalCapacity());
			double usedPercent = (usedCapacity/capacity) * 100.0;
			double remainingPercent = 100 - usedPercent;

			raidStats.add(new DefaultMetricValue(USED_CAPACITY_DESCRIPTOR, new TimedValue(usedCapacity, time)));
			raidStats.add(new DefaultMetricValue(USED_PERCENT_DESCRIPTOR, new TimedValue(usedPercent, time)));
			raidStats.add(new DefaultMetricValue(REMAINING_PERCENT_DESCRIPTOR, new TimedValue(remainingPercent, time)));

			String raidGroupDisplayName = "RaidGroup" +raidGroup.getId(); //Has to match what is configured in topology discoverer
			performanceValuesByWidgetName.put(raidGroupDisplayName, raidStats);
		}
	}

	@Override
	public void attach(PerformanceModelUpdate update) {

		log.info("Call attach " + update);

		update.addNodeMetricValues(performanceValuesByWidgetName);
	}

	// stubs for the example 3rd party API facade classes, down here to get them out of the way...
	private class EmcDiskPerformanceCollectionRequest {

	}

}