REM Set the path to your Java 1.6
set JAVA_HOME=C:/Program Files/Java/jdk1.6.0_45

"%JAVA_HOME%/bin/java" -Xmx1024m -cp ".;lib/*;OpenNmsRestCollector;OpenNmsRestCollector/lib/*;VmwareRestCollector;VmwareRestCollector/lib/*;EMCRestCollector;EMCRestCollector/lib/*;RefinementRestCollector;RefinementRestCollector/lib/*;DirectInjectRestCollector;DirectInjectRestCollector/lib/*" com.intergence.hgsrest.SpringLauncher classpath*:spring/appContext-hyperglance-rest-framework.xml collectorRunner