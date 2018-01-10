echo off

call setEnv

echo java -classpath %CLASSPATH% %JAVA_OPTIONS% kr.go.kofiu.common.agent.Controller shutdown %1
java -classpath %CLASSPATH% %JAVA_OPTIONS% kr.go.kofiu.common.agent.Controller shutdown %1
