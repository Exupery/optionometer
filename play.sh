#! /bin/sh
play="play $1"
cmd=""
while read line 
do
	cmd="$cmd$line "
done < .env
eval "$cmd$play"
