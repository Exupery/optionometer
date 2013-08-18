#! /bin/sh
play="play $1"
cmd=""
while read line 
do
    first=`expr index $line '#'` 
    if [ $first -ne 1 ]; then
    	cmd="$cmd$line "
    fi
done < .env
eval "$cmd$play"
