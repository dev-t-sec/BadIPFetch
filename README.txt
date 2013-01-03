"Fetch bad IPs"

Version 0.9, 01-01-2013

Idea
----

DTAG runs a number of honeypots to see attack traffic and collect threat information. One interesting
information is the current set of "bad" or "misused" IP adresses.

The backend logic remembers only IP adresses, which have been involved in an attack the last thirty
minutes.

This little tool provides you with the necessary interface code for it.


Dependencies:
-------------

Java 1.6 or higher (tested with both, latest 1.6 and 1.7 versions on Mac OS X)
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


Cheers

Markus

Contact: markus-schmall@t-online.de



