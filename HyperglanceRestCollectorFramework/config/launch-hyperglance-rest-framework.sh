#!/bin/sh

# Set the script directory
DIR=`dirname "$BASH_SOURCE[0]"`

# Set the path to your Java 1.6
# JAVA_HOME=/usr/java/jdk1.6.0_33;
JAVA_HOME=/usr;

${JAVA_HOME}/bin/java -Xmx1024m -cp "${DIR}:${DIR}/lib/*:${DIR}/OpenNmsRestCollector:${DIR}/OpenNmsRestCollector/lib/*:${DIR}/VmwareRestCollector:${DIR}/VmwareRestCollector/lib/*:${DIR}/EMCRestCollector:${DIR}/EMCRestCollector/lib/*:${DIR}/RefinementRestCollector:${DIR}/RefinementRestCollector/lib/*:${DIR}/DirectInjectRestCollector:${DIR}/DirectInjectRestCollector/lib/*" com.intergence.hgsrest.SpringLauncher classpath*:spring/appContext-hyperglance-rest-framework.xml collectorRunner