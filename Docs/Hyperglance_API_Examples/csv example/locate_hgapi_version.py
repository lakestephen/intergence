import sys
import urllib.parse

import config
import rest_util


def find_hgapi_url():

    version_info = rest_util.get(config.HGAPI_ROOT)

    all_versions = version_info['versions']
    compatible_versions = [v for v in all_versions if v['id'] == config.HGAPI_VERSION]

    if not compatible_versions:
        print("HGS Server does not support necessary version: " + config.HGAPI_VERSION, file=sys.stderr)
        return None
        
    assert len(compatible_versions) == 1
    version = compatible_versions[0]

    status = version['status']
    path = version['path']
    if path.endswith('/'): path = path[:-1]
    
    url = path if path.startswith('http') else config.HGS_SERVER + path

    print("Found HGAPI " + config.HGAPI_VERSION + " at: " + url)
    print("  status: " + status)
    
    return url


if __name__ == "__main__":
    v1_url = find_hgapi_url()
    print("Got URL: " + v1_url)
