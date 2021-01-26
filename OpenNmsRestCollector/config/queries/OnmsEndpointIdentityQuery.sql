					SELECT s.id AS endpointid,
						'endpoint' AS type,
						s.nodeId AS nodeid,
						s.snmpipadentnetmask AS "Netmask",
						s.snmpphysaddr AS "Physical Address",
						s.snmpifindex AS "Interface Index",
						s.snmpifname AS "Interface Name",
						s.snmpifalias AS "Interface Alias",
						s.snmpifspeed AS "Interface Speed",
						(CASE s.snmpIfAdminStatus
							WHEN 1 THEN 'Up'
							WHEN 2 THEN 'Down'
							WHEN 3 THEN 'Testing'
              ELSE 'Unknown'
						END) AS "Admin Status",
						(CASE s.snmpIfOperStatus
							WHEN 1 THEN 'Up'
							WHEN 2 THEN 'Down'
							WHEN 3 THEN 'Testing'
              ELSE 'Unknown'
						END) AS "Operational Status"

					FROM SnmpInterface s
					JOIN Node n ON n.nodeId = s.nodeid
					WHERE n.nodeType != 'D'