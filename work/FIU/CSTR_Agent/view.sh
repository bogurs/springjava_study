if [ "$1" = "CTR" ]; then
	tail -100f ./logs/ctr_agent.log
elif [ "$1" = "STR" ]; then
	tail -100f ./logs/str_agent.log
else
	echo "     ex> view.sh CTR (or STR)"
	echo "      �ùٸ� ��ɾ �Է��� �ּ���."
fi
