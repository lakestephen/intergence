package com.realstatus.collector;

public class PropertiesKeys {

	// RRD keys
	public static final String OPENNMS_RRD_ROOT = "opennms.rrd.root";
	public static final String RRD_STEP = "opennms.rrd.step";
	public static final String RRD_AGGREGATION_PERIOD = "opennms.rrd.aggregatation.period";
	
	// badly brittle - this is now in three of these constants classes !
	public static final String CALCULATE_PROCESSED_METRICS = "metrics.calculate.processed";
}
