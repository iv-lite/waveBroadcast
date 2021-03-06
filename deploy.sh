#!/bin/bash

USER="ibekwec";
DEST_DIR="/home/${USER}/";
DATA_DIR="data";
JAR_FILE="broadcast.jar";
IP_DATA_FILE="data.txt";
PORT=8000;
read -sp "password: " password;
echo "";

# Get the number of hosts from the datafile
hostNbr=$(sed -n '1{p;q}' $IP_DATA_FILE);
hostWaitNbr=0;
hostInitNbr=0;
# Construct an array of hosts
# with there extensions.
for (( i=0; i<$hostNbr; i+=1 )); do
    let line=i+2;
    host=$(sed -n $line'{p;q}' $IP_DATA_FILE);
    exts[$i]=${host##*.};
    hosts[$i]=$(basename $host .${exts[$i]});

    if [ ${exts[$i]} = "WAIT" ]; then
        hostWait[$hostWaitNbr]=$i;
        let hostWaitNbr=hostWaitNbr+1;
    else
        hostInit[$hostInitNbr]=$i;
        let hostInitNbr=hostInitNbr+1;
    fi
done

echo "Copying processed files from data dir onto provided hosts.";
updateFile="o";
while [ ! $updateFile = "y" ] && [ ! $updateFile = "n" ]; do
    read -sp "Update files on remote hosts ? (y/n) " updateFile;
done
# Construction of each node neigbors files
# for simplicity each node is supposed to be connected
# with every other one.
let neighborNbr=hostNbr-1;
for (( i=0; i<$hostNbr; i+=1 )); do
    neighborFile=$DATA_DIR"/neighbors_${hosts[$i]}";
    echo $neighborNbr > $neighborFile;
    for (( j=0; j<$hostNbr; j+=1 )); do
        if [ $j -ne $i ]; then
            echo "${hosts[$j]}" >> $neighborFile;
        fi
    done

    # Once the file is processed
    # we can copy it onto the destination server.
    if [ $updateFile = "y" ]; then
        sshpass -p$password scp $neighborFile $JAR_FILE $USER@${hosts[$i]}:$DEST_DIR;
    fi

    echo ".";
done

echo "Launching background waiting nodes";
# launch background process in waiting nodes.
for index in ${hostWait[*]}; do
    remoteNeighborFile="neighbors_${hosts[$index]}";
    command="java -jar $JAR_FILE $PORT $remoteNeighborFile WAIT";
    sshpass -p$password ssh -f -v $USER@${hosts[$index]} "$command";

    echo ".";
done

echo "Launching background init nodes.";
# launch background process in init nodes.
let hostInitNbr=hostInitNbr-1;
for (( i=0; i<$hostInitNbr; i+=1 )); do
    index=${hostInit[$i]};
    remoteNeighborFile="neighbors_${hosts[$index]}";
    command="java -jar $JAR_FILE $PORT $remoteNeighborFile INIT";
    sshpass -p$password ssh -v -f $USER@${hosts[$index]} "$command";

    echo ".";
done

#launch ssh on last init node as a foreground process
index=${hostInit[$hostInitNbr]};
remoteNeighborFile="neighbors_${hosts[$index]}";
command="java -jar $JAR_FILE $PORT $remoteNeighborFile INIT";
sshpass -p$password ssh $USER@${hosts[$index]} "$command;";
