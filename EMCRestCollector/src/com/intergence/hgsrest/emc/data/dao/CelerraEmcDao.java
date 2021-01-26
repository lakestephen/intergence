package com.intergence.hgsrest.emc.data.dao;

import com.intergence.hgsrest.emc.connector.EmcConnectorManager;
import com.intergence.hgsrest.emc.data.bean.ControlStationInfo;
import com.intergence.hgsrest.emc.data.bean.DiskInfo;
import com.intergence.hgsrest.emc.data.bean.GeneralConfigInfo;
import com.intergence.hgsrest.emc.data.bean.RaidGroupInfo;
import com.intergence.hgsrest.emc.data.bean.StorageProcessorInfo;
import com.intergence.hgsrest.emc.data.bean.SystemInfo;
import org.apache.log4j.Logger;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Implementation of the EMC Dao that uses the celerra API to connect.
 *
 * @author stephen
 */
public class CelerraEmcDao implements EmcDao {

	private final Logger log = Logger.getLogger(this.getClass());

	private EmcConnectorManager emcConnectorManager;


	private static final String GET_SYSTEM_INFO_QUERY =
			"<RequestPacket xmlns=\"http://www.emc.com/schemas/celerra/xml_api\" apiVersion=\"V1_1\">\n" +
					"\t<Request>\n" +
					"\t\t<Query>\n" +
					"\t\t\t<CelerraSystemQueryParams />\n" +
					"\t\t</Query>\n" +
					"\t</Request>\n" +
					"</RequestPacket>";

	@Override
	public List<SystemInfo> getSystemInfo() {
		List<SystemInfo> result = new ArrayList<SystemInfo>();

		try {
			String response = emcConnectorManager.makeCall(GET_SYSTEM_INFO_QUERY);
			Element root = getRootElement(response);
			Element responseTag = root.element("Response");

			// iterate through child elements of root, but only take the first
			for ( Iterator i = responseTag.elementIterator("CelerraSystem"); i.hasNext(); ) {
				Element element = (Element) i.next();
				String type = safeGetAttributeValue(element, "type");
				String serial = safeGetAttributeValue(element, "serial");
				String productName = safeGetAttributeValue(element, "productName");
				String wwCid = safeGetAttributeValue(element, "wwCid");
				String version = safeGetAttributeValue(element, "version");
				String celerra = safeGetAttributeValue(element, "celerra");

				SystemInfo systemInfo = new SystemInfo(type, serial, productName, wwCid, version, celerra);
				result.add(systemInfo);
			}

		} catch (UnsupportedEncodingException e) {
			log.error("TODO", e);
		} catch (DocumentException e) {
			log.error("TODO", e);
		}

		return result;
	}

	private static final String GET_GENERAL_CONFIG_QUERY =
			"<RequestPacket xmlns=\"http://www.emc.com/schemas/celerra/xml_api\" apiVersion=\"V1_1\">\n" +
			"\t<RequestEx>\n" +
			"\t\t<Query>\n" +
			"\t\t\t<ClariionGeneralConfigQueryParams clariion=\"1\"></ClariionGeneralConfigQueryParams>\n" +
			"\t\t</Query>\n" +
			"\t</RequestEx>\n" +
			"</RequestPacket>";

	@Override
	public List<GeneralConfigInfo> getGeneralConfig() {
		List<GeneralConfigInfo> result = new ArrayList<GeneralConfigInfo>();

		try {
			String response = emcConnectorManager.makeCall(GET_GENERAL_CONFIG_QUERY);
			Element root = getRootElement(response);
			Element responseTag = root.element("ResponseEx");

			// iterate through child elements of root, but only take the first
			for ( Iterator i = responseTag.elementIterator("ClariionConfig"); i.hasNext(); ) {
				Element element = (Element) i.next();
				String name = safeGetAttributeValue(element, "name");
				String uid = safeGetAttributeValue(element, "uid");
				String modelNumber = safeGetAttributeValue(element, "modelNumber");
				String modelType = safeGetAttributeValue(element, "modelType");
				String clariionDevices = safeGetAttributeValue(element, "clariionDevices");
				String physicalDisks = safeGetAttributeValue(element, "physicalDisks");
				String visibleDevices = safeGetAttributeValue(element, "visibleDevices");
				String raidGroups = safeGetAttributeValue(element, "raidGroups");
				String storageGroups = safeGetAttributeValue(element, "storageGroups");
				String snapshot = safeGetAttributeValue(element, "snapshot");
				String cachePageSize = safeGetAttributeValue(element, "cachePageSize");
				String lowWaterMark = safeGetAttributeValue(element, "lowWaterMark");
				String highWaterMark = safeGetAttributeValue(element, "highWaterMark");
				String unassignedCachePages = safeGetAttributeValue(element, "unassignedCachePages");
				String storage = safeGetAttributeValue(element, "storage");

				GeneralConfigInfo generalConfigInfo = new GeneralConfigInfo(name, uid, modelNumber, modelType, clariionDevices, physicalDisks, visibleDevices, raidGroups,storageGroups, snapshot, cachePageSize, lowWaterMark, highWaterMark, unassignedCachePages, storage);
				result.add(generalConfigInfo);
			}

		} catch (UnsupportedEncodingException e) {
			log.error("TODO", e);
		} catch (DocumentException e) {
			log.error("TODO", e);
		}

		return result;
	}

	private static final String GET_CONTROL_STATION_QUERY =
			"<RequestPacket xmlns=\"http://www.emc.com/schemas/celerra/xml_api\">\n" +
			"\t<Request>\n" +
			"\t\t<Query>\n" +
			"\t\t\t<ControlStationQueryParams/>\n" +
			"\t\t</Query>\n" +
			"\t</Request>\n" +
			"</RequestPacket>\n";

	@Override
	public List<ControlStationInfo> getControlStation() {
		List<ControlStationInfo> result = new ArrayList<ControlStationInfo>();

		try {
			String response = emcConnectorManager.makeCall(GET_CONTROL_STATION_QUERY);

			Element root = getRootElement(response);
			Element responseExTag = root.element("Response");

			// iterate through child elements of root
			for ( Iterator i = responseExTag.elementIterator("ControlStation"); i.hasNext(); ) {
				Element element = (Element) i.next();
				String type = safeGetAttributeValue(element, "type");
				String dnsServers = safeGetAttributeValue(element, "dnsServers");
				String version = safeGetAttributeValue(element, "version");
				String hostname = safeGetAttributeValue(element, "hostname");
				String address = safeGetAttributeValue(element, "address");
				String netmask = safeGetAttributeValue(element, "netmask");
				String gateway = safeGetAttributeValue(element, "gateway");
				String dnsDomain = safeGetAttributeValue(element, "dnsDomain");
				String time = safeGetAttributeValue(element, "time");
				String timeZone = safeGetAttributeValue(element, "timeZone");
				String slot = safeGetAttributeValue(element, "slot");

				ControlStationInfo controlStationInfo = new ControlStationInfo(type, dnsServers, version, hostname, address, netmask, gateway, dnsDomain, time, timeZone, slot);

				result.add(controlStationInfo);
			}

		} catch (UnsupportedEncodingException e) {
			log.error("TODO", e);
		} catch (DocumentException e) {
			log.error("TODO", e);
		}

		return result;
	}

	private static final String GET_ALL_DISK_QUERY =
			"<RequestPacket xmlns=\"http://www.emc.com/schemas/celerra/xml_api\" apiVersion=\"V1_1\">\n" +
			"\t<RequestEx>\n" +
			"\t\t<Query>\n" +
			"\t\t\t<ClariionDiskQueryParams clariion=\"1\"></ClariionDiskQueryParams>\n" +
			"\t\t</Query>\n" +
			"\t</RequestEx>\n" +
			"</RequestPacket>";

	@Override
	public List<DiskInfo> getAllDisks() {
		List<DiskInfo> result = new ArrayList<DiskInfo>();

		try {
			String response = emcConnectorManager.makeCall(GET_ALL_DISK_QUERY);

			Element root = getRootElement(response);
			Element responseExTag = root.element("ResponseEx");

			// iterate through child elements of root
			for ( Iterator i = responseExTag.elementIterator("ClariionDiskConfig"); i.hasNext(); ) {
				Element element = (Element) i.next();
				String bus = safeGetAttributeValue(element, "bus");
				String enclosureNumber = safeGetAttributeValue(element, "enclosureNumber");
				String diskNumber = safeGetAttributeValue(element, "diskNumber");
				String state = safeGetAttributeValue(element, "state");
				String vendorId = safeGetAttributeValue(element, "vendorId");
				String productId = safeGetAttributeValue(element, "productId");
				String revision = safeGetAttributeValue(element, "revision");
				String serialNumber = safeGetAttributeValue(element, "serialNumber");
				String capacity = safeGetAttributeValue(element, "capacity");
				String usedCapacity = safeGetAttributeValue(element, "usedCapacity");
				String remappedBlocks = safeGetAttributeValue(element, "remappedBlocks");
				String storage = safeGetAttributeValue(element, "storage");
				String name = safeGetAttributeValue(element, "name");
				DiskInfo diskInfo = new DiskInfo(name, bus, enclosureNumber, diskNumber, state,vendorId,productId,revision,serialNumber,capacity,usedCapacity,remappedBlocks,storage);

				result.add(diskInfo);
			}

		} catch (UnsupportedEncodingException e) {
			log.error("TODO", e);
		} catch (DocumentException e) {
			log.error("TODO", e);
		}

		return result;
	}

	private static final String GET_ALL_RAID_GROUP_QUERY =
			"<RequestPacket xmlns=\"http://www.emc.com/schemas/celerra/xml_api\" apiVersion=\"V1_1\">\n" +
			"\t<RequestEx>\n" +
			"\t\t<Query>\n" +
			"\t\t\t<ClariionRaidGroupQueryParams clariion=\"1\" />\n" +
			"\t\t</Query>\n" +
			"\t</RequestEx>\n" +
			"</RequestPacket>";

	@Override
	public List<RaidGroupInfo> getAllRaidGroups() {
		List<RaidGroupInfo> result = new ArrayList<RaidGroupInfo>();

		try {
			String response = emcConnectorManager.makeCall(GET_ALL_RAID_GROUP_QUERY);

			Element root = getRootElement(response);
			Element responseExTag = root.element("ResponseEx");

			// iterate through child elements of root
			for ( Iterator i = responseExTag.elementIterator("ClariionRaidGroupConfig"); i.hasNext(); ) {
				Element element = (Element) i.next();

				String id = safeGetAttributeValue(element, "id");
				String raidType = safeGetAttributeValue(element, "raidType");
				String state = safeGetAttributeValue(element, "state");
				String rawCapacity = safeGetAttributeValue(element, "rawCapacity");
				String logicalCapacity = safeGetAttributeValue(element, "logicalCapacity");
				String usedCapacity = safeGetAttributeValue(element, "usedCapacity");
				String disks = safeGetAttributeValue(element, "disks");
				String devices = safeGetAttributeValue(element, "devices");
				String storage = safeGetAttributeValue(element, "storage");

				RaidGroupInfo raidGroupInfo = new RaidGroupInfo(id, raidType,state, rawCapacity, logicalCapacity, usedCapacity, disks, devices, storage);

				result.add(raidGroupInfo);
			}

		} catch (UnsupportedEncodingException e) {
			log.error("TODO", e);
		} catch (DocumentException e) {
			log.error("TODO", e);
		}

		return result;
	}

	public static final String GET_ALL_STORAGE_PROCESSOR_QUERY =
			"<RequestPacket xmlns=\"http://www.emc.com/schemas/celerra/xml_api\" apiVersion=\"V1_1\">\n" +
			"\t<RequestEx>\n" +
			"\t\t<Query>\n" +
			"\t\t\t<ClariionSpQueryParams clariion=\"1\">\n" +
			"\t\t\t\t<AspectSelection status=\"true\" config=\"true\"></AspectSelection>\n" +
			"\t\t\t</ClariionSpQueryParams>\n" +
			"\t\t</Query>\n" +
			"\t</RequestEx>\n" +
			"</RequestPacket>";

	@Override
	public List<StorageProcessorInfo> getAllStorageProcessors() {
		List<StorageProcessorInfo> result = new ArrayList<StorageProcessorInfo>();

		try {
			String response = emcConnectorManager.makeCall(GET_ALL_STORAGE_PROCESSOR_QUERY);

			Element root = getRootElement(response);
			Element responseExTag = root.element("ResponseEx");

			// iterate through child elements of root
			for ( Iterator i = responseExTag.elementIterator("ClariionSPConfig"); i.hasNext(); ) {
				Element element = (Element) i.next();

				String id = safeGetAttributeValue(element, "id");
				String signature = safeGetAttributeValue(element, "signature");
				String microcodeRev = safeGetAttributeValue(element, "microcodeRev");
				String serialNumber = safeGetAttributeValue(element, "serialNumber");
				String promRev = safeGetAttributeValue(element, "promRev");
				String physicalMemorySize = safeGetAttributeValue(element, "physicalMemorySize");
				String systemBufferSize = safeGetAttributeValue(element, "systemBufferSize");
				String readCacheSize = safeGetAttributeValue(element, "readCacheSize");
				String writeCacheSize = safeGetAttributeValue(element, "writeCacheSize");
				String freeMemorySize = safeGetAttributeValue(element, "freeMemorySize");
				String raid3MemorySize = safeGetAttributeValue(element, "raid3MemorySize");
				String storage = safeGetAttributeValue(element, "storage");

				StorageProcessorInfo storageProcessorInfo = new StorageProcessorInfo(id, signature, microcodeRev, serialNumber, promRev, microcodeRev, physicalMemorySize, systemBufferSize, readCacheSize, writeCacheSize, freeMemorySize, raid3MemorySize, storage);

				result.add(storageProcessorInfo);
			}

		} catch (UnsupportedEncodingException e) {
			log.error("TODO", e);
		} catch (DocumentException e) {
			log.error("TODO", e);
		}

		return result;
	}

	private Element getRootElement(String response) throws UnsupportedEncodingException, DocumentException {
		SAXReader reader = new SAXReader();
		InputStream stream = new ByteArrayInputStream(response.getBytes("UTF-8"));
		Document document = reader.read(stream);
		return document.getRootElement();
	}

	private String safeGetAttributeValue(Element element, String attributeName) {
		Attribute attribute = element.attribute(attributeName);
		if (attribute == null) {
			return null;
		}
		return attribute.getValue();
	}

	public void setEmcConnectorManager(EmcConnectorManager emcConnectorManager) {
		this.emcConnectorManager = emcConnectorManager;
	}
}