#!/bin/bash
#
# hello.sh

if [ -x /usr/bin/figlet ]
then
  figlet "Hello !"
else
  echo "Hello !"
fi