#!/bin/bash
#
# hello.sh


if [ -x /usr/bin/cowsay ]
then
  cowsay "Hello !"
elif [ -x /usr/bin/figlet ]
then
  figlet "Hello !"
else
  echo "Hello !"
fi