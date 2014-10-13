#!/bin/bash

USER="ibekwec";
DEST_DIR="/home/ibekwec/";
JAR_FILE="broadcast.jar";
IP_DATA_FILE="data.txt";
PORT=8080;
read -sp "password: " password;

read -p "Copy ssh id to host?: (y/n)" wantCopySshId;
if [ $wantCopySshId = "y" ]; then
  ssh-copy-id -i ~/.ssh/id_rsa.pub $USER@${base};
elif [ ! $wantCopySshId = "n" ]; then
  $($0);
fi

# Get the number of hosts from the datafile
hostNbr=$(sed -n '1{p;q}' $IP_DATA_FILE);
# Construct an array of hosts
# with there extensions.
for (( i=0; i<$hostNbr; i+=1 )); do
  let line=i+2;
  host=$(sed -n $line'{p;q}' $IP_DATA_FILE);
  exts[$i]=${host##*.};
  hosts[$i]=$(basename $host .${exts[$i]});
done



echo "${hosts[*]}";
exit;

done=0;
for filename in data/*; do
  ext=${filename##*.};
  base=$(basename $filename .$ext);

  sshpass -p$password scp -i ~/.ssh/id_rsa.pub $filename $USER@${base}:$DEST_DIR;
  sshpass -p$password scp -i ~/.ssh/id_rsa.pub $JAR_FILE $USER@${base}:$DEST_DIR;
  command="java -jar ${JAR_FILE} $PORT ${base}.${ext} $ext";

done

echo "deployed";
