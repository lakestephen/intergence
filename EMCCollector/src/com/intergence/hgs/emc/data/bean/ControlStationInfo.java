package com.intergence.hgs.emc.data.bean;

/**
 * TODO Comments ???
 *
 * @author Stephen
 */
public class ControlStationInfo {

	private final String type;
	private final String dnsServers;
	private final String version;
	private final String hostname;
	private final String address;
	private final String netmask;
	private final String gateway;
	private final String dnsDomain;
	private final String time;
	private final String timeZone;
	private final String slot;


	public ControlStationInfo(String type, String dnsServers, String version, String hostname, String address, String netmask, String gateway, String dnsDomain, String time, String timeZone, String slot) {
		this.type = type;
		this.dnsServers = dnsServers;
		this.version = version;
		this.hostname = hostname;
		this.address = address;
		this.netmask = netmask;
		this.gateway = gateway;
		this.dnsDomain = dnsDomain;
		this.time = time;
		this.timeZone = timeZone;
		this.slot = slot;
	}

	public String getType() {
		return type;
	}

	public String getDnsServers() {
		return dnsServers;
	}

	public String getVersion() {
		return version;
	}

	public String getHostname() {
		return hostname;
	}

	public String getAddress() {
		return address;
	}

	public String getNetmask() {
		return netmask;
	}

	public String getGateway() {
		return gateway;
	}

	public String getDnsDomain() {
		return dnsDomain;
	}

	public String getTime() {
		return time;
	}

	public String getTimeZone() {
		return timeZone;
	}

	public String getSlot() {
		return slot;
	}

	@Override
	public String toString() {
		return "ControlStationInfo{" +
				"type='" + type + '\'' +
				", dnsServers='" + dnsServers + '\'' +
				", version='" + version + '\'' +
				", hostname='" + hostname + '\'' +
				", address='" + address + '\'' +
				", netmask='" + netmask + '\'' +
				", gateway='" + gateway + '\'' +
				", dnsDomain='" + dnsDomain + '\'' +
				", time='" + time + '\'' +
				", timeZone='" + timeZone + '\'' +
				", slot='" + slot + '\'' +
				'}';
	}
}
