from locate_hgapi_version import find_hgapi_url
import rest_util
import config

import urllib.parse

topology_to_remove = urllib.parse.quote("example topology")

url = find_hgapi_url()
rest_util.delete(url + config.DELETE_TOPOLOGY + "?name=" + topology_to_remove)
