#!/bin/bash
#
# hello.sh

if [ -x /usr/bin/cowsay ]
then
  cowsay "Hello !"
else
  echo "Hello !"
fi