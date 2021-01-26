package com.intergence.hgs.emc.collector;

import com.intergence.hgs.emc.config.EmcCollectorProperties;
import com.intergence.hgs.emc.connector.EmcConnectorManagerFactory;
import com.intergence.hgs.emc.data.bean.*;
import com.intergence.hgs.emc.data.dao.CelerraEmcDao;
import com.intergence.hgs.emc.data.dao.EmcDao;
import com.realstatus.hgs.collection.CollectorPluginDescriptor;
import com.realstatus.hgs.collection.DiscoveryExecutor;
import com.realstatus.hgs.model.lookup.ModelLookup;
import com.realstatus.hgs.model.update.ModelUpdate;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Find the topology that the EMC system supports.
 * Called by the HyperGlance framework
 *
 * @author Stephen Lake
 */
public class EmcTopologyDiscoverer implements DiscoveryExecutor {

	private final Logger log = Logger.getLogger(this.getClass());

	private final EmcDao emcDao = new CelerraEmcDao(EmcCollectorProperties.getInstance().isUseCannedData()?null:EmcConnectorManagerFactory.getInstance());

	@Override
	public void execute(CollectorPluginDescriptor descriptor, ModelUpdate update, ModelLookup lookup) {
		String systemName = addSystem(update);
		Map<String, String> diskEndpointNames = addDisks(update, systemName);
		addRaidGroups(update, diskEndpointNames);
		addStorageProcessors(update, systemName);
	}

	private String addSystem(ModelUpdate update) {
		List<ControlStationInfo> controlStations = emcDao.getControlStation();
		ControlStationInfo controlStationInfo = (controlStations.size() > 0)?controlStations.get(0):null;
		List<SystemInfo> systemInfos = emcDao.getSystemInfo();
		List<GeneralConfigInfo> generalConfigInfos = emcDao.getGeneralConfig();

		String systemName = null;

		for (SystemInfo systemInfo : systemInfos) {
			// Build the system
			Map<String, String> attributes = new HashMap<String, String>();

			// Control Station Info
			attributes.put("name", controlStationInfo.getHostname());
			attributes.put("ip address", controlStationInfo.getAddress());
			attributes.put("netmask", controlStationInfo.getNetmask());
			attributes.put("control station type", controlStationInfo.getType());
			attributes.put("control station version", controlStationInfo.getVersion());
			attributes.put("dns servers", controlStationInfo.getDnsServers());
			attributes.put("dns domain", controlStationInfo.getDnsDomain());
			attributes.put("timezone", controlStationInfo.getTimeZone());
			attributes.put("slot", controlStationInfo.getSlot());

			// System Info
			attributes.put("product name", systemInfo.getProductName());
			attributes.put("type", "Storage" );
			attributes.put("serial", systemInfo.getSerial() );
			attributes.put("ww cid", systemInfo.getWwCid());
			attributes.put("system version", systemInfo.getVersion() );

			// General Info
			GeneralConfigInfo generalConfigByName = findGeneralConfigByName(generalConfigInfos, systemInfo.getSerial());
			attributes.put("uuid", generalConfigByName.getUid() );
			attributes.put("model number", generalConfigByName.getModelNumber() );
			attributes.put("model type", generalConfigByName.getModelType() );
			attributes.put("clariion devices", generalConfigByName.getClariionDevices() );
			attributes.put("physical disks", generalConfigByName.getPhysicalDisks() );
			attributes.put("visible devices", generalConfigByName.getVisibleDevices() );
			attributes.put("raid groups", generalConfigByName.getRaidGroups() );
			attributes.put("storage groups", generalConfigByName.getStorageGroups() );
			attributes.put("visible devices", generalConfigByName.getVisibleDevices() );

			// Add Node
			systemName = controlStationInfo.getHostname().replace(" ","");
			update.addNode(systemName, attributes);

			String systemEndpointName = systemName;
			update.addEndpoint(systemName, systemEndpointName , new HashMap<String, String>());

		}
		return systemName;
	}

	private GeneralConfigInfo findGeneralConfigByName(List<GeneralConfigInfo> generalConfigInfos, String serial) {
		for (GeneralConfigInfo generalConfigInfo : generalConfigInfos) {
			if (serial.equals(generalConfigInfo.getName())) {
				return generalConfigInfo;
			}
		}

		return null;
	}

	private Map<String, String> addDisks(ModelUpdate update, String systemName) {
		final Map<String, String> busEndPoints = new HashMap<String, String>();
		final Map<String, String> diskEndPoints = new HashMap<String, String>();

		// Build disks
		List<DiskInfo> allDisks = emcDao.getAllDisks();
		for (DiskInfo diskInfo : allDisks) {
			String diskDisplayName = "Disk" + diskInfo.getName();

			Map<String, String> attributes = new HashMap<String, String>();
			attributes.put("type", "Appliance");
			attributes.put("name", diskDisplayName);
			attributes.put("bus", diskInfo.getBus());
			attributes.put("enclosure number", diskInfo.getEnclosureNumber());
			attributes.put("disk number", diskInfo.getDiskNumber());
			attributes.put("vendor id", diskInfo.getVendorId());
			attributes.put("product id", diskInfo.getProductId());
			attributes.put("revision", diskInfo.getRevision());
			attributes.put("serial number", diskInfo.getSerialNumber());
			attributes.put("storage", diskInfo.getStorage());
			attributes.put("capacity", diskInfo.getCapacity());

			// Add the Disk
			update.addNode(diskDisplayName, attributes);

			// Add disk endpoint
			String diskEndpointName = diskDisplayName;
			update.addEndpoint(diskDisplayName, diskEndpointName, new HashMap<String, String>());
			diskEndPoints.put(diskInfo.getName(), diskEndpointName);

			// Add the bus endpoint
			String bus = diskInfo.getBus();
			String busEndpointName = createOrGetBusEndpointOnSystem(update, bus, systemName, busEndPoints);

			// add link to system
			Map<String, String> linkAttributes = new HashMap<String, String>();
			linkAttributes.put("description", "Storage in system");
			String linkName = "DiskConnection" + diskDisplayName + "-" + bus;
			update.addLink(diskEndpointName, busEndpointName, linkName, linkAttributes);
		}
		return diskEndPoints;
	}

	private String createOrGetBusEndpointOnSystem(ModelUpdate update, String bus, String systemName, Map<String, String> busEndPoints) {
		String busEndpointName = busEndPoints.get(bus);
		if (busEndpointName == null) {
			busEndpointName = systemName + "Bus" + bus;
			busEndPoints.put(bus, busEndpointName);
			update.addEndpoint(systemName, busEndpointName,new HashMap<String, String>());
		}

		return busEndpointName;
	}

	private void addRaidGroups(ModelUpdate update, Map<String, String> diskEndpointNames) {
		// Build disks
		List<RaidGroupInfo> allRaidGroups = emcDao.getAllRaidGroups();

		for (RaidGroupInfo raidGroupInfo : allRaidGroups) {
			String raidGroupDisplayName = "RaidGroup" +raidGroupInfo.getId();

			Map<String, String> attributes = new HashMap<String, String>();
			attributes.put("type", "Cloud");
			attributes.put("name", raidGroupDisplayName);
			attributes.put("id", raidGroupInfo.getId());
			attributes.put("raid type", raidGroupInfo.getRaidType());
			attributes.put("state", raidGroupInfo.getState());
			attributes.put("raw capacity", raidGroupInfo.getRawCapacity());
			attributes.put("logical capacity", raidGroupInfo.getLogicalCapacity());
			attributes.put("used capacity", raidGroupInfo.getUsedCapacity());

			// Add the Raid Group
			log.debug("Adding Node [" + raidGroupDisplayName + "], [" + attributes + "]");
			update.addNode(raidGroupDisplayName, attributes);

			// Add Raid Group Endpoint
			String raidGroupEndpointName = raidGroupDisplayName;
			log.debug("Adding Endpoint [" + raidGroupDisplayName + "], [" + attributes + "]");
			update.addEndpoint(raidGroupDisplayName, raidGroupEndpointName, new HashMap<String, String>());

			// Add links to Disks
			String[] diskNames = raidGroupInfo.getDisks().split(" ");
			log.debug("Got Disks " + Arrays.toString(diskNames));
			for (String diskName : diskNames) {
				String diskEndpointName = diskEndpointNames.get(diskName);
				Map<String, String> linkAttributes = new HashMap<String, String>();
				linkAttributes.put("description", "Disk in raid group");
				String linkName = "RaidConnection" + raidGroupDisplayName + "-" + diskName;
				log.info("Adding link [" + diskEndpointName + "], [" + raidGroupEndpointName + "], [" + linkName + "], [" + linkAttributes);
				update.addLink(diskEndpointName, raidGroupEndpointName, linkName, linkAttributes);
			}
		}
	}

	private void addStorageProcessors(ModelUpdate update, String systemEndpointName) {
		// Build disks
		List<StorageProcessorInfo> allStorageProcessors = emcDao.getAllStorageProcessors();

		for (StorageProcessorInfo storageProcessors : allStorageProcessors) {
			Map<String, String> attributes = new HashMap<String, String>();
			attributes.put("type", "Server");

			attributes.put("id", storageProcessors.getId());
			attributes.put("signature", storageProcessors.getSignature());
			attributes.put("microcode rev", storageProcessors.getMicrocodeRev());
			attributes.put("serial number", storageProcessors.getSerialNumber());
			attributes.put("prom rev", storageProcessors.getPromRev());
			attributes.put("agent rev", storageProcessors.getAgentRev());
			attributes.put("physical memory size", storageProcessors.getPhysicalMemorySize());
			attributes.put("system buffer size", storageProcessors.getSystemBufferSize());
			attributes.put("read cache size", storageProcessors.getReadCacheSize());
			attributes.put("write cache size", storageProcessors.getWriteCacheSize());
			attributes.put("free memory size", storageProcessors.getFreeMemorySize());
			attributes.put("raid 3 memory size", storageProcessors.getRaid3MemorySize());
			attributes.put("storage", storageProcessors.getStorage());

			// Add the Storage Processor Group
			String storageProcessorDisplayName = "StorageProcessor" +storageProcessors.getId();
			log.info("Adding Node [" + storageProcessorDisplayName + "], [" + attributes + "]");
			update.addNode(storageProcessorDisplayName, attributes);

			// Add Storage Processor Endpoint
			String storageProcessorEndpointName = storageProcessorDisplayName;
			log.info("Adding Endpoint [" + storageProcessorDisplayName + "], [" + attributes + "]");
			update.addEndpoint(storageProcessorDisplayName, storageProcessorEndpointName, new HashMap<String, String>());

			// Add links to System
			String linkName = "StorageProcessor" + storageProcessorDisplayName + "-" + systemEndpointName;
			Map<String, String> linkAttributes = new HashMap<String, String>();
			linkAttributes.put("description", "Storage Proessor");
			log.info("Adding link [" + systemEndpointName + "], [" + storageProcessorEndpointName + "], [" + linkName + "], [" + linkAttributes);
			update.addLink(systemEndpointName, storageProcessorEndpointName, linkName, linkAttributes);

		}
	}
}
