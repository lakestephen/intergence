create table VmWareCredential (
  key SERIAL PRIMARY KEY,
  username VARCHAR(64),
  password VARCHAR(64),
  hostNameOrIp VARCHAR(128)
);