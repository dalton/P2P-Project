#!/usr/bin/env bash

mvn exec:java -f pom.xml  -Dexec.mainClass="edu.ufl.cise.cnt5106c.peerProcess" -Dexec.args="1001" -X
