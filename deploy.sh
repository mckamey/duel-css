#!/bin/sh

clear;clear

cd css
mvn clean deploy -DperformRelease=true -Dgpg.keyname=EE82F9AB
