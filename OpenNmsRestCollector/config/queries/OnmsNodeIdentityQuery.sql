					SELECT node.nodeId AS nodeid,
						'node' AS type,
						node.nodeSysName AS "Name",
						node.nodeSysOid AS "OID",
						ipInterface.ipAddr AS "IP Address",
						node.nodeCreateTime AS "Create Time",
						node.nodeSysContact AS "Contact",
						node.nodeSysName AS "Name",
						node.nodeSysDescription AS "Description",
						node.nodeSysLocation AS "Location",
						node.nodeLabel AS "Label"
					FROM Node
					LEFT OUTER JOIN IpInterface ON ipInterface.nodeId = node.nodeId AND ipInterface.isSnmpPrimary = 'P'
					WHERE node.nodeType != 'D'