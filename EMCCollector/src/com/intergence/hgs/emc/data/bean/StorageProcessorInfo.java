package com.intergence.hgs.emc.data.bean;

/**
 * TODO comments.
 *
 * @author stephen
 */
public class StorageProcessorInfo {

	String id;
	String signature;
	String microcodeRev;
	String serialNumber;
	String promRev;
	String agentRev;
	String physicalMemorySize;
	String systemBufferSize;
	String readCacheSize;
	String writeCacheSize;
	String freeMemorySize;
	String raid3MemorySize;
	String storage;

	public StorageProcessorInfo(String id, String signature, String microcodeRev, String serialNumber, String promRev, String agentRev, String physicalMemorySize, String systemBufferSize, String readCacheSize, String writeCacheSize, String freeMemorySize, String raid3MemorySize, String storage) {
		this.id = id;
		this.signature = signature;
		this.microcodeRev = microcodeRev;
		this.serialNumber = serialNumber;
		this.promRev = promRev;
		this.agentRev = agentRev;
		this.physicalMemorySize = physicalMemorySize;
		this.systemBufferSize = systemBufferSize;
		this.readCacheSize = readCacheSize;
		this.writeCacheSize = writeCacheSize;
		this.freeMemorySize = freeMemorySize;
		this.raid3MemorySize = raid3MemorySize;
		this.storage = storage;
	}

	public String getId() {
		return id;
	}

	public String getSignature() {
		return signature;
	}

	public String getMicrocodeRev() {
		return microcodeRev;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public String getPromRev() {
		return promRev;
	}

	public String getAgentRev() {
		return agentRev;
	}

	public String getPhysicalMemorySize() {
		return physicalMemorySize;
	}

	public String getSystemBufferSize() {
		return systemBufferSize;
	}

	public String getReadCacheSize() {
		return readCacheSize;
	}

	public String getWriteCacheSize() {
		return writeCacheSize;
	}

	public String getFreeMemorySize() {
		return freeMemorySize;
	}

	public String getRaid3MemorySize() {
		return raid3MemorySize;
	}

	public String getStorage() {
		return storage;
	}

	@Override
	public String toString() {
		return "StorageProcessorInfo{" +
				"id='" + id + '\'' +
				", signature='" + signature + '\'' +
				", microcodeRev='" + microcodeRev + '\'' +
				", serialNumber='" + serialNumber + '\'' +
				", promRev='" + promRev + '\'' +
				", agentRev='" + agentRev + '\'' +
				", physicalMemorySize='" + physicalMemorySize + '\'' +
				", systemBufferSize='" + systemBufferSize + '\'' +
				", readCacheSize='" + readCacheSize + '\'' +
				", writeCacheSize='" + writeCacheSize + '\'' +
				", freeMemorySize='" + freeMemorySize + '\'' +
				", raid3MemorySize='" + raid3MemorySize + '\'' +
				", storage='" + storage + '\'' +
				'}';
	}
}
