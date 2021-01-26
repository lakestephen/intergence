from locate_hgapi_version import find_hgapi_url
import rest_util
import config

example_topology = {
    "name": "example topology",
    "nodes": [
        {
            "key": "node1",
            "type": "vm",
            "attributes": [
                { "name": "Name", "value":"new-vm-test" }
            ]
        },
        {
            "key": "node2",
            "type": "host",
            "attributes": [
                { "name": "Name", "value":"lab host1" }
            ]
        }
    ],
    "endpoints": [
        {
            "key": "ep1",
            "type": "virtual port",
            "nodeKey": "node1"
        },
        {
            "key": "ep2",
            "type": "physical port",
            "nodeKey": "node2"
        }
    ],
    "links": [
        {
            "key": "host-to-vm link",
            "type": "connection",
            "endpointAKey": "ep1",
            "endpointBKey": "ep2"
        }
    ]
}



url = find_hgapi_url()
response = rest_util.put(url + config.PUT_TOPOLOGY, example_topology)

for n in response['nodes']:
    print("Node '" + n['key'] + "' created with UID: " + n['UID'])

for e in response['endpoints']:
    print("Endpoint '" + e['key'] + "' created with UID: " + e['UID'])

for l in response['links']:
    print("Link '" + l['key'] + "' created with UID: " + l['UID'])


