package com.intergence.hgsrest.emc.data.bean;

/**
 * EMC Data representation of a System
 *
 * @author stephen
 */
public class SystemInfo {

	private final String type;
	private final String serial;
	private final String productName;
	private final String wwCid;
	private final String version;
	private final String celerra;

	public SystemInfo(String type, String serial, String productName, String wwCid, String version, String celerra) {
		this.type = type;
		this.serial = serial;
		this.productName = productName;
		this.wwCid = wwCid;
		this.version = version;
		this.celerra = celerra;
	}

	public String getType() {
		return type;
	}

	public String getSerial() {
		return serial;
	}

	public String getProductName() {
		return productName;
	}

	public String getWwCid() {
		return wwCid;
	}

	public String getVersion() {
		return version;
	}

	public String getCelerra() {
		return celerra;
	}

	@Override
	public String toString() {
		return "SystemInfo{" +
				"type='" + type + '\'' +
				", serial='" + serial + '\'' +
				", productName='" + productName + '\'' +
				", wwCid='" + wwCid + '\'' +
				", version='" + version + '\'' +
				", celerra='" + celerra + '\'' +
				'}';
	}
}
