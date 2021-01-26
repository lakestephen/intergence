package com.realstatus.collector;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.realstatus.collector.metric.OpenNmsRrdFileHelper;
import com.realstatus.collector.metric.RrdFacade;
import com.realstatus.hgs.collection.CollectorPluginDescriptor;
import com.realstatus.hgs.collection.DataGatheringExecutor;
import com.realstatus.hgs.collection.requirements.CollectionRequirements;
import com.realstatus.hgs.collection.requirements.EntityDataRequirements;
import com.realstatus.hgs.collection.requirements.EntityScopeRequirement;
import com.realstatus.hgs.collection.requirements.EntityScopedDataRequirements;
import com.realstatus.hgs.collection.requirements.MetricRequirement;
import com.realstatus.hgs.model.Endpoint;
import com.realstatus.hgs.model.Node;
import com.realstatus.hgs.model.lookup.ModelLookup;
import com.realstatus.hgs.model.metric.DefaultMetricDescriptor;
import com.realstatus.hgs.model.metric.MetricDescriptor;
import com.realstatus.hgs.model.metric.TimeSeries;
import com.realstatus.hgs.model.metric.TimedValue;
import com.realstatus.hgs.model.update.ModelUpdate;

public class OpenNmsDataGatheringExecutor implements DataGatheringExecutor {

	public static final String COLLECTOR_NAME = "OpenNMS";
	
	private static final Logger logger = Logger.getLogger(OpenNmsDataGatheringExecutor.class);
	
	// For OpenNMS the only sourced from types that really make any sense are "node" and "interface"
	// Not really liking how brittle this is at all - these values also have to match with the jboss-beans.xml, ideally we'd just look them up from the plugindescriptor (if we had access to one here)
	private static final String NODE_TYPE = "node";
	private static final String INTERFACE_TYPE = "interface";
	
	@Override
	public void execute(CollectorPluginDescriptor pluginDescriptor, CollectionRequirements collectionRequirements, ModelLookup lookup, ModelUpdate update) {
		// For OpenNMS this is only used in gathering performance metrics (attributes are gathered using SQL queries: see DataGatheringJob)
		if (!collectionRequirements.requiresMetrics()) {
			return;
		}
		
		// satisfy 'global' requirements (i.e. not entity-scoped)
		satisfyMetricRequirementsBySourcedFromType(collectionRequirements.getGlobalDataRequirements(), null, lookup);
		
		// satsify entity-scoped requirements
		for (EntityScopedDataRequirements scopedRequirements : collectionRequirements.getEntityScopedRequirements()) {
			satisfyMetricRequirementsBySourcedFromType(scopedRequirements.getDataRequirements(), scopedRequirements.getEntityScope(), lookup);
		}
	}

	private void satisfyMetricRequirementsBySourcedFromType(
			EntityDataRequirements entityDataRequirements, EntityScopeRequirement entityScope, ModelLookup lookup) {
		Map<String, Set<MetricRequirement>> metricRequirementsByType = entityDataRequirements.getMetricRequirementsBySourcedFromType();
		
		final Collection<String> scopedNodeEntities = new HashSet<String>();
		final Collection<String> scopedEndpointEntities = new HashSet<String>();
		
		if (entityScope != null) {
			if (entityScope.getNodeEntities() != null) {
				scopedNodeEntities.addAll(entityScope.getNodeEntities());
			}
			if (entityScope.getEndpointEntities() != null) {
				scopedEndpointEntities.addAll(entityScope.getEndpointEntities());
			}
		}
		
		for (Entry<String, Set<MetricRequirement>> entry : metricRequirementsByType.entrySet()) {
			// these keys may not always be set, so if they're not then we've got no choice but to do all
			// or perhaps the keys are not set just in the scoped entity type of request. TODO talk to Dave about this
			// [Dave:] This was happening because of a bug in the client (SPIN-2296). Basically if the sourcedFromType is omitted from
			//         a filter's requirements section then that requirement will appear with a null key here.
			//         My intention was that it's a mandatory field but it's not being explicitly validated (TODO: INMS-1491)
			if (entry.getKey() == null) {
				gatherNodeMetrics(getNodesToConsider(entityScope, lookup, scopedNodeEntities), entry.getValue());
				gatherInterfaceMetrics(getEndpointsToConsider(entityScope, lookup, scopedEndpointEntities), entry.getValue());
			}
			else {
				if (entry.getKey().equalsIgnoreCase(NODE_TYPE)) {
					gatherNodeMetrics(getNodesToConsider(entityScope, lookup, scopedNodeEntities), entry.getValue());
				} else if (entry.getKey().equalsIgnoreCase(INTERFACE_TYPE)) {
					gatherInterfaceMetrics(getEndpointsToConsider(entityScope, lookup, scopedEndpointEntities), entry.getValue());
				} else {
					logger.trace("Unsupported type of metric requirement ' " +entry.getKey()+"' will not be satisfied");
				}
			}
		}
	}

	private Collection<Node> getNodesToConsider(EntityScopeRequirement entityScope, 
			ModelLookup lookup, final Collection<String> scopedNodeEntities) {
		// TODO make this more efficient
		return entityScope == null ? lookup.getNodes(COLLECTOR_NAME) : 
			Collections2.filter(lookup.getNodes(COLLECTOR_NAME), new Predicate<Node>() {

			@Override
			public boolean apply(Node node) {
				return scopedNodeEntities.contains(node.getForeignSourceId());
			}
		});
	}
	
	private Collection<Endpoint> getEndpointsToConsider(EntityScopeRequirement entityScope, 
			ModelLookup lookup, final Collection<String> scopedEndpointEntities) {
		return entityScope == null ? lookup.getEndpoints(COLLECTOR_NAME) : 
			Collections2.filter(lookup.getEndpoints(COLLECTOR_NAME), new Predicate<Endpoint>() {

			@Override
			public boolean apply(Endpoint endpoint) {
				return scopedEndpointEntities.contains(endpoint.getForeignSourceId());
			}
		});
	}

	private void gatherNodeMetrics(Collection<Node> nodes, Collection<MetricRequirement> metricRequirements) {
		for (Node node : nodes) {
			for (MetricRequirement requirement : metricRequirements) {
				// handle the host resources case here - if the requirement.distinguisher = '*' then we need 
				// to spin over all host resources directories (drive letters for storage info + memory directories)
				if (requirement.getDistinguisher() != null && requirement.getDistinguisher().equals("*")) {
					for (File driveLetter : OpenNmsRrdFileHelper.getHostResourcesDrivesDirectories(node)) {
						String distinguisher = driveLetter.getName();
						processNodeRequirement(node, requirement, distinguisher);
					}
				} else {
					processNodeRequirement(node, requirement, requirement.getDistinguisher());
				}
			}
		}
	}

	private void processNodeRequirement(Node node, MetricRequirement requirement, String actualDistinguisherToUse) {
		try {
			File file = getRrdFileHandle(node, requirement.getName(), actualDistinguisherToUse);
			
			TimedValue[] values =
				new RrdFacade().fetchData(
						file,
						requirement.getName(),
						requirement.getRollup(),
						requirement.getPeriod());
			if (values != null && values.length > 0) {
				MetricDescriptor descriptor = requirement.toPartialDescriptor().withDistinguisher(actualDistinguisherToUse);
				node.putMetricTimeSeries(descriptor, new TimeSeries(values), true);
			} else {
				logger.trace("Failed to satisfy a metric because no data was available for metric.name: " +requirement.getName());
			}
		} catch (Throwable t) {
			// likely to mean the RRD file doesn't exist, just log failure to satisfy the requirement
			logger.trace("Failed to satisfy a metric for metric.name: " +requirement.getName());
		}
	}
	
	private void gatherInterfaceMetrics(Collection<Endpoint> endpoints, Collection<MetricRequirement> metricRequirements) {
		for (Endpoint endpoint : endpoints) {
			for (MetricRequirement requirement : metricRequirements) {
				try {
					File file = new OpenNmsRrdFileHelper().getFile(endpoint, requirement.getName());
					
					TimedValue[] values =
						new RrdFacade().fetchData(
								file,
								requirement.getName(),
								requirement.getRollup(),
								requirement.getPeriod());
					
					if (values != null && values.length > 0) {
						endpoint.putMetricTimeSeries(requirement.toPartialDescriptor(), new TimeSeries(values), true);
					} else {
						logger.trace("Failed to satisfy a metric because no data was available for metric.name: " +requirement.getName());
					}
				} catch (Throwable t) {
					// likely to mean the RRD file doesn't exist, just log failure to satisfy the requirement
					logger.trace("Failed to satisfy a metric for metric.name: " +requirement.getName());
				}
			}
		}
	}
	
	private File getRrdFileHandle(Node node, String metricName, String metricDistinguisher) {
		File file = null;
		if (metricDistinguisher == null  || metricDistinguisher.isEmpty()) {
			file = new OpenNmsRrdFileHelper().getFile(node, metricName);
		} else {
			file = OpenNmsRrdFileHelper.getHostResourcesFile(node, metricDistinguisher, metricName);
		}
		return file;
	}
}
