#! /bin/bash

start_time=`date +%s`
OPENREFINE_PROJECTS_LOCATION="$HOME/.local/share/openrefine/"
FILES_IN_LOCATION=`find $OPENREFINE_PROJECTS_LOCATION`

SKELETONS=`echo -e "$FILES_IN_LOCATION" | grep "skeleton.json"` 
echo "removing persisted models"
rm -f $SKELETONS
PROJECTS=`echo -e "$FILES_IN_LOCATION" | grep "project"`
echo "removing projects"
rm -rf $PROJECTS
PROJECTS_CORRUPTED=`echo -e "$FILES_IN_LOCATION" | grep "project.corrupted"`
echo "removing corrupted projects"
rm -rf $PROJECTS_CORRUPTED
echo "reset finished"
OREFINEPROCESS=`ps aux | tr -s ' ' | grep com.google.refine.Refine | grep java-8-oracle | cut -d" " -f2`
kill -9 $OREFINEPROCESS
finish_time=`date +%s`
echo "reset time:""$(($finish_time-$start_time))"
./refine

