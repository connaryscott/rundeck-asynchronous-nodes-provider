rundeck-asynchronous-nodes-provider
========================

A generic resource model source provider with caching capability
NOTE:  this current implementation only supports xml backend (yaml not yet supported).

To Build:
=====

./build.sh jar

Creates the  dist/asynchronous-url-nodes-provider-<VERSION>.jar 

To Install the Rundeck Plugin 
=====

copy dist/asynchronous-url-nodes-provider-<VERSION>.jar to the Rundeck Server Extension Directory (such as /var/lib/rundeck/libext)

and restart Rundeck.

