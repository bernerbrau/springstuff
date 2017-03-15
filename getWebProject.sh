#!/usr/bin/env bash
echo $1 $2
if [ -d $2 ]; then
    cd $2 &&
    svn switch $1 &&
    (svn update || svn revert);
else
    if [ -f $2 ]; then rm $2; fi
    svn checkout $1 $2;
fi