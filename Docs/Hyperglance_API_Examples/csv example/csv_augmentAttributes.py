import json

from locate_hgapi_version import find_hgapi_url
import rest_util
import config
import csv_handling

def get_attributes(augmented_attrs):#Adds information from .csv to augmented_attrs
    
    for row in config.container:
        
        inner_attrs = {"attributes" : []}
        attributes_dict = {}
        
        for i in range(1, len(row)):
            attributes_dict[config.title_row[i]] = row[i]   
            
        inner_attrs["attributes"].append(attributes_dict)   
        augmented_attrs["attributes"].append(inner_attrs)
    return augmented_attrs

def get_UID(inner_attrs, key, group):#gets UID information from server
    
    for datasource in response['network']:
        for topology in datasource['topologies']:
            
            for item in topology[group]:
                if key == item["key"]:
                    inner_attrs["UID"] = item["UID"] 
    return inner_attrs

def list_UIDs(UID_list):#adds UID information to UID_list
    
    key_list = []
    
    for row in config.container:
        key_list.append(row[0])
    
    for key in key_list:
        for row in config.container:
            inner_attrs = {"attributes" : []}            
                   
            get_UID(inner_attrs, key, "nodes")  
            get_UID(inner_attrs, key, "endpoints")
            get_UID(inner_attrs, key, "links")  
                                
        UID_list.append(inner_attrs)
        
    return UID_list

augmented_attrs = {
    "attributes" : []
}

UID_list = []

url = find_hgapi_url()

response = rest_util.get(url + config.GET_NETWORK)#retrieves topology information from server

csv_handling.prep_csv("attributes")#Reads .csv and stores information in container and title_row

augmented_attrs = get_attributes(augmented_attrs)
UID_list = list_UIDs(UID_list)

for i in range(len(UID_list)): # Merges attributes and UIDs
    UID_list[i].update(augmented_attrs["attributes"][i])

augmented_attrs["attributes"] = UID_list

rest_util.put(url + config.AUGMENT_ATTRIBUTES, augmented_attrs)#Sends info to server

print(json.dumps(augmented_attrs, indent=2, sort_keys=True))

