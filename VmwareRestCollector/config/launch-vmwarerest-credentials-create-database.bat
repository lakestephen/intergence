REM Set the path to your Java 1.6
set JAVA_HOME=C:/Program Files/Java/jdk1.6.0_45

"%JAVA_HOME%/bin/java" -Xmx128m -cp ".;../lib/*;lib/*;ddl/*" -Dvmware.credentials.create=true com.intergence.hgsrest.SpringLauncher classpath*:spring/appContext-vmwarerest-rebuildCredentialsDatabase.xml