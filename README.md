rundeck-url-nodes-plugin
========================

A generic resource model source provider with caching capability

To Build:
=====

plugin builds against jars within the ${RUNDECK_SERVER_DIR}/exp/webapp/WEB-INF/lib directory
which assumes one of the following:

/var/lib/rundeck exists

    ant jar

-or-

define environment:  RUNDECK_SERVER_DIR 

    export RUNDECK_SERVER_DIR=/path/to/rundeck/server/dir
    ant jar


Creates the  dist/rundeck-url-nodes-plugin.jar

To Install the Rundeck Plugin 
=====

copy dist/rundeck-url-nodes-plugin.jar to the Rundeck Server Extension Directory (such as /var/lib/rundeck/libext)

and restart Rundeck.

