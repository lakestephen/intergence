package com.intergence.hgsrest.emc.data.bean;

/**
 * EMC Data representation of a General Config
 *
 * @author stephen
 */
public class GeneralConfigInfo {
	private final String name;
	private final String uid;
	private final String modelNumber;
	private final String modelType;
	private final String clariionDevices;
	private final String physicalDisks;
	private final String visibleDevices;
	private final String raidGroups;
	private final String storageGroups;
	private final String snapshot;
	private final String cachePageSize;
	private final String lowWaterMark;
	private final String highWaterMark;
	private final String unassignedCachePages;
	private final String storage;

	public GeneralConfigInfo(String name, String uid, String modelNumber, String modelType, String clariionDevices, String physicalDisks, String visibleDevices, String raidGroups, String storageGroups, String snapshot, String cachePageSize, String lowWaterMark, String highWaterMark, String unassignedCachePages, String storage) {
		this.name = name;
		this.uid = uid;
		this.modelNumber = modelNumber;
		this.modelType = modelType;
		this.clariionDevices = clariionDevices;
		this.physicalDisks = physicalDisks;
		this.visibleDevices = visibleDevices;
		this.raidGroups = raidGroups;
		this.storageGroups = storageGroups;
		this.snapshot = snapshot;
		this.cachePageSize = cachePageSize;
		this.lowWaterMark = lowWaterMark;
		this.highWaterMark = highWaterMark;
		this.unassignedCachePages = unassignedCachePages;
		this.storage = storage;
	}

	public String getName() {
		return name;
	}

	public String getUid() {
		return uid;
	}

	public String getModelNumber() {
		return modelNumber;
	}

	public String getModelType() {
		return modelType;
	}

	public String getClariionDevices() {
		return clariionDevices;
	}

	public String getPhysicalDisks() {
		return physicalDisks;
	}

	public String getVisibleDevices() {
		return visibleDevices;
	}

	public String getRaidGroups() {
		return raidGroups;
	}

	public String getStorageGroups() {
		return storageGroups;
	}

	public String getSnapshot() {
		return snapshot;
	}

	public String getCachePageSize() {
		return cachePageSize;
	}

	public String getLowWaterMark() {
		return lowWaterMark;
	}

	public String getHighWaterMark() {
		return highWaterMark;
	}

	public String getUnassignedCachePages() {
		return unassignedCachePages;
	}

	public String getStorage() {
		return storage;
	}

	@Override
	public String toString() {
		return "GeneralConfigInfo{" +
				"name='" + name + '\'' +
				", uid='" + uid + '\'' +
				", modelNumber='" + modelNumber + '\'' +
				", modelType='" + modelType + '\'' +
				", clariionDevices='" + clariionDevices + '\'' +
				", physicalDisks='" + physicalDisks + '\'' +
				", visibleDevices='" + visibleDevices + '\'' +
				", raidGroups='" + raidGroups + '\'' +
				", storageGroups='" + storageGroups + '\'' +
				", snapshot='" + snapshot + '\'' +
				", cachePageSize='" + cachePageSize + '\'' +
				", lowWaterMark='" + lowWaterMark + '\'' +
				", highWaterMark='" + highWaterMark + '\'' +
				", unassignedCachePages='" + unassignedCachePages + '\'' +
				", storage='" + storage + '\'' +
				'}';
	}
}
