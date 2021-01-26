package com.intergence.hgs.emc.data.bean;

/**
 * TODO Comments ???
 *
 * @author Stephen
 */
public class RaidGroupInfo {

	String id;
	String raidType;
	String state;
	String rawCapacity;
	String logicalCapacity;
	String usedCapacity;
	String disks;
	String devices;
	String storage;

	public RaidGroupInfo(String id, String raidType, String state, String rawCapacity, String logicalCapacity, String usedCapacity, String disks, String devices, String storage) {
		this.id = id;
		this.raidType = raidType;
		this.state = state;
		this.rawCapacity = rawCapacity;
		this.logicalCapacity = logicalCapacity;
		this.usedCapacity = usedCapacity;
		this.disks = disks;
		this.devices = devices;
		this.storage = storage;
	}

	public String getId() {
		return id;
	}

	public String getRaidType() {
		return raidType;
	}

	public String getState() {
		return state;
	}

	public String getRawCapacity() {
		return rawCapacity;
	}

	public String getLogicalCapacity() {
		return logicalCapacity;
	}

	public String getUsedCapacity() {
		return usedCapacity;
	}

	public String getDisks() {
		return disks;
	}

	public String getDevices() {
		return devices;
	}

	public String getStorage() {
		return storage;
	}

	@Override
	public String toString() {
		return "RaidGroupInfo{" +
				"id='" + id + '\'' +
				", raidType='" + raidType + '\'' +
				", state='" + state + '\'' +
				", rawCapacity='" + rawCapacity + '\'' +
				", logicalCapacity='" + logicalCapacity + '\'' +
				", usedCapacity='" + usedCapacity + '\'' +
				", disks='" + disks + '\'' +
				", devices='" + devices + '\'' +
				", storage='" + storage + '\'' +
				'}';
	}
}
