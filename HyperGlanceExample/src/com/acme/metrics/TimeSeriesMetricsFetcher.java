package com.acme.metrics;

import java.util.Date;

import com.realstatus.hgs.collection.dao.PerformanceFetcher;
import com.realstatus.hgs.model.MetricedEntity;
import com.realstatus.hgs.model.enumeration.PeriodEnum;
import com.realstatus.hgs.model.metric.MetricDescriptor;
import com.realstatus.hgs.model.metric.TimedValue;


public class TimeSeriesMetricsFetcher extends PerformanceFetcher {

	// some canned example values
	private static final TimedValue[] CANNED_EXAMPLE_VALUES;
	
	// static initialisation block to initialise the canned values (in a pseudo random fashion.
	static {
		CANNED_EXAMPLE_VALUES = new TimedValue[15];
		
		long time = (new Date()).getTime() - (60000 * 14);
		
		for (int i = 0; i < 15; i++) {
			CANNED_EXAMPLE_VALUES[i] = new TimedValue(Math.random() * 100, time + i * 60000);
		}
	}
	
	@Override
	public TimedValue[] getTimeSeries(MetricedEntity entity, MetricDescriptor metricDescriptor, PeriodEnum timePeriod) {
		// Lookup data in the third party system, an example of what this API might
		// look like (if based on an action executor pattern) is commented out below:
//		Date now = new Date();
//		
//		AcmeMetricValueResponse response = 
//			new AcmeAction().getMetricValues(
//					new AcmeMetricValueRequest()
//						.withEntityName(entity.getForeignSourceId())
//						.withMetricName(metricDescriptor.getName())
//						.withInteval(metricDescriptor.getInterval())
//						.withRollup(metricDescriptor.getRollup())
//						.withStartTime(DateHelper.subtractPeriod(timePeriod, now.getTime()))
//						.withEndTime(now.getTime()));
		
		// once we've got the response we'd then now iterate over the datapoints that it no doubt aggregates
		// to translate the values from their API to the HG API
		// (NOTE: I'm not doing this here and am instead simply returning some canned example values)
		return CANNED_EXAMPLE_VALUES;
	}
}
