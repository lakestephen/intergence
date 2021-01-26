package com.intergence.hgs.emc.data.dao;

/**
 * TODO comments.
 *
 * @author stephen
 */
public class CelerraDaoCannedData {

	public static final String GET_SYSTEM_INFO_RESPONSE =
			"<ResponsePacket xmlns=\"http://www.emc.com/schemas/celerra/xml_api\">\n" +
			"    <Response>\n" +
			"        <QueryStatus maxSeverity=\"ok\"/>\n" +
			"        <CelerraSystem type=\"system\" serial=\"CKM00140700163\" productName=\"VNX5200\" wwCid=\"CKM001407001632007\" version=\"8.1.2-51 \" celerra=\"0\">\n" +
			"            <Status maxSeverity=\"ok\"/>\n" +
			"        </CelerraSystem>\n" +
			"    </Response>\n" +
			"</ResponsePacket>";

	public static final String GET_GENERAL_CONFIG_RESPONSE =
			"<ResponsePacket xmlns=\"http://www.emc.com/schemas/celerra/xml_api\" apiVersion=\"V1_1\">\n" +
			"    <ResponseEx>\n" +
			"        <ClariionConfig name=\"CKM00140700163\" uid=\"50060160886025fc0000000000000000\" modelNumber=\"Model-VNX5200\" modelType=\"rackmount\" clariionDevices=\"6\" physicalDisks=\"12\" visibleDevices=\"4\" raidGroups=\"2\" storageGroups=\"2\" snapshot=\"1396822434\" cachePageSize=\"8\" lowWaterMark=\"-1.0\" highWaterMark=\"-1.0\" unassignedCachePages=\"-1\" storage=\"1\"/>\n" +
			"    </ResponseEx>\n" +
			"</ResponsePacket>";

	public static final String GET_CONTROL_STATION_RESPONSE =
			"<ResponsePacket xmlns=\"http://www.emc.com/schemas/celerra/xml_api\">\n" +
			"    <Response>\n" +
			"        <QueryStatus maxSeverity=\"ok\"/>\n" +
			"        <ControlStation type=\"controlStation\" dnsServers=\"10.0.1.10 10.0.1.13\" version=\"8.1.2-51 \" hostname=\"EMC-Storage\" address=\"10.0.1.77\" netmask=\"255.255.255.0\" gateway=\"10.0.1.1\" dnsDomain=\"intergence.com\" time=\"1396822591\" timeZone=\"Europe/London\" slot=\"0\">\n" +
			"            <Status maxSeverity=\"ok\"/>\n" +
			"        </ControlStation>\n" +
			"    </Response>\n" +
			"</ResponsePacket>\n";

	public static final String GET_ALL_DISK_INFO_RESPONSE = "<ResponsePacket xmlns=\"http://www.emc.com/schemas/celerra/xml_api\" apiVersion=\"V1_1\">\n"+
			"    <ResponseEx>\n"+
			"        <ClariionDiskConfig bus=\"0\" enclosureNumber=\"0\" diskNumber=\"0\" state=\"unbound\" vendorId=\"HITACHI\" productId=\"HUC10906 CLAR600\" revision=\"C430\" serialNumber=\"KSJUKBTR\" capacity=\"1125767168\" usedCapacity=\"0\" remappedBlocks=\"18446744073709551615\" storage=\"1\" name=\"0_0_0\"/>\n"+
			"        <ClariionDiskConfig bus=\"0\" enclosureNumber=\"0\" diskNumber=\"1\" state=\"unbound\" vendorId=\"HITACHI\" productId=\"HUC10906 CLAR600\" revision=\"C430\" serialNumber=\"KSJUJP2R\" capacity=\"1125767168\" usedCapacity=\"0\" remappedBlocks=\"18446744073709551615\" storage=\"1\" name=\"0_0_1\"/>\n"+
			"        <ClariionDiskConfig bus=\"0\" enclosureNumber=\"0\" diskNumber=\"2\" state=\"unbound\" vendorId=\"HITACHI\" productId=\"HUC10906 CLAR600\" revision=\"C430\" serialNumber=\"KSJ7S9BR\" capacity=\"1125767168\" usedCapacity=\"0\" remappedBlocks=\"18446744073709551615\" storage=\"1\" name=\"0_0_2\"/>\n"+
			"        <ClariionDiskConfig bus=\"0\" enclosureNumber=\"0\" diskNumber=\"3\" state=\"unbound\" vendorId=\"HITACHI\" productId=\"HUC10906 CLAR600\" revision=\"C430\" serialNumber=\"KSJGJLJJ\" capacity=\"1125767168\" usedCapacity=\"0\" remappedBlocks=\"18446744073709551615\" storage=\"1\" name=\"0_0_3\"/>\n"+
			"        <ClariionDiskConfig bus=\"0\" enclosureNumber=\"0\" diskNumber=\"4\" state=\"enabled\" vendorId=\"HITACHI\" productId=\"HUC10906 CLAR600\" revision=\"C430\" serialNumber=\"KSJG084J\" capacity=\"1125767168\" usedCapacity=\"562838272\" remappedBlocks=\"18446744073709551615\" storage=\"1\" name=\"0_0_4\"/>\n"+
			"        <ClariionDiskConfig bus=\"0\" enclosureNumber=\"0\" diskNumber=\"5\" state=\"enabled\" vendorId=\"HITACHI\" productId=\"HUC10906 CLAR600\" revision=\"C430\" serialNumber=\"KSJUKBHR\" capacity=\"1125767168\" usedCapacity=\"562838272\" remappedBlocks=\"18446744073709551615\" storage=\"1\" name=\"0_0_5\"/>\n"+
			"        <ClariionDiskConfig bus=\"0\" enclosureNumber=\"0\" diskNumber=\"6\" state=\"enabled\" vendorId=\"HITACHI\" productId=\"HUC10906 CLAR600\" revision=\"C430\" serialNumber=\"KSJUVRLR\" capacity=\"1125767168\" usedCapacity=\"892547891\" remappedBlocks=\"18446744073709551615\" storage=\"1\" name=\"0_0_6\"/>\n"+
			"        <ClariionDiskConfig bus=\"0\" enclosureNumber=\"0\" diskNumber=\"7\" state=\"enabled\" vendorId=\"HITACHI\" productId=\"HUC10906 CLAR600\" revision=\"C430\" serialNumber=\"KSJGPUTJ\" capacity=\"1125767168\" usedCapacity=\"892547891\" remappedBlocks=\"18446744073709551615\" storage=\"1\" name=\"0_0_7\"/>\n"+
			"        <ClariionDiskConfig bus=\"0\" enclosureNumber=\"0\" diskNumber=\"8\" state=\"enabled\" vendorId=\"HITACHI\" productId=\"HUC10906 CLAR600\" revision=\"C430\" serialNumber=\"KSJUNLER\" capacity=\"1125767168\" usedCapacity=\"892547891\" remappedBlocks=\"18446744073709551615\" storage=\"1\" name=\"0_0_8\"/>\n"+
			"        <ClariionDiskConfig bus=\"0\" enclosureNumber=\"0\" diskNumber=\"9\" state=\"enabled\" vendorId=\"HITACHI\" productId=\"HUC10906 CLAR600\" revision=\"C430\" serialNumber=\"KSJUK0MR\" capacity=\"1125767168\" usedCapacity=\"892547891\" remappedBlocks=\"18446744073709551615\" storage=\"1\" name=\"0_0_9\"/>\n"+
			"        <ClariionDiskConfig bus=\"0\" enclosureNumber=\"0\" diskNumber=\"10\" state=\"enabled\" vendorId=\"HITACHI\" productId=\"HUC10906 CLAR600\" revision=\"C430\" serialNumber=\"KSJUKKDR\" capacity=\"1125767168\" usedCapacity=\"892547891\" remappedBlocks=\"18446744073709551615\" storage=\"1\" name=\"0_0_10\"/>\n"+
			"        <ClariionDiskConfig bus=\"0\" enclosureNumber=\"0\" diskNumber=\"11\" state=\"unbound\" vendorId=\"HITACHI\" productId=\"HUC10906 CLAR600\" revision=\"C430\" serialNumber=\"KSJGNVZJ\" capacity=\"1125767168\" usedCapacity=\"0\" remappedBlocks=\"18446744073709551615\" storage=\"1\" name=\"0_0_11\"/>\n"+
			"    </ResponseEx>\n"+
			"</ResponsePacket>";

	public static final String GET_ALL_RAID_GROUP_RESPONSE =
			"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
			"<ResponsePacket xmlns=\"http://www.emc.com/schemas/celerra/xml_api\" apiVersion=\"V1_1\">\n" +
			"    <ResponseEx>\n" +
			"        <ClariionRaidGroupConfig raidType=\"raid-5\" state=\"valid-luns\" rawCapacity=\"4502487040\" logicalCapacity=\"4502478848\" usedCapacity=\"4462768128\" disks=\"0_0_6 0_0_7 0_0_8 0_0_9 0_0_10\" devices=\"0 1 3 20 21\" storage=\"1\" id=\"0000\"/>\n" +
			"        <ClariionRaidGroupConfig raidType=\"raid-1\" state=\"valid-luns\" rawCapacity=\"1125687296\" logicalCapacity=\"1125681152\" usedCapacity=\"1125681152\" disks=\"0_0_4 0_0_5\" devices=\"2\" storage=\"1\" id=\"0001\"/>\n" +
			"    </ResponseEx>\n" +
			"</ResponsePacket>\n";

	public static final String GET_ALL_STORAGE_PROCESSOR_RESPONSE =
			"<ResponsePacket xmlns=\"http://www.emc.com/schemas/celerra/xml_api\" apiVersion=\"V1_1\">\n" +
					"    <ResponseEx>\n" +
					"        <ClariionSPConfig  signature=\"3686624\" microcodeRev=\"05.33.000.5.051\" serialNumber=\"CF2BN140400224\" promRev=\"7.20.00\" agentRev=\"7.33.2 (0.51)\" physicalMemorySize=\"16384\" systemBufferSize=\"-1\" readCacheSize=\"-1\" writeCacheSize=\"-1\" freeMemorySize=\"-1\" raid3MemorySize=\"-1\" storage=\"1\" id=\"A\">\n" +
					"            <Port portNumber=\"0\" portUid=\"50060160886025fc50060160086025fc\" switchUid=\"00000000000000000000000000000000\"/>\n" +
					"            <Port portNumber=\"1\" portUid=\"50060160886025fc50060161086025fc\" switchUid=\"00000000000000000000000000000000\"/>\n" +
					"            <Port portNumber=\"2\" portUid=\"50060160886025fc50060162086025fc\" switchUid=\"00000000000000000000000000000000\"/>\n" +
					"            <Port portNumber=\"3\" portUid=\"50060160886025fc50060163086025fc\" switchUid=\"00000000000000000000000000000000\"/>\n" +
					"            <Port portNumber=\"8\" portUid=\"50060160886025fc50060160086425fc\" switchUid=\"00000000000000000000000000000000\"/>\n" +
					"            <Port portNumber=\"9\" portUid=\"50060160886025fc50060161086425fc\" switchUid=\"00000000000000000000000000000000\"/>\n" +
					"            <Port portNumber=\"10\" portUid=\"50060160886025fc50060162086425fc\" switchUid=\"00000000000000000000000000000000\"/>\n" +
					"            <Port portNumber=\"11\" portUid=\"50060160886025fc50060163086425fc\" switchUid=\"00000000000000000000000000000000\"/>\n" +
					"            <Port portNumber=\"4\" portUid=\"00000000000000000000000000000000\" switchUid=\"00000000000000000000000000000000\"/>\n" +
					"            <Port portNumber=\"5\" portUid=\"00000000000000000000000000000000\" switchUid=\"00000000000000000000000000000000\"/>\n" +
					"            <Port portNumber=\"6\" portUid=\"00000000000000000000000000000000\" switchUid=\"00000000000000000000000000000000\"/>\n" +
					"            <Port portNumber=\"7\" portUid=\"00000000000000000000000000000000\" switchUid=\"00000000000000000000000000000000\"/>\n" +
					"        </ClariionSPConfig>\n" +
					"        <ClariionSpStatus readCacheState=\"enabled\" writeCacheState=\"enabled\" state=\"operational\" storage=\"1\" id=\"A\">\n" +
					"            <Port portNumber=\"0\" linkStatus=\"up\" portStatus=\"on-line\"/>\n" +
					"            <Port portNumber=\"1\" linkStatus=\"down\" portStatus=\"disabled\"/>\n" +
					"            <Port portNumber=\"2\" linkStatus=\"down\" portStatus=\"disabled\"/>\n" +
					"            <Port portNumber=\"3\" linkStatus=\"down\" portStatus=\"disabled\"/>\n" +
					"            <Port portNumber=\"8\" linkStatus=\"up\" portStatus=\"on-line\"/>\n" +
					"            <Port portNumber=\"9\" linkStatus=\"up\" portStatus=\"on-line\"/>\n" +
					"            <Port portNumber=\"10\" linkStatus=\"down\" portStatus=\"disabled\"/>\n" +
					"            <Port portNumber=\"11\" linkStatus=\"down\" portStatus=\"disabled\"/>\n" +
					"            <Port portNumber=\"4\" linkStatus=\"down\" portStatus=\"on-line\"/>\n" +
					"            <Port portNumber=\"5\" linkStatus=\"down\" portStatus=\"on-line\"/>\n" +
					"            <Port portNumber=\"6\" linkStatus=\"down\" portStatus=\"on-line\"/>\n" +
					"            <Port portNumber=\"7\" linkStatus=\"down\" portStatus=\"on-line\"/>\n" +
					"        </ClariionSpStatus>\n" +
					"        <ClariionSPConfig signature=\"3686616\" microcodeRev=\"05.33.000.5.051\" serialNumber=\"CF2BN140400216\" promRev=\"7.20.00\" agentRev=\"7.33.2 (0.51)\" physicalMemorySize=\"16384\" systemBufferSize=\"-1\" readCacheSize=\"-1\" writeCacheSize=\"-1\" freeMemorySize=\"-1\" raid3MemorySize=\"-1\" storage=\"1\" id=\"B\">\n" +
					"            <Port portNumber=\"4\" portUid=\"00000000000000000000000000000000\" switchUid=\"00000000000000000000000000000000\"/>\n" +
					"            <Port portNumber=\"5\" portUid=\"00000000000000000000000000000000\" switchUid=\"00000000000000000000000000000000\"/>\n" +
					"            <Port portNumber=\"6\" portUid=\"00000000000000000000000000000000\" switchUid=\"00000000000000000000000000000000\"/>\n" +
					"            <Port portNumber=\"7\" portUid=\"00000000000000000000000000000000\" switchUid=\"00000000000000000000000000000000\"/>\n" +
					"            <Port portNumber=\"0\" portUid=\"50060160886025fc50060168086025fc\" switchUid=\"00000000000000000000000000000000\"/>\n" +
					"            <Port portNumber=\"1\" portUid=\"50060160886025fc50060169086025fc\" switchUid=\"00000000000000000000000000000000\"/>\n" +
					"            <Port portNumber=\"2\" portUid=\"50060160886025fc5006016a086025fc\" switchUid=\"00000000000000000000000000000000\"/>\n" +
					"            <Port portNumber=\"3\" portUid=\"50060160886025fc5006016b086025fc\" switchUid=\"00000000000000000000000000000000\"/>\n" +
					"            <Port portNumber=\"8\" portUid=\"50060160886025fc50060168086425fc\" switchUid=\"00000000000000000000000000000000\"/>\n" +
					"            <Port portNumber=\"9\" portUid=\"50060160886025fc50060169086425fc\" switchUid=\"00000000000000000000000000000000\"/>\n" +
					"            <Port portNumber=\"10\" portUid=\"50060160886025fc5006016a086425fc\" switchUid=\"00000000000000000000000000000000\"/>\n" +
					"            <Port portNumber=\"11\" portUid=\"50060160886025fc5006016b086425fc\" switchUid=\"00000000000000000000000000000000\"/>\n" +
					"        </ClariionSPConfig>\n" +
					"        <ClariionSpStatus readCacheState=\"enabled\" writeCacheState=\"enabled\" state=\"operational\" storage=\"1\" id=\"B\">\n" +
					"            <Port portNumber=\"4\" linkStatus=\"down\" portStatus=\"on-line\"/>\n" +
					"            <Port portNumber=\"5\" linkStatus=\"down\" portStatus=\"on-line\"/>\n" +
					"            <Port portNumber=\"6\" linkStatus=\"down\" portStatus=\"on-line\"/>\n" +
					"            <Port portNumber=\"7\" linkStatus=\"down\" portStatus=\"on-line\"/>\n" +
					"            <Port portNumber=\"0\" linkStatus=\"up\" portStatus=\"on-line\"/>\n" +
					"            <Port portNumber=\"1\" linkStatus=\"down\" portStatus=\"disabled\"/>\n" +
					"            <Port portNumber=\"2\" linkStatus=\"down\" portStatus=\"disabled\"/>\n" +
					"            <Port portNumber=\"3\" linkStatus=\"down\" portStatus=\"disabled\"/>\n" +
					"            <Port portNumber=\"8\" linkStatus=\"up\" portStatus=\"on-line\"/>\n" +
					"            <Port portNumber=\"9\" linkStatus=\"up\" portStatus=\"on-line\"/>\n" +
					"            <Port portNumber=\"10\" linkStatus=\"down\" portStatus=\"disabled\"/>\n" +
					"            <Port portNumber=\"11\" linkStatus=\"down\" portStatus=\"disabled\"/>\n" +
					"        </ClariionSpStatus>\n" +
					"    </ResponseEx>\n" +
					"</ResponsePacket>\n";
}
