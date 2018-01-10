#!/usr/bin/sh

# env
. setEnv.sh

#shutdown
java -classpath ${CLASSPATH} ${JAVA_OPTIONS} kr.go.kofiu.ctr.cr.agent.Controller shutdown
