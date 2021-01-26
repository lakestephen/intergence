insert into node (nodeId, nodeSysName, nodeSysOid, nodeType) values (1, 'Disk1', '1234', 'A')
insert into node (nodeId, nodeSysName, nodeSysOid, nodeType) values (2, 'Disk2', '5678', 'A')
insert into node (nodeId, nodeSysName, nodeSysOid, nodeType) values (2, 'Disk2', '5678', 'A')

insert into ipInterface (id, nodeId, ipAddr, isSnmpPrimary) values (101, 1, '123.456.789.1', 'P')
insert into ipInterface (id, nodeId, ipAddr, isSnmpPrimary) values (102, 2, '123.456.789.2', 'P')

insert into SnmpInterface (id, nodeId,SNMPIFINDEX, snmpIpAdEntNetMask) values (201, 1, 0, '4.3.2.1')
insert into SnmpInterface (id, nodeId,SNMPIFINDEX, snmpIpAdEntNetMask) values (202, 2, 0, '1.2.3.4')

insert into datalinkinterface (id, nodeId, nodeparentid, ifindex, parentifindex, status) values (303, 1, 2, 0, 0, 'A')
