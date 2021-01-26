/**
 * Copyright 2010 Real-Status Ltd. All rights
 * reserved. This file contains intellectual property   
 * belonging to Real-Status Ltd and its licensors.
 */
package com.intergence.hgsrest.vmware.vmware;

import com.intergence.hgsrest.vmware.credentials.Credential;
import com.vmware.vim25.AlarmState;
import com.vmware.vim25.DynamicProperty;
import com.vmware.vim25.InvalidProperty;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.ObjectContent;
import com.vmware.vim25.ObjectSpec;
import com.vmware.vim25.PerfEntityMetricBase;
import com.vmware.vim25.PerfQuerySpec;
import com.vmware.vim25.PropertyFilterSpec;
import com.vmware.vim25.PropertySpec;
import com.vmware.vim25.RuntimeFault;
import com.vmware.vim25.SelectionSpec;
import com.vmware.vim25.TraversalSpec;
import org.apache.log4j.Logger;

import java.rmi.RemoteException;

public class VSphereWrapper {
	private static final Logger logger = Logger.getLogger(VSphereWrapper.class);
	
	private VSphereSession vsphere;

	public static VSphereWrapper tryStartSession(Credential credentials) {
		try {
			VSphereSession session = new VSphereSession(credentials);
			try {
				VSphereWrapper service = new VSphereWrapper(session);
				return service;
			}
			catch (Throwable t) {
				logger.error("Unable to construct VSphereWrapper service for " + credentials.getHostNameOrIp() + " so the session will be terminated.", t);
				session.disconnect();
			}
		}
		catch (Throwable t) {
			logger.error("VSphere connection failure: (" + credentials.getHostNameOrIp() + ")", t);
		}
		
		return null;
	}
	
	public VSphereWrapper(VSphereSession session) {
		vsphere = session;
		VSphereHelper.validateEssentialServices(vsphere.getContent());
	}
	
	public VSphereSession getSessionWrapper() {
		return vsphere;
	}
	
	/*public VSphereMetricService getMetricService() {
		if (metrics == null) {
			metrics = VSphereMetricService.tryGetInstance(this);
		}
		return metrics;
	}
	
	public boolean isMetricServiceAvailable() {
		return getMetricService() != null;
	}*/
	
	public String getEntityId(Object managedObjectReference) {
		if (managedObjectReference == null) {
			return null;
		}
		return scopeId(((ManagedObjectReference)managedObjectReference).get_value());
	}
	
	public String scopeId(String id) {
		int vCenterKey = vsphere.getSessionCredentials().getKey();
		return id + ":" + String.valueOf(vCenterKey);
	}
	
	public String getTypeFromId(String typeWithId) {
		return typeWithId.substring(0, typeWithId.lastIndexOf('-'));
	}
	
	public ObjectContent[] queryEntityProperties(String type, String[] properties) {
		ManagedObjectReference view = createContainerView(type);
		TraversalSpec traversalSpec = createTraversalSpec("ContainerView", "view", null, null);
		return traverseEntity(view, true, type, properties, traversalSpec);
	}
	
	public ObjectContent[] queryForEntities(String type) {
		return queryEntityProperties(type, null);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getEntityProperty(ManagedObjectReference entity, String property) {
		ObjectContent[] results = traverseEntity(entity, false, entity.getType(), new String[] { property }, null);
		DynamicProperty[] propSet = results[0].getPropSet();
		if (propSet != null && propSet.length == 1) {
			return (T)propSet[0].getVal();
		}
		return null;
	}

	public AlarmState[] getAlarmInstances(ManagedObjectReference entity) {
		try {
			AlarmState[] alarms = vsphere.getApi().getAlarmState(vsphere.getContent().getAlarmManager(), entity);
			if (alarms == null) {
				return new AlarmState[0];
			}
			return alarms;
		} catch (RuntimeFault e) {
			logger.error("RuntimeFault exception occurred querying for alarm states", e);
			throw new RuntimeException(e);
		} catch (RemoteException e) {
			logger.error("RemoteException occurred querying for alarm states", e);
			throw new RuntimeException(e);
		}
	}
		
	private ObjectContent[] traverseEntity(ManagedObjectReference entity, Boolean skip, String type, String[] properties, TraversalSpec traversalSpec) {
		return traverseEntities(new ManagedObjectReference[] { entity }, skip, type, properties, traversalSpec);
	}
	
	private ObjectContent[] traverseEntities(ManagedObjectReference[] entities, Boolean skip, String type, String[] properties, TraversalSpec traversalSpec) {
		ObjectSpec[] objectSpecs = new ObjectSpec[entities.length];
		for (int i = 0; i < entities.length; ++i) {
			ManagedObjectReference entity = entities[i];
			ObjectSpec objectSpec = createObjectSpec(entity, skip, traversalSpec);
			objectSpecs[i] = objectSpec;
		}
		PropertySpec propertySpec = createPropertySpec(type, properties);
		return runFilter(objectSpecs, new PropertySpec[] { propertySpec });
	}
	
	private ObjectContent[] runFilter(ObjectSpec[] objects, PropertySpec[] properties) {
		PropertyFilterSpec propertyFilter = new PropertyFilterSpec();
		propertyFilter.setObjectSet(objects);
		propertyFilter.setPropSet  (properties);
		
		try {
			ObjectContent[] results =
				vsphere.getApi().retrieveProperties(
					vsphere.getContent().getPropertyCollector(),
					new PropertyFilterSpec[] { propertyFilter });
			
			if (results == null) {
				return new ObjectContent[0];
			}
			
			return results;
		} catch (InvalidProperty e) {
			logger.error("InvalidProperty exception occurred retrieving properties", e);
			throw new RuntimeException(e);
		} catch (RuntimeFault e) {
			logger.error("RuntimeFault exception occurred retrieving properties", e);
			throw new RuntimeException(e);
		} catch (RemoteException e) {
			logger.error("RemoteException occurred retrieving properties", e);
			throw new RuntimeException(e);
		}
	}
	
	
	private ManagedObjectReference createContainerView(String entityType) {
		try {
			return vsphere.getApi().createContainerView(
					vsphere.getContent().getViewManager(),
					vsphere.getContent().getRootFolder(),
					new String[] { entityType },
					true );
		} catch (RuntimeFault rf) {
			logger.error("RuntimeFault occurred creating container view of " + entityType, rf);
			throw new RuntimeException(rf);
		} catch (RemoteException re) {
			logger.error("RemoteException occurred creating container view of " + entityType, re);
			throw new RuntimeException(re);
		}
	}
	
	private TraversalSpec createTraversalSpec(String type, String pathThroughType, SelectionSpec[] selectionSpecs, String name) {
	      TraversalSpec spec = new TraversalSpec(
	              null, null, null,
	              type, 
	              pathThroughType,
	              Boolean.FALSE, 
	              selectionSpecs);
	      if (name != null) {
	    	  spec.setName(name);
	      }
	      return spec;
	}
	
	private ObjectSpec createObjectSpec(ManagedObjectReference mor, Boolean skip, SelectionSpec selection) {
		SelectionSpec[] selectSpec = selection == null ? null : new SelectionSpec[] { selection };
		return new ObjectSpec(null, null, mor, skip, selectSpec);
	}
	
	private PropertySpec createPropertySpec(String type, String[] properties) {
		PropertySpec spec = new PropertySpec();
		spec.setType(type);
		if (properties != null) {
			spec.setPathSet(properties);
		}
		return spec;
	}
	
	public PerfEntityMetricBase[] queryEntityMetrics(PerfQuerySpec[] perfSpecs) {
	   	try {
			return vsphere.getApi().queryPerf(
					vsphere.getContent().getPerfManager(),
					perfSpecs);
		} catch (RuntimeFault e) {
			logger.error("RuntimeFault exception occurred querying performance metrics", e);
			throw new RuntimeException(e);
		} catch (RemoteException e) {
			logger.error("RemoteException occurred querying performance metrics", e);
			throw new RuntimeException(e);
		}
	}

	public double getVersion() {
		String version = vsphere.getContent().getAbout().getApiVersion();
		String versionWithoutMinorVersionTextualDelimiters = version.replaceAll( "u", "" );
		return Double.parseDouble(versionWithoutMinorVersionTextualDelimiters);
	}
}
