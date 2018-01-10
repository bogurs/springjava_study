#!/usr/bin/sh

# env
. ./setEnv.sh

while  true
do

# run
if [ "$1" = "CTR" ]; then
	if [ "$2" != "" ]; then
		echo java -DFIUELVCTR -Xms64m -Xmx256m -classpath ${CLASSPATH} ${JAVA_OPTIONS} kr.go.kofiu.common.agent.Agent $1 $2
		java -DFIUELVCTR -Xms64m -Xmx256m -classpath ${CLASSPATH} ${JAVA_OPTIONS} kr.go.kofiu.common.agent.Agent $1 $2
	else
		echo java -DFIUELVCTR -Xms64m -Xmx256m -classpath ${CLASSPATH} ${JAVA_OPTIONS} kr.go.kofiu.common.agent.Agent $1
		java -DFIUELVCTR -Xms64m -Xmx256m -classpath ${CLASSPATH} ${JAVA_OPTIONS} kr.go.kofiu.common.agent.Agent $1
	fi
elif [ "$1" = "STR" ]; then
	if [ "$2" != "" ]; then
		echo java -DFIUELVSTR -Xms256m -Xmx2048m -classpath ${CLASSPATH} ${JAVA_OPTIONS} kr.go.kofiu.common.agent.Agent $1 $2
		java -DFIUELVSTR -Xms256m -Xmx2048m -classpath ${CLASSPATH} ${JAVA_OPTIONS} kr.go.kofiu.common.agent.Agent $1 $2
	else
		echo java -DFIUELVSTR -Xms256m -Xmx2048m -classpath ${CLASSPATH} ${JAVA_OPTIONS} kr.go.kofiu.common.agent.Agent $1
		java -DFIUELVSTR -Xms256m -Xmx2048m -classpath ${CLASSPATH} ${JAVA_OPTIONS} kr.go.kofiu.common.agent.Agent $1
	fi
fi

# check

if [ "$?" != "100" ]; 
then 
  break; 
fi

done
