#!/bin/sh
java -cp ./badipfetch.jar:./libs/httpclient-cache-4.3.6.jar:./libs/httpclient-4.3.6.jar:./libs/httpcore-4.3.3.jar:./libs/jedis-2.1.0.jar:./libs/commons-logging-1.1.3.jar org.metams.badipfetch.OneShot $1 $2 $3

