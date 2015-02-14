"Fetch bad IPs"

Version 0.91, 02-10-2015

Idea
----

DTAG runs a number of honeypots to see attack traffic and collect threat information. One interesting
information is the current set of "bad" or "misused" IP adresses.

The backend logic remembers only IP adresses, which have been involved in an attack the last thirty
minutes.

This little tool provides you with the necessary interface code for it.

In general we differentiate between two categories:

1. "Validated" Honeypots
2. Community honeypots

A user cannot can only be linked to one of the both categories.



Dependencies:
-------------

Java 1.6 or higher (tested with both, latest 1.6, 1.7 and 1.8 versions on Mac OS X)
Redis (simple, lightweight NoSQL DB, see http://redis.io, tested with version 2.6.7)
Jedis (Java client for Redis, included in this archiv)
HttpClient (package from Apache foundation, included in this package)


Usage:
------

Compile the stuff,
get a working set of username / password via cert@telekom.de
then code something like this:

    Init:

    EWSClient x = new EWSClient("https://www.t-sec-radar.de/ews-0.1/alert/retrieveIPs", true);
    String authToken = x.getMessage(USERNAME, PASSWORD);

    Call in a loop:

    List ips = x.fetchIPs(authToken);

If you want to deploy the code in a productive environment, ensure, that you have about 2000 * 100 bytes
memory for Redis allocated to keep everything in memory.


Using the supplied demo code:
-----------------------------

Create a directory config in your user home.

E.g. for my setup

mkdir /Users/flake/config


Create a file configs.txt within this directory with the following lines:

badipfetchname=YOUR USERNAME
badipfetchpw=YOUR PASSWORD
badipfetchserver=https://www.t-sec-radar.de/ews-0.1/alert/retrieveIPs


For the community version, use the following code:

badipfetchname=YOUR USERNAME
badipfetchpw=YOUR PASSWORD
badipfetchserver=https://www.t-sec-radar.de/ews-0.1/alert/retrieveCommunityIPs


Compile the code using the supplied ant script.

Demo output (API 1.0 only):
---------------------------

Sat Mar 02 08:44:12 CET 2013: Info: Fetching IPs from EWS as local DB is outdated
Answer from server: <EWSSimpleIPInfo><Sources><Source><Address>100.200.300.400</Address></Source></EWSSimpleIPInfo>
Info: Extracted 1000 IPs at time Sat Mar 02 08:44:21 CET 2013
Sat Mar 02 08:44:21 CET 2013: Info: Set last update of database
Sat Mar 02 08:44:21 CET 2013: Info: Set ip number to 1000 within database


Demo output (API 1.1 ++):
---------------------------

<EWSSimpleIPInfo><Sources><Source category='ipv4' foundByType='100000'>188.173.62.BLAH</Source>




How to implement a cron job alike call (dumping IPs at every call):
-------------------------------------------------------------------

ant
call ./start.sh with username password server as parameters



Cheers

Markus

Contact: cert@telekom.de





