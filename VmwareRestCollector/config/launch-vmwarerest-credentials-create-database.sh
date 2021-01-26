#!/bin/sh

# Set the script directory.
DIR=`dirname "$BASH_SOURCE[0]"`

# Set the path to your Java 1.6
# JAVA_HOME=/usr/java/jdk1.6.0_33;
JAVA_HOME=/usr;

${JAVA_HOME}/bin/java -Xmx128m -cp "${DIR}:${DIR}/../lib/*:${DIR}/lib/*:${DIR}/ddl/*" -Dvmware.credentials.create=true com.intergence.hgsrest.SpringLauncher classpath*:spring/appContext-vmwarerest-rebuildCredentialsDatabase.xml