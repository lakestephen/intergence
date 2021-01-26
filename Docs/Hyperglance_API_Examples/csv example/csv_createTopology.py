from locate_hgapi_version import find_hgapi_url
import csv_handling
import config
import rest_util

import json

def build_attributes(row, n):#adds attribute information from topology.csv
    
    global topology_map
    global node_attributes
    
    for i in range(5, len(row)):
        node_attributes[config.title_row[i]] = row[i]
    topology_map[row[0]][n]["attributes"].append(node_attributes)
    n += 1
    return n

def build_map():#Adds the information stored in topology.csv to topology_map
    
    n = 0
    e = 0
    l = 0
    
    global topology_map
    
    for row in config.container:
            
        if row[0] == "nodes":
            topology_map[row[0]].append({"key": row[1], "type": row[2], "attributes": []})
            n = build_attributes(row,n)   
                            
        if row[0] == "endpoints":
            topology_map[row[0]].append({"key": row[1], "type": row[2], "nodeKey": row[3], "attributes": []})
            e = build_attributes(row,e)    
                                  
        if row[0] == "links":
            topology_map[row[0]].append({"key": row[1], "type": row[2], "endpointAKey": row[3], "endpointBKey": row[4], "attributes": []})
            l = build_attributes(row,l)   
                 
topology_map = {
                "name" :"topology_map",
                "nodes":[],
                "links":[],
                "endpoints":[]
                }

node_attributes = {}

csv_handling.prep_csv("topology")#Reads .csv and stores information in container and title_row

build_map()

url = find_hgapi_url()
response = rest_util.put(url + config.PUT_TOPOLOGY, topology_map)#Sends info to server

print(json.dumps(topology_map, indent=2, sort_keys=True))
