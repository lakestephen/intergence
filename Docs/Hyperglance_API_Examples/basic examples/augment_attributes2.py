import json

from locate_hgapi_version import find_hgapi_url
import rest_util
import config


url = find_hgapi_url()

response = rest_util.get(url + config.GET_NETWORK)

augmented_attrs = {
    "attributes": []
}

for datasource in response['network']:
    for topology in datasource['topologies']:
        for node in topology['nodes']:
            augmented_attrs['attributes'].append(
                {
                    "UID": node['UID'],
                    "attributes": [
                        { "name": "Contact E-Mail", "value": "jon@example.com" }
                    ]
                } )


# pretty-print json request
print(json.dumps(augmented_attrs, indent=2, sort_keys=True))

rest_util.put(url + config.AUGMENT_ATTRIBUTES, augmented_attrs)


