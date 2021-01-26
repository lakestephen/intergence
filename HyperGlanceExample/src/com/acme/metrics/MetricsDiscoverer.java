package com.acme.metrics;

import com.realstatus.hgs.collection.BatchGroup;
import com.realstatus.hgs.collection.CollectorPluginDescriptor;
import com.realstatus.hgs.collection.PerformanceExecutor;
import com.realstatus.hgs.model.lookup.ModelLookup;
import com.realstatus.hgs.model.metric.*;
import com.realstatus.hgs.model.update.PerformanceModelUpdate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MetricsDiscoverer implements PerformanceExecutor {

	// trivial example ACME API data request stubs
	private AcmeWidgetPerformanceCollectionRequest widgetPerformanceCollector = new AcmeWidgetPerformanceCollectionRequest();
	private AcmeGadgetPerformanceCollectionRequest gadgetPerformanceCollector = new AcmeGadgetPerformanceCollectionRequest();

	// trivial example value cache
	private Map<String, Collection<MetricValue>> performanceValuesByWidgetName = new ConcurrentHashMap<String, Collection<MetricValue>>();
	private Map<String, Collection<MetricValue>> performanceValuesByGadgetName = new ConcurrentHashMap<String, Collection<MetricValue>>();

	@Override
	public Collection<BatchGroup> getBatchGroups(CollectorPluginDescriptor pluginDescriptor, ModelLookup lookup) {
		/*
		 * Phase 1 - Invoked at start to generate coarse-grained BatchGroups
		 * Return BatchGroups containing any entities that need processing separately
		 */
		
		// The ACME API makes it easier to process widgets and gadgets separately
		Collection<BatchGroup> batches = new ArrayList<BatchGroup>();
		
		batches.add( // nodes
				new BatchGroup(
						widgetPerformanceCollector,
						lookup.getNodes(pluginDescriptor.getDatasourceName())));
		
		batches.add( // endpoints
				new BatchGroup(
						gadgetPerformanceCollector,
						lookup.getNodes(pluginDescriptor.getDatasourceName())));

		return batches;
	}
	
	@Override
	public void collect(Long executionTime, BatchGroup batch, ModelLookup lookup) {
		/*
		 * Phase 2 - Invoked multiple times in parallel, once per fine-grained BatchGroup (may be more 
		 * fine grained than the groups defined in getBatchGroups() as batch groups are partitioned according to the
		 * "collectionEntityBatchSize" specified in the collector's descriptor)
		 * 
		 * Process the contents of the BatchGroup and accumulate the results in a thread-safe manner.
		 */
		Object collector = batch.getMetaData();
		if (collector instanceof AcmeWidgetPerformanceCollectionRequest) {
			AcmeWidgetPerformanceCollectionRequest widgetRequest = (AcmeWidgetPerformanceCollectionRequest)collector;
			
			// TODO write some code to get the latest widget values, cache those values on performanceValuesByWidgetName
		} else {
			// TODO must be a gadget request... process it
			
		}
	}

	@Override
	public void attach(PerformanceModelUpdate update) {
		/*
		 * Phase 3 - Invoked at end to push the accumulated results through the update object.
		 * Use the update object to persist metrics accumulated by this executor.
		 */
		
		addCannedDataValues();
		
		update.addNodeMetricValues(performanceValuesByWidgetName);
		update.addNodeMetricValues(performanceValuesByGadgetName);
	}
	
	/**
	 * A (silly) example of some performance data
	 */
	private void addCannedDataValues() {
		Collection<MetricValue> coyoteStats = new ArrayList<MetricValue>();
		
		// see MetricDescriptor javadoc for description of attributes
		MetricDescriptor descriptor = 
			new DefaultMetricDescriptor(
				null,							// sourced from
				null, 							// group
				null, 							// distinguisher
				"average",		 				// rollup (e.g. "average", "min", "max")
				"roadrunner_kills",		 		// name
				"kph",							// unit: kph (kills per hour)
				(int)300);						// interval: how often we expect to sample the metric
		
		coyoteStats.add(
				new DefaultMetricValue(
						descriptor,
						new TimedValue(0.0, new Date().getTime())));
		
		performanceValuesByWidgetName.put("Coyote", coyoteStats);
	}

	// stubs for the example 3rd party API facade classes, down here to get them out of the way...
	private class AcmeWidgetPerformanceCollectionRequest {
		
	}
	
	private class AcmeGadgetPerformanceCollectionRequest {

	}
	
}
