set JAVA_OPTIONS=-Dorg.apache.commons.logging.Log=org.apache.commons.logging.impl.NoOpLog

set path=.\pkilib;%path%

set CLASSPATH=./lib/agent.jar
set CLASSPATH=%CLASSPATH%;./lib/activation.jar
set CLASSPATH=%CLASSPATH%;./lib/backport-util-concurrent.jar
set CLASSPATH=%CLASSPATH%;./lib/quartz.jar
set CLASSPATH=%CLASSPATH%;./lib/log4j-1.2.11.jar
set CLASSPATH=%CLASSPATH%;./lib/commons-collections.jar
set CLASSPATH=%CLASSPATH%;./lib/commons-digester-1.7.jar
set CLASSPATH=%CLASSPATH%;./lib/commons-beanutils.jar
set CLASSPATH=%CLASSPATH%;./lib/commons-logging.jar
set CLASSPATH=%CLASSPATH%;./lib/mail.jar
set CLASSPATH=%CLASSPATH%;./lib/webserviceclient.jar
set CLASSPATH=%CLASSPATH%;./lib/schemas.jar
set CLASSPATH=%CLASSPATH%;./lib/schemas.v.0.89.jar
set CLASSPATH=%CLASSPATH%;./lib/CSTRWebService.jar
set CLASSPATH=%CLASSPATH%;./lib/libgpkiapi_jni.jar
set CLASSPATH=%CLASSPATH%;./lib/jsr173_api.jar
set CLASSPATH=%CLASSPATH%;./lib/cgejb_client.jar
set CLASSPATH=%CLASSPATH%;./lib/com_gpki_sdk.jar
set CLASSPATH=%CLASSPATH%;./lib/xbean-2.2.0.jar
set CLASSPATH=%CLASSPATH%;./lib/ctrutil.jar
set CLASSPATH=%CLASSPATH%;./lib/commons-codec-1.6.jar
set CLASSPATH=%CLASSPATH%;./lib/StrSchemas.jar
set CLASSPATH=%CLASSPATH%;./lib/jxl.jar

set CLASSPATH=%CLASSPATH%;./lib/wmqlib/com.ibm.mq.pcf.jar
set CLASSPATH=%CLASSPATH%;./lib/wmqlib/com.ibm.mqjms.jar
set CLASSPATH=%CLASSPATH%;./lib/wmqlib/mqcontext.jar

set CLASSPATH=%CLASSPATH%;./lib/javax.jms-1.1.jar
set CLASSPATH=%CLASSPATH%;./lib/ldapjdk.jar
set CLASSPATH=%CLASSPATH%;./lib/sggpki.jar
set CLASSPATH=%CLASSPATH%;./lib/sgkm.jar
set CLASSPATH=%CLASSPATH%;./lib/sgsecukit.jar
set CLASSPATH=%CLASSPATH%;./lib/signgate_common.jar
set CLASSPATH=%CLASSPATH%;./lib/signgateCrypto.jar
