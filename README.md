rundeck-asynchronous-nodes-provider
========================

A generic resource model source provider with caching capability

*NOTE*:  this current implementation only supports xml backend (yaml not yet supported).

To Build:
=====

./build.sh jar

Creates the  dist/asynchronous-url-nodes-provider-<VERSION>.jar 

To Download:
=====

Download a release from [build.rundeck.org](http://build.rundeck.org/view/plugins/job/rundeck-asynchronous-nodes-provider-master/lastSuccessfulBuild/artifact/dist/)

To Install the Rundeck Plugin:
=====


copy dist/asynchronous-url-nodes-provider-<VERSION>.jar to the Rundeck Server Extension Directory (such as /var/lib/rundeck/libext)

and restart Rundeck.

To Configure the Rundeck Plugin:
=====

[Add the Resources Provider](https://github.com/connaryscott/rundeck-asynchronous-nodes-provider/blob/master/doc/addAsynchronousResourcesProvider.jpg)

[Configure the Resources Provider](https://github.com/connaryscott/rundeck-asynchronous-nodes-provider/blob/master/doc/asynchronousResourcesProvider.jpg)


Add logging support for Rundeck Plugins:
=====

Add DEBUG support to the log4j.properties file (e.g. /var/lib/rundeck/exp/webapp/WEB-INF/classes/log4j.properties)

log4j.logger.com.dtolabs.rundeck.plugin=DEBUG, server-logger
