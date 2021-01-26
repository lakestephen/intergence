create table node (
	nodeID		integer not null,
	dpName		varchar(12),
	nodeCreateTime	timestamp,
	nodeParentID	integer,
	nodeType	char(1),
	nodeSysOID	varchar(256),
	nodeSysName	varchar(256),
	nodeSysDescription	varchar(256),
	nodeSysLocation	varchar(256),
	nodeSysContact	varchar(256),
	nodeLabel	varchar(256),
	nodeLabelSource	char(1),
	nodeNetBIOSName varchar(16),
	nodeDomainName  varchar(16),
	operatingSystem varchar(64),
	foreignSource	varchar(64),
	foreignId       varchar(64),

);


create table ipInterface (
  id              INTEGER NOT NULL,
  nodeID			integer not null,
  ipAddr			text not null,
  ifIndex			integer,
  ipHostname		varchar(256),
  isManaged		char(1),
  ipStatus		integer,
  ipLastCapsdPoll timestamp,
  isSnmpPrimary   char(1),
  snmpInterfaceId	integer,
);


create table snmpInterface (
    id				INTEGER NOT NULL,
	nodeID			integer not null,
	ipAddr			text ,
	snmpIpAdEntNetMask	varchar(45),
	snmpPhysAddr		varchar(32),
	snmpIfIndex		integer not null,
	snmpIfDescr		varchar(256),
	snmpIfType		integer,
	snmpIfName		varchar(96),
	snmpIfSpeed		bigint,
	snmpIfAdminStatus	integer,
	snmpIfOperStatus	integer,
	snmpIfAlias		varchar(256),
    snmpLastCapsdPoll timestamp ,
    snmpCollect     varchar(2) default 'N',
    snmpPoll     varchar(1) default 'N',
    snmpLastSnmpPoll timestamp ,
);


create table datalinkinterface (
    id               integer not null,
    nodeid           integer not null,
    ifindex          integer not null,
    nodeparentid     integer not null,
    parentIfIndex    integer not null,
    status           char(1) not null,
    protocol         varchar(31),
    linkTypeId       integer,
    lastPollTime     timestamp ,
    source           varchar(64) not null default 'linkd',

);