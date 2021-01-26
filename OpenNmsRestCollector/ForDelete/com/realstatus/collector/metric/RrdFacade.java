/**
 * Copyright 2010 Real-Status Ltd. All rights
 * reserved. This file contains intellectual property   
 * belonging to Real-Status Ltd and its licensors.
 */
package com.realstatus.collector.metric;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.jrobin.core.FetchData;
import org.jrobin.core.RrdDb;
import org.jrobin.core.RrdDbPool;
import org.jrobin.core.RrdException;

import com.google.common.base.Preconditions;
import com.realstatus.collector.PropertiesKeys;
import com.realstatus.hgs.model.enumeration.PeriodEnum;
import com.realstatus.hgs.model.metric.TimedValue;
import com.realstatus.hgs.util.DateHelper;
import com.realstatus.hgs.util.PropertiesLoader;
import com.realstatus.hgs.util.ServerTime;

public class RrdFacade {

	private static final Logger logger = Logger.getLogger(RrdFacade.class);
	
	public TimedValue[] fetchData(File file, String datasourceName, 
			String consolodationFunction, PeriodEnum periodEnum) {
		Preconditions.checkNotNull(file);
		
		RrdDb rrdFile = null;
		try {
						
			rrdFile = RrdDbPool.getInstance().requestRrdDb(file.getAbsolutePath());
			
			if (rrdFile != null) {
			    return getData(rrdFile, datasourceName, consolodationFunction, periodEnum);
			} else {
				logger.error("file not found : " +file.getAbsolutePath());
				return new TimedValue[] {};
			}
		} catch (IOException ioe) {
			throw new RuntimeException(ioe);
		} catch (RrdException rrde) {
			throw new RuntimeException(rrde);
		} finally {
			releaseRrdFileHandle(rrdFile);
		}
	}

	private TimedValue[] getData(RrdDb rrdFile, String datasourceName, 
			String consolodationFunction, PeriodEnum periodEnum) throws RrdException, IOException {
		int rrdStep = 
			PropertiesLoader.getIntegerProperty(PropertiesKeys.RRD_STEP);
		
		long nowInSeconds = ServerTime.milliseconds() / 1000;
		
		long end = (nowInSeconds - (nowInSeconds % rrdStep));
		long start = DateHelper.subtractPeriod(periodEnum, nowInSeconds*1000) / 1000;
		
		long resolution = rrdStep;
		
		FetchData data = rrdFile.createFetchRequest(
				consolodationFunction, start, end, resolution).fetchData();
		
		double[] values = data.getValues(datasourceName);
	    long[] times = data.getTimestamps();
	    
	    Collection<TimedValue> timedValues = new ArrayList<TimedValue>();
	    for (int i = 0; i < values.length; i++) {
	    	if (!((Double)values[i]).equals(Double.NaN)) {
	    		timedValues.add(new TimedValue(values[i], times[i]*1000));
	    	}
		}
	    
		return timedValues.toArray(new TimedValue[0]);
	}

	private void releaseRrdFileHandle(RrdDb rrdFile) {
		if (rrdFile != null) {
			try {
				RrdDbPool.getInstance().release(rrdFile);
			} catch (Throwable t) {
				// nothing we can do
			}
		}
	}

}
