#!/usr/bin/sh

# env
. setEnv.sh

# restart
java -classpath ${CLASSPATH} ${JAVA_OPTIONS} kr.go.kofiu.ctr.cr.agent.Controller restart
