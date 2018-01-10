#!/usr/bin/sh

# variables
idx=`cat gen.seed`
cnt=$1

# check variables

if [ "$idx" = ""  ]; then 
  idx=1
fi

if [ "$cnt" = "" ]; then 
  cnt=10
fi

# max
max=$(($idx+$cnt))

while [ $idx -lt $max ]
do
  sfx=CTR_1104_`date "+%Y%m%d`_`printf "%.5d" $idx`.SND 
  echo $sfx
  cp CTR_1104_20051010_00001.TST $sfx
  idx=$(($idx+1))
done

echo $idx > gen.seed

