package com.realstatus.collector.metric;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jrobin.core.Archive;
import org.jrobin.core.RrdDb;
import org.jrobin.core.RrdDbPool;
import org.jrobin.core.RrdException;

import com.realstatus.hgs.collection.CollectorPluginDescriptor;
import com.realstatus.hgs.collection.MetricAccessor;
import com.realstatus.hgs.model.AttributedEntity;
import com.realstatus.hgs.model.Endpoint;
import com.realstatus.hgs.model.Node;
import com.realstatus.hgs.model.metric.DefaultMetricDescriptor;
import com.realstatus.hgs.model.metric.MetricDescriptor;


public class OnmsMetricAccessor implements MetricAccessor {

	private static final Logger logger = Logger.getLogger(OnmsMetricAccessor.class);
	
	@Override
	public Collection<MetricDescriptor> getEndpointMetrics(CollectorPluginDescriptor descriptor, AttributedEntity endpoint) {
		Collection<MetricDescriptor> descriptors = new ArrayList<MetricDescriptor>();
		
		for (File directory : OpenNmsRrdFileHelper.getRrdDirectories((Endpoint)endpoint)) {
			descriptors.addAll(getMetricDescriptors(directory, null));
		}
				
		return descriptors;
	}

	@Override
	public Collection<MetricDescriptor> getNodeMetrics(CollectorPluginDescriptor descriptor, AttributedEntity node) {		
		Collection<MetricDescriptor> descriptors = new ArrayList<MetricDescriptor>();
		
		for (File directory : OpenNmsRrdFileHelper.getRrdDirectories((Node)node)) {
			descriptors.addAll(getMetricDescriptors(directory, null));
		}
		
		for (File drive : OpenNmsRrdFileHelper.getHostResourcesDrivesDirectories((Node)node)) {
			if (!drive.isDirectory()) {
				continue;
			}
			
			String driveDistinguisher = drive.getName();
			descriptors.addAll(getMetricDescriptors(drive, driveDistinguisher));
		}
		return descriptors;
	}
	
	private Collection<MetricDescriptor> getMetricDescriptors(File directory, String attributeNameSuffix) {
		File[] rrdFiles = directory.listFiles();
		if (rrdFiles == null) {
			return new ArrayList<MetricDescriptor>();
		}
		
		Set<MetricDescriptor> descriptors = new HashSet<MetricDescriptor>();
		
		for (File file : rrdFiles) {
			if (!isRrdFile(file)) {
				continue;
			}
			
			String filename = file.getAbsolutePath();
			
			RrdDb rrdFile = null;
			try {
				rrdFile = RrdDbPool.getInstance().requestRrdDb(filename);

				String[] dsNames = rrdFile.getDsNames();	
				
				// NB we configure ONMS with 1 metric per RRD datasource, so this is safe.
				String dsName = dsNames[0];
				
				for (int i = 0; i<rrdFile.getArcCount(); i++) {
					Archive archive = rrdFile.getArchive(i);
					// There may be multiple archives for a consolodation function (with differing 
					// aggregation periods), but this doesn't matter as we're building a Set of descriptors
					// and we don't care about the data intervals at this point
					String archiveConsolFun = archive.getConsolFun();
					
					descriptors.add(
							new DefaultMetricDescriptor(dsName, archiveConsolFun).withDistinguisher(attributeNameSuffix));							// unit not known 
				}
			} catch (IOException ioe) {
				logger.error("IOException occurred : ", ioe);
				
				throw new RuntimeException(ioe);
			} catch (RrdException rrde) {
				logger.error("RrdException occurred : ", rrde);
				throw new RuntimeException(rrde);
			} finally {
				releaseRrdFileHandle(rrdFile);
			}
		}
		return descriptors;
	}
	
	private boolean isRrdFile(File file) {
		return !file.isDirectory() && (file.getName().endsWith("jrb") || file.getName().endsWith("rrd"));
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
