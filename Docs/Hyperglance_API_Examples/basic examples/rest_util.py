import config

import urllib.request
import base64
import json

def _basic_auth_token(user, passwd):
    userpasswd_bytes = (user + ':' + passwd).encode('utf-8')
    return base64.b64encode(userpasswd_bytes).decode('ascii')

_AUTH_HEADER = "Basic " + _basic_auth_token(user=config.DATASOURCE,
                                            passwd=config.API_KEY)

if __name__ == "__main__":
    print("Generated 'Authorization' Header: " + _AUTH_HEADER)


def get(path):
    print('Issuing GET: ' + path)

    resp = urllib.request.urlopen(
        urllib.request.Request(
            method='GET',
            url=path,
            headers={'Authorization': _AUTH_HEADER}))
    
    json_out = resp.read().decode('utf-8')
    return json.loads(json_out) if json_out else None


def put(path, data):
    print('Issuing PUT: ' + path)

    json_in = json.dumps(data).encode('utf-8')
 
    resp = urllib.request.urlopen(
        urllib.request.Request(
            method='PUT',
            url=path,
            data=json_in,
            headers={'Content-Type': 'application/json',
                     'Authorization': _AUTH_HEADER}))
    
    json_out = resp.read().decode('utf-8')
    return json.loads(json_out) if json_out else None


def delete(path):
    print('Issuing DELETE: ' + path)
    
    urllib.request.urlopen(
        urllib.request.Request(
            method='DELETE',
            url=path,
            headers={'Authorization': _AUTH_HEADER}))
    
