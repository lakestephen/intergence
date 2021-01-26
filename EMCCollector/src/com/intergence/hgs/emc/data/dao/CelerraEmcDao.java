package com.intergence.hgs.emc.data.dao;

import com.intergence.hgs.emc.connector.EmcConnectorManager;
import com.intergence.hgs.emc.data.bean.*;
import org.apache.log4j.Logger;
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

	private final EmcConnectorManager emcConnectorManager;

	public CelerraEmcDao(EmcConnectorManager emcConnectorManager) {
		this.emcConnectorManager = emcConnectorManager;
		if (this.emcConnectorManager == null) {
			log.error("Running in Canned Data Mode");
		}
		else {
			log.info("Running in EMC Query Mode");
		}
	}

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
			String response = makeCall(GET_SYSTEM_INFO_QUERY, CelerraDaoCannedData.GET_SYSTEM_INFO_RESPONSE);
			Element root = getRootElement(response);
			Element responseTag = root.element("Response");

			// iterate through child elements of root, but only take the first
			for ( Iterator i = responseTag.elementIterator("CelerraSystem"); i.hasNext(); ) {
				Element element = (Element) i.next();
				String type = element.attribute("type").getValue();
				String serial = element.attribute("serial").getValue();
				String productName = element.attribute("productName").getValue();
				String wwCid = element.attribute("wwCid").getValue();
				String version = element.attribute("version").getValue();
				String celerra = element.attribute("celerra").getValue();

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
			String response = makeCall(GET_GENERAL_CONFIG_QUERY, CelerraDaoCannedData.GET_GENERAL_CONFIG_RESPONSE);
			Element root = getRootElement(response);
			Element responseTag = root.element("ResponseEx");

			// iterate through child elements of root, but only take the first
			for ( Iterator i = responseTag.elementIterator("ClariionConfig"); i.hasNext(); ) {
				Element element = (Element) i.next();
				String name = element.attribute("name").getValue();
				String uid = element.attribute("uid").getValue();
				String modelNumber = element.attribute("modelNumber").getValue();
				String modelType = element.attribute("modelType").getValue();
				String clariionDevices = element.attribute("clariionDevices").getValue();
				String physicalDisks = element.attribute("physicalDisks").getValue();
				String visibleDevices = element.attribute("visibleDevices").getValue();
				String raidGroups = element.attribute("raidGroups").getValue();
				String storageGroups = element.attribute("storageGroups").getValue();
				String snapshot = element.attribute("snapshot").getValue();
				String cachePageSize = element.attribute("cachePageSize").getValue();
				String lowWaterMark = element.attribute("lowWaterMark").getValue();
				String highWaterMark = element.attribute("highWaterMark").getValue();
				String unassignedCachePages = element.attribute("unassignedCachePages").getValue();
				String storage = element.attribute("storage").getValue();

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
			String response = makeCall(GET_CONTROL_STATION_QUERY, CelerraDaoCannedData.GET_CONTROL_STATION_RESPONSE);

			Element root = getRootElement(response);
			Element responseExTag = root.element("Response");

			// iterate through child elements of root
			for ( Iterator i = responseExTag.elementIterator("ControlStation"); i.hasNext(); ) {
				Element element = (Element) i.next();
				String type = element.attribute("type").getValue();
				String dnsServers = element.attribute("dnsServers").getValue();
				String version = element.attribute("version").getValue();
				String hostname = element.attribute("hostname").getValue();
				String address = element.attribute("address").getValue();
				String netmask = element.attribute("netmask").getValue();
				String gateway = element.attribute("gateway").getValue();
				String dnsDomain = element.attribute("dnsDomain").getValue();
				String time = element.attribute("time").getValue();
				String timeZone = element.attribute("timeZone").getValue();
				String slot = element.attribute("slot").getValue();

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
			String response = makeCall(GET_ALL_DISK_QUERY, CelerraDaoCannedData.GET_ALL_DISK_INFO_RESPONSE);

			Element root = getRootElement(response);
			Element responseExTag = root.element("ResponseEx");

			// iterate through child elements of root
			for ( Iterator i = responseExTag.elementIterator("ClariionDiskConfig"); i.hasNext(); ) {
				Element element = (Element) i.next();
				String bus = element.attribute("bus").getValue();
				String enclosureNumber = element.attribute("enclosureNumber").getValue();
				String diskNumber = element.attribute("diskNumber").getValue();
				String state = element.attribute("state").getValue();
				String vendorId = element.attribute("vendorId").getValue();
				String productId = element.attribute("productId").getValue();
				String revision = element.attribute("revision").getValue();
				String serialNumber = element.attribute("serialNumber").getValue();
				String capacity = element.attribute("capacity").getValue();
				String usedCapacity = element.attribute("usedCapacity").getValue();
				String remappedBlocks = element.attribute("remappedBlocks").getValue();
				String storage = element.attribute("storage").getValue();
				String name = element.attribute("name").getValue();
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
			String response = makeCall(GET_ALL_RAID_GROUP_QUERY, CelerraDaoCannedData.GET_ALL_RAID_GROUP_RESPONSE);

			Element root = getRootElement(response);
			Element responseExTag = root.element("ResponseEx");

			// iterate through child elements of root
			for ( Iterator i = responseExTag.elementIterator("ClariionRaidGroupConfig"); i.hasNext(); ) {
				Element element = (Element) i.next();

				String id = element.attribute("id").getValue();
				String raidType = element.attribute("raidType").getValue();
				String state = element.attribute("state").getValue();
				String rawCapacity = element.attribute("rawCapacity").getValue();
				String logicalCapacity = element.attribute("logicalCapacity").getValue();
				String usedCapacity = element.attribute("usedCapacity").getValue();
				String disks = element.attribute("disks").getValue();
				String devices = element.attribute("devices").getValue();
				String storage = element.attribute("storage").getValue();

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
			String response = makeCall(GET_ALL_STORAGE_PROCESSOR_QUERY, CelerraDaoCannedData.GET_ALL_STORAGE_PROCESSOR_RESPONSE);

			Element root = getRootElement(response);
			Element responseExTag = root.element("ResponseEx");

			// iterate through child elements of root
			for ( Iterator i = responseExTag.elementIterator("ClariionSPConfig"); i.hasNext(); ) {
				Element element = (Element) i.next();

				String id = element.attribute("id").getValue();
				String signature = element.attribute("signature").getValue();
				String microcodeRev = element.attribute("microcodeRev").getValue();
				String serialNumber = element.attribute("serialNumber").getValue();
				String promRev = element.attribute("promRev").getValue();
				String physicalMemorySize = element.attribute("physicalMemorySize").getValue();
				String systemBufferSize = element.attribute("systemBufferSize").getValue();
				String readCacheSize = element.attribute("readCacheSize").getValue();
				String writeCacheSize = element.attribute("writeCacheSize").getValue();
				String freeMemorySize = element.attribute("freeMemorySize").getValue();
				String raid3MemorySize = element.attribute("raid3MemorySize").getValue();
				String storage = element.attribute("storage").getValue();

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

	private String makeCall(String query, String cannedResponse) {
		String response;
		if (emcConnectorManager != null) {
			response = emcConnectorManager.makeCall(query);
		}
		else {
			response = cannedResponse;
		}
		return response;
	}

	private Element getRootElement(String response) throws UnsupportedEncodingException, DocumentException {
		SAXReader reader = new SAXReader();
		InputStream stream = new ByteArrayInputStream(response.getBytes("UTF-8"));
		Document document = reader.read(stream);
		return document.getRootElement();
	}
}