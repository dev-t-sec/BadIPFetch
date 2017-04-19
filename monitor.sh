#!/bin/sh
java -cp ./badipfetch.jar:./libs/httpclient-cache-4.5.3.jar:./libs/httpclient-4.5.3.jar:./libs/httpcore-4.4.6.jar:./libs/jedis-2.9.0.jar:./libs/commons-logging-1.2.jar org.metams.badipfetch.Monitor $1 $2 $3 $4

