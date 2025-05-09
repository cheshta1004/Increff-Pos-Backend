#!/bin/bash
export JAVA_HOME="/Library/Java/JavaVirtualMachines/jdk1.8.0_202.jdk/Contents/Home"
export PATH="$JAVA_HOME/bin:$PATH"
export M2_HOME="/tmp/apache-maven-3.9.9"
export PATH="$M2_HOME/bin:$PATH"
/tmp/apache-maven-3.9.9/bin/mvn clean install
