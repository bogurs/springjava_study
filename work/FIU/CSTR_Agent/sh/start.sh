
startAgent() {
	#echo `ps -ef | grep 'java -DFIUCSTR' | wc -l`
	PS_LINE_COUNT="`ps -ef | grep 'java -DFIUCSTR' | wc -l`"

	if [ "${PS_LINE_COUNT}" != "2" ] ; then
		if [ "$1" = "CTR" ]; then
			echo "FIUCTR Agent Starting ......."
			if [ "$2" != "" ]; then				
				nohup sh startup.sh $1 $2 > ./logs/ctr_agent.log &
			else
				nohup sh startup.sh $1 > ./logs/ctr_agent.log &
			
			fi
		elif [ "$1" = "STR" ]; then
			echo "FIUSTR Agent Starting ......"
			if [ "$2" != "" ]; then				
				nohup sh startup.sh $1 $2 > ./logs/str_agent.log &
			else
				nohup sh startup.sh $1 > ./logs/str_agent.log &			
			fi
		else
			printMessage		
		fi
	else
        	echo "<FAIL> FIUCSTR Agent is Already Running...."
        	echo "<FAIL> Stop Agent Process first...."
	fi
}

printMessage(){

	echo "***** INVALID option - use option below"
	echo "      ex>start.sh CTR none(or version or force)"
	echo "      ex>start.sh STR none(or version or force)"
	echo "     1st parameter"
	echo "      CTR : run CTR"
	echo "      STR : run STR"
	echo "     2nd parameter"
	echo "      none : JUST input 1st parameter!!"
	echo " 		version : show CTR/STR Version Info"
	echo " 		force : start CTR/STR MOUDLE FORCE"

}

if [ "$1" != "" ]; then
	if [ "$1" = "CTR" ]; then
		if [ "$2" != "" ]; then
		case $2 in
			version)
				startup.sh $1 $2
				;;
			force)
				startAgent $1 $2
				;;
			*)
				printMessage	
				;;
		esac
		else
			startAgent $1
		fi
	elif [ "$1" = "STR" ]; then
		if [ "$2" != "" ]; then
		case $2 in
			version)
				startup.sh $1 $2
				;;
			force)
				startAgent $1 $2
				;;
			*)
				printMessage
				;;
		esac
		else
			startAgent $1
		fi
	else
		printMessage	
	fi
else
	printMessage	
fi
