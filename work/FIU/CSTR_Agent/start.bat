echo off

:home

call setEnv


:run
if "%1" == "CTR" (
echo java -DFIUELVCTR -Xms64m -Xmx256m -classpath %CLASSPATH% %JAVA_OPTIONS% kr.go.kofiu.common.agent.Agent %1 %2 
java -DFIUELVCTR -Xms64m -Xmx256m -classpath %CLASSPATH% %JAVA_OPTIONS% kr.go.kofiu.common.agent.Agent %1 %2
)
if "%1" == "STR" (
echo java -DFIUELVSTR -Xms256m -Xmx2048m -classpath %CLASSPATH% %JAVA_OPTIONS% kr.go.kofiu.common.agent.Agent %1 %2 
java -DFIUELVSTR -Xms256m -Xmx2048m -classpath %CLASSPATH% %JAVA_OPTIONS% kr.go.kofiu.common.agent.Agent %1 %2
)




:check

if errorlevel 100 goto run


:exit
