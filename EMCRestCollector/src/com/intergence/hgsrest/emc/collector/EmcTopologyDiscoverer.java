package com.intergence.hgsrest.emc.collector;

import com.intergence.hgsrest.collection.DiscoveryExecutor;
import com.intergence.hgsrest.collection.SkeletalDiscoveryExecutor;
import com.intergence.hgsrest.emc.data.bean.ControlStationInfo;
import com.intergence.hgsrest.emc.data.bean.DiskInfo;
import com.intergence.hgsrest.emc.data.bean.GeneralConfigInfo;
import com.intergence.hgsrest.emc.data.bean.RaidGroupInfo;
import com.intergence.hgsrest.emc.data.bean.StorageProcessorInfo;
import com.intergence.hgsrest.emc.data.bean.SystemInfo;
import com.intergence.hgsrest.emc.data.dao.EmcDao;
import com.intergence.hgsrest.model.update.DiscoveryType;
import com.intergence.hgsrest.model.update.ModelUpdate;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Find the topology that the EMC system supports.
 * Called by the HyperGlance framework
 *
 * @author Stephen Lake
 */
public class EmcTopologyDiscoverer extends SkeletalDiscoveryExecutor implements DiscoveryExecutor {

	private final Logger log = Logger.getLogger(this.getClass());

	private EmcDao emcDao;

	@Override
	public void execute(ModelUpdate update) {
		checkNotNull(emcDao, "Please supply a emcDao");

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
			putAttributeIfNotNull("name", controlStationInfo.getHostname(), attributes);
			putAttributeIfNotNull("ip address", controlStationInfo.getAddress(), attributes);
			putAttributeIfNotNull("netmask", controlStationInfo.getNetmask(), attributes);
			putAttributeIfNotNull("control station type", controlStationInfo.getType(), attributes);
			putAttributeIfNotNull("control station version", controlStationInfo.getVersion(), attributes);
			putAttributeIfNotNull("dns servers", controlStationInfo.getDnsServers(), attributes);
			putAttributeIfNotNull("dns domain", controlStationInfo.getDnsDomain(), attributes);
			putAttributeIfNotNull("timezone", controlStationInfo.getTimeZone(), attributes);
			putAttributeIfNotNull("slot", controlStationInfo.getSlot(), attributes);

			// System Info
			putAttributeIfNotNull("product name", systemInfo.getProductName(), attributes);
			putAttributeIfNotNull("serial", systemInfo.getSerial(), attributes);
			putAttributeIfNotNull("ww cid", systemInfo.getWwCid(), attributes);
			putAttributeIfNotNull("system version", systemInfo.getVersion(), attributes);

			// General Info
			GeneralConfigInfo generalConfigByName = findGeneralConfigByName(generalConfigInfos, systemInfo.getSerial());
			putAttributeIfNotNull("uuid", generalConfigByName.getUid(), attributes);
			putAttributeIfNotNull("model number", generalConfigByName.getModelNumber(), attributes);
			putAttributeIfNotNull("model type", generalConfigByName.getModelType(), attributes);
			putAttributeIfNotNull("clariion devices", generalConfigByName.getClariionDevices(), attributes);
			putAttributeIfNotNull("physical disks", generalConfigByName.getPhysicalDisks(), attributes);
			putAttributeIfNotNull("visible devices", generalConfigByName.getVisibleDevices(), attributes);
			putAttributeIfNotNull("raid groups", generalConfigByName.getRaidGroups(), attributes);
			putAttributeIfNotNull("storage groups", generalConfigByName.getStorageGroups(), attributes);
			putAttributeIfNotNull("visible devices", generalConfigByName.getVisibleDevices(), attributes);

			// Add Node
			systemName = controlStationInfo.getHostname().replace(" ","");
			update.addNode(getCollectorName(), systemName, "storage", attributes, DiscoveryType.DISCOVERED);

			String systemEndpointName = systemName + "_Endpoint";
			update.addEndpoint(getCollectorName(), systemEndpointName, "system", systemName,  new HashMap<String, String>(), DiscoveryType.DISCOVERED);

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
			putAttributeIfNotNull("name", diskDisplayName, attributes);
			putAttributeIfNotNull("bus", diskInfo.getBus(), attributes);
			putAttributeIfNotNull("enclosure number", diskInfo.getEnclosureNumber(), attributes);
			putAttributeIfNotNull("disk number", diskInfo.getDiskNumber(), attributes);
			putAttributeIfNotNull("vendor id", diskInfo.getVendorId(), attributes);
			putAttributeIfNotNull("product id", diskInfo.getProductId(), attributes);
			putAttributeIfNotNull("revision", diskInfo.getRevision(), attributes);
			putAttributeIfNotNull("serial number", diskInfo.getSerialNumber(), attributes);
			putAttributeIfNotNull("storage", diskInfo.getStorage(), attributes);
			putAttributeIfNotNull("capacity", diskInfo.getCapacity(), attributes);

			// Add the Disk
			update.addNode(getCollectorName(), diskDisplayName, "Appliance", attributes, DiscoveryType.DISCOVERED);

			// Add disk endpoint
			String diskEndpointName = diskDisplayName + "_Endpoint";
			update.addEndpoint(getCollectorName(), diskEndpointName, "disk", diskDisplayName, new HashMap<String, String>(), DiscoveryType.DISCOVERED);
			diskEndPoints.put(diskInfo.getName(), diskEndpointName);

			// Add the bus endpoint
			String bus = diskInfo.getBus();
			String busEndpointName = createOrGetBusEndpointOnSystem(update, bus, systemName, busEndPoints);

			// add link to system
			Map<String, String> linkAttributes = new HashMap<String, String>();
			putAttributeIfNotNull("description", "Storage in system", linkAttributes);
			String linkName = "DiskConnection" + diskDisplayName + "-" + bus;
			update.addLink(getCollectorName(), linkName, "link", diskEndpointName, busEndpointName, linkAttributes, DiscoveryType.DISCOVERED);
		}
		return diskEndPoints;
	}

	private String createOrGetBusEndpointOnSystem(ModelUpdate update, String bus, String systemName, Map<String, String> busEndPoints) {
		String busEndpointName = busEndPoints.get(bus);
		if (busEndpointName == null) {
			busEndpointName = systemName + "Bus" + bus;
			busEndPoints.put(bus, busEndpointName);
			update.addEndpoint(getCollectorName(), busEndpointName, "bus", systemName, new HashMap<String, String>(), DiscoveryType.DISCOVERED);
		}

		return busEndpointName;
	}

	private void addRaidGroups(ModelUpdate update, Map<String, String> diskEndpointNames) {
		// Build disks
		List<RaidGroupInfo> allRaidGroups = emcDao.getAllRaidGroups();

		for (RaidGroupInfo raidGroupInfo : allRaidGroups) {
			String raidGroupDisplayName = "RaidGroup" +raidGroupInfo.getId();

			Map<String, String> attributes = new HashMap<String, String>();
			putAttributeIfNotNull("name", raidGroupDisplayName, attributes);
			putAttributeIfNotNull("id", raidGroupInfo.getId(), attributes);
			putAttributeIfNotNull("raid type", raidGroupInfo.getRaidType(), attributes);
			putAttributeIfNotNull("state", raidGroupInfo.getState(), attributes);
			putAttributeIfNotNull("raw capacity", raidGroupInfo.getRawCapacity(), attributes);
			putAttributeIfNotNull("logical capacity", raidGroupInfo.getLogicalCapacity(), attributes);
			putAttributeIfNotNull("used capacity", raidGroupInfo.getUsedCapacity(), attributes);

			// Add the Raid Group
			log.info("Adding Node [" + raidGroupDisplayName + "], [" + attributes + "]");
			update.addNode(getCollectorName(), raidGroupDisplayName, "cloud", attributes, DiscoveryType.DISCOVERED);

			// Add Raid Group Endpoint
			String raidGroupEndpointName = raidGroupDisplayName + "_Endpoint";
			log.info("Adding Endpoint [" + raidGroupDisplayName + "], [" + attributes + "]");
			update.addEndpoint(getCollectorName(), raidGroupEndpointName, "cloud", raidGroupDisplayName, new HashMap<String, String>(), DiscoveryType.DISCOVERED);

			// Add links to Disks
			String[] diskNames = raidGroupInfo.getDisks().split(" ");
			log.debug("Got Disks " + Arrays.toString(diskNames));
			for (String diskName : diskNames) {
				String diskEndpointName = diskEndpointNames.get(diskName);
				Map<String, String> linkAttributes = new HashMap<String, String>();
				putAttributeIfNotNull("description", "Disk in raid group", linkAttributes);
				String linkName = "RaidConnection" + raidGroupDisplayName + "-" + diskName;
				log.info("Adding link [" + diskEndpointName + "], [" + raidGroupEndpointName + "], [" + linkName + "], [" + linkAttributes);
				update.addLink(getCollectorName(), linkName, "link", diskEndpointName, raidGroupEndpointName, linkAttributes, DiscoveryType.DISCOVERED);
			}
		}
	}

	private void addStorageProcessors(ModelUpdate update, String systemEndpointName) {
        systemEndpointName += "_Endpoint";
        // Build disks
		List<StorageProcessorInfo> allStorageProcessors = emcDao.getAllStorageProcessors();

		for (StorageProcessorInfo storageProcessors : allStorageProcessors) {
			Map<String, String> attributes = new HashMap<String, String>();
			putAttributeIfNotNull("id", storageProcessors.getId(), attributes);
			putAttributeIfNotNull("signature", storageProcessors.getSignature(), attributes);
			putAttributeIfNotNull("microcode rev", storageProcessors.getMicrocodeRev(), attributes);
			putAttributeIfNotNull("serial number", storageProcessors.getSerialNumber(), attributes);
			putAttributeIfNotNull("prom rev", storageProcessors.getPromRev(), attributes);
			putAttributeIfNotNull("agent rev", storageProcessors.getAgentRev(), attributes);
			putAttributeIfNotNull("physical memory size", storageProcessors.getPhysicalMemorySize(), attributes);
			putAttributeIfNotNull("system buffer size", storageProcessors.getSystemBufferSize(), attributes);
			putAttributeIfNotNull("read cache size", storageProcessors.getReadCacheSize(), attributes);
			putAttributeIfNotNull("write cache size", storageProcessors.getWriteCacheSize(), attributes);
			putAttributeIfNotNull("free memory size", storageProcessors.getFreeMemorySize(), attributes);
			putAttributeIfNotNull("raid 3 memory size", storageProcessors.getRaid3MemorySize(), attributes);
			putAttributeIfNotNull("storage", storageProcessors.getStorage(), attributes);

			// Add the Storage Processor Group
			String storageProcessorDisplayName = "StorageProcessor" +storageProcessors.getId();
			log.info("Adding Node [" + storageProcessorDisplayName + "], [" + attributes + "]");
			update.addNode(getCollectorName(), storageProcessorDisplayName, "server", attributes, DiscoveryType.DISCOVERED);

			// Add Storage Processor Endpoint
			String storageProcessorEndpointName = storageProcessorDisplayName + "_Endpoint";
			log.info("Adding Endpoint [" + storageProcessorDisplayName + "], [" + attributes + "]");
			update.addEndpoint(getCollectorName(), storageProcessorEndpointName, "server", storageProcessorDisplayName, new HashMap<String, String>(), DiscoveryType.DISCOVERED);

			// Add links to System
			String linkName = "StorageProcessor" + storageProcessorDisplayName + "-" + systemEndpointName;
			Map<String, String> linkAttributes = new HashMap<String, String>();
			putAttributeIfNotNull("description", "Storage Proessor", linkAttributes);
			log.info("Adding link [" + systemEndpointName + "], [" + storageProcessorEndpointName + "], [" + linkName + "], [" + linkAttributes);
			update.addLink(getCollectorName(), linkName, "link", systemEndpointName, storageProcessorEndpointName, linkAttributes, DiscoveryType.DISCOVERED);

		}
	}

	private void putAttributeIfNotNull(String key, String value, Map<String, String> attributes) {
		if (key != null && value != null) {
			attributes.put(key, value);
		}
	}

	public void setEmcDao(EmcDao emcDao) {
		this.emcDao = emcDao;
	}

    @Override
    public String toString() {
        return "EmcTopologyDiscoverer{" +
                "hgsDatasourceName='" + getCollectorName() + '\'' +
				", executeOrder='" + getExecuteOrder() + '\'' +
				'}';
    }
}
