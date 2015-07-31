#!/bin/bash

#set -x

onexit() {
  echo "exit code $? lastfile: $i"
  echo "processed $c files"
}
trap onexit EXIT 

let 'c=0'  #returns -1
set -e

for i in `find . -iname '*.jpg'`; do
  exiv2 -da rm "$i"
#  echo "$i"
  set +e
  let 'c++'  #returns -1
  set -e
done



