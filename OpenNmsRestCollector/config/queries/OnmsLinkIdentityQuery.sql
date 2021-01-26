	        SELECT
					  datalinkinterface.id AS linkid,
						'link' AS type,
						s1.id AS endaid,
						s2.id AS endbid
					FROM DataLinkInterface
					JOIN SnmpInterface s1 ON s1.nodeid = datalinkinterface.nodeid AND s1.snmpifindex = datalinkinterface.ifindex
					JOIN SnmpInterface s2 ON s2.nodeId = datalinkinterface.nodeParentid AND s2.snmpifindex = datalinkinterface.parentifindex
					JOIN Node n1 ON n1.nodeId = datalinkinterface.nodeid
					JOIN Node n2 ON n2.nodeId = datalinkinterface.nodeparentid
					WHERE n1.nodeType != 'D' AND n2.nodeType != 'D' AND datalinkinterface.status != 'D'
-- AND datalinkinterface.ifindex > -1 AND datalinkinterface.parentifindex > -1

-- Consider uncommenting the line above to ignore links involving "unknown" SNMP interface indices
-- This has pros and cons - sometimes it can result in a clearer network graph, sometimes it can result in
-- unhooking a lot of devices. Recent experimentation seems to show that our current default settings, along with
-- the CoLocatedLinkRefinementCollector yield the best overall results  -->