package com.intergence.hgsrest.emc.data.bean;

/**
 * EMC Data representation of a Disk
 *
 * @author stephen
 */
public class DiskInfo {

	private final String name;
	private final String bus;
	private final String enclosureNumber;
	private final String diskNumber;
	private final String state;
	private final String vendorId;
	private final String productId;
	private final String revision;
	private final String serialNumber;
	private final String capacity;
	private final String usedCapacity;
	private final String remappedBlocks;
	private final String storage;

	public DiskInfo(String name, String bus, String enclosureNumber, String diskNumber, String state, String vendorId, String productId, String revision, String serialNumber, String capacity, String usedCapacity, String remappedBlocks, String storage) {
		this.name = name;
		this.bus = bus;
		this.enclosureNumber = enclosureNumber;
		this.diskNumber = diskNumber;
		this.state = state;
		this.vendorId = vendorId;
		this.productId = productId;
		this.revision = revision;
		this.serialNumber = serialNumber;
		this.capacity = capacity;
		this.usedCapacity = usedCapacity;
		this.remappedBlocks = remappedBlocks;
		this.storage = storage;
	}

	public String getName() {
		return name;
	}

	public String getBus() {
		return bus;
	}

	public String getEnclosureNumber() {
		return enclosureNumber;
	}

	public String getDiskNumber() {
		return diskNumber;
	}

	public String getState() {
		return state;
	}

	public String getVendorId() {
		return vendorId;
	}

	public String getProductId() {
		return productId;
	}

	public String getRevision() {
		return revision;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public String getCapacity() {
		return capacity;
	}

	public String getUsedCapacity() {
		return usedCapacity;
	}

	public String getRemappedBlocks() {
		return remappedBlocks;
	}

	public String getStorage() {
		return storage;
	}

	@Override
	public String toString() {
		return "DiskInfo{" +
				"name='" + name + '\'' +
				", bus='" + bus + '\'' +
				", enclosureNumber='" + enclosureNumber + '\'' +
				", diskNumber='" + diskNumber + '\'' +
				", state='" + state + '\'' +
				", vendorId='" + vendorId + '\'' +
				", productId='" + productId + '\'' +
				", revision='" + revision + '\'' +
				", serialNumber='" + serialNumber + '\'' +
				", capacity='" + capacity + '\'' +
				", usedCapacity='" + usedCapacity + '\'' +
				", remappedBlocks='" + remappedBlocks + '\'' +
				", storage='" + storage + '\'' +
				'}';
	}
}
