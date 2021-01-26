from locate_hgapi_version import find_hgapi_url
import rest_util
import config


node_UID = "AcmeApp/example topology/node/vm/node1"

augmented_attrs = {
    "attributes": [
        {
            "UID": node_UID,
            "attributes": [
                { "name": "Contact E-Mail", "value": "jon@example.com" }
            ]
        }
    ]
}


url = find_hgapi_url()
rest_util.put(url + config.AUGMENT_ATTRIBUTES, augmented_attrs)


