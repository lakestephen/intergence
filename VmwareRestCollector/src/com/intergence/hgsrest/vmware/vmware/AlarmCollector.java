/**
 * Copyright 2010 Real-Status Ltd. All rights
 * reserved. This file contains intellectual property   
 * belonging to Real-Status Ltd and its licensors.
 */

package com.intergence.hgsrest.vmware.vmware;

import com.intergence.hgsrest.model.enumeration.AlarmSeverityEnum;
import com.vmware.vim25.AlarmInfo;
import com.vmware.vim25.AlarmState;
import com.vmware.vim25.ManagedEntityStatus;
import com.vmware.vim25.ObjectContent;
import org.apache.log4j.Logger;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;



public class AlarmCollector {
	private static final Logger logger = Logger.getLogger(AlarmCollector.class);
	
	private static final DateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	
	public static void collect(VSphereWrapper service, VmWareRepositoryUpdater updater) {
		ObjectContent[] results = service.queryEntityProperties("ManagedEntity", null);
		
		Set<String> uniqueAlarmIds = new HashSet<String>();
		
		for (ObjectContent entity : results) {
			AlarmState[] alarmInstances = service.getAlarmInstances(entity.getObj());
			
			for (AlarmState alarmInstance : alarmInstances) {
				if (isAlarming(alarmInstance)) {
					
					String alarmId = service.scopeId(alarmInstance.getKey());
					if (uniqueAlarmIds.add(alarmId)) {
						
						String vSphereNodeId = service.getEntityId(entity.getObj());
						
						AlarmInfo info = service.getEntityProperty(alarmInstance.getAlarm(), "info");
						
						Map<String, String> attributes = new HashMap<String, String>();
						attributes.put("eventName", info.getName());
						attributes.put("Description", info.getDescription());
						attributes.put("Timestamp", DEFAULT_DATE_FORMAT.format(alarmInstance.getTime().getTime()));
						
						AlarmSeverityEnum severity = convertStatusToSeverity(alarmInstance.getOverallStatus());
						updater.addAlarm(alarmId, vSphereNodeId, severity, attributes);
					}
					else {
						logger.trace("This alarm was found to be duplicated: " + alarmId + ", skipping it.");
					}
				}
			}
		}
	}
	
	private static boolean isAlarming(AlarmState alarm) {
		ManagedEntityStatus status = alarm.getOverallStatus();
		return status == ManagedEntityStatus.yellow || status == ManagedEntityStatus.red;
	}
	
	private static AlarmSeverityEnum convertStatusToSeverity(ManagedEntityStatus status) {
		if (status == ManagedEntityStatus.gray) {
			return AlarmSeverityEnum.UNKNOWN;
		}
		else if (status == ManagedEntityStatus.yellow) {
			return AlarmSeverityEnum.MINOR;
		}
		else if (status == ManagedEntityStatus.red) {
			return AlarmSeverityEnum.MAJOR;
		}
		return AlarmSeverityEnum.INFORMATIONAL;
	}
}
