#!/bin/bash




export ANT_HOME=$(pwd)/tools/apache-ant-1.8.2
export PATH=$ANT_HOME/bin:$PATH
chmod +x $ANT_HOME/bin/ant
tools/apache-ant-1.8.2/bin/ant  $*
