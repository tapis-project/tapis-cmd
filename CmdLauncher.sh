#!/bin/bash

#ARG 1: Name of jar file without extension containg classes to be called
#ARG 2: Name of class to be called from jar file
#ARG 3: MUST BE IN QUOTES "<Arg 3>" Contains the rest of the command where parameters are passed in.
#       For example if passing a jwt and system name it would look like: "-jwt <jwt name> -sys <system name>"

jar=$1
class=$2

echo ""
echo "|===============================================================|"
echo "Running class: $2 from jar: $1.jar with args: $3" 
echo "|===============================================================|"
echo ""

java -cp $1.jar edu.utexas.tacc.tapis.cmd.driver.$2 $3
