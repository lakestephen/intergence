from locate_hgapi_version import find_hgapi_url
import rest_util
import config

import json

url = find_hgapi_url()
response = rest_util.get(url + config.LIST_TOPOLOGY)

# pretty-print json
print(json.dumps(response, indent=2, sort_keys=True))
