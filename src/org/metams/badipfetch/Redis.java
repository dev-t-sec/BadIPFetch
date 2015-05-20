package org.metams.badipfetch;

import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;


public class Redis
{

    // connection to Redis DB via Jedis
    private Jedis   m_con = null;
    private boolean m_verbose = true;


    /**
     * constructor for the Redis class
     * @param verbose
     */
    public Redis(boolean verbose)
    {
        m_verbose = verbose;
    } // Redis constructor


    /**
     * constructor for the Redis class
     */
    public Redis()
    {
        m_verbose = true;

    }   // Redis constructor


    /**
     * database open
     *
     * @return  true on success, false on fail
     */
    public boolean open()
    {

        try
        {
            m_con = new Jedis("localhost");
            m_con.connect();
            return (m_con != null);
        }
        catch (Exception e)
        {
            return false;
        }

    }   // open


    /**
     *
     * @return
     */
    public List getIPs()
    {
        List<String> ls=new ArrayList<String>();

        try
        {

            int counterFromDB = new Integer(m_con.get("NUMBER_OF_IPS")).intValue();

            if (m_verbose)
                System.out.println(new java.util.Date().toString() + ": Info: Retrieved " + new Integer(counterFromDB).toString() + " ips from database");

            for (int runner = 0; runner <= counterFromDB - 1; runner++)
            {
                String ip = m_con.get("IP_" + new Integer(runner).toString());
                ls.add(ip);
            }
        }
        catch (Exception e)
        {
            if (m_verbose)
                System.out.println(new java.util.Date().toString() + ": Error: Retrieving ips from database broken down");

        }

        return ls;
    }   // getIPs


    /**
     * sets a new ip
     * @param ip
     * @param number of ip
     */
    public void setIP(String ip, int ipCounter, boolean increaseNumberOfIPs)
    {
        m_con.set("IP_" + new Integer(ipCounter).toString(), ip);

        if (increaseNumberOfIPs)
        {
            String n = getNumberOfIPs();
            int nn = new Integer(n).intValue() + 1;
            setNumberOfIPs(nn);
        }

    }


    /**
     * sets a new ip
     * @param ip
     */
    public void setIP(String ip, boolean increaseNumberOfIPs)
    {
        m_con.set("IP_" + getNumberOfIPs(), ip);

        if (increaseNumberOfIPs)
        {
            String n = getNumberOfIPs();
            int nn = new Integer(n).intValue() + 1;
            setNumberOfIPs(nn);
        }

    }


    /**
     * checks if a a given IP is in the database
     * @param ip
     * @return
     */
    public boolean checkKnownIP(String ip)
    {
        List x = getIPs();

        for (int runner = 0; runner <= x.size() - 1; runner++)
        {

            String key = (String)x.get(runner);
            if (key != null && key.equals(ip))
                return true;
        }

        return false;
    }


    /**
     * delete a give IP from the redis server
     * @param ipToDelete
     */
    public boolean deleteIP(String ipToDelete)
    {

        boolean code = false;

        int numberOfIPS = new Integer(getNumberOfIPs()).intValue();

        for (int runner = 0; runner <= numberOfIPS - 1 ; runner++ )
        {

            String ip = m_con.get("IP_" + new Integer(runner).toString());
            if (ip != null && ip.equals(ipToDelete))
            {
                m_con.del("IP_" + new Integer(runner).toString());
                code = true;
            }

        }
        return code;
    }


    /**
     * sets the number of stored IPs
     * @return
     */
    public void setNumberOfIPs(int number)
    {
        m_con.set("NUMBER_OF_IPS", new Integer(number).toString());

        if (m_verbose)
            System.out.println(new java.util.Date().toString() + ": Info: Set ip number to " + new Integer(number).toString() + " within database");

    }   // setNumberOfIPs


    /**
     * returns the number of stored IPs
     * @return
     */
    public String getNumberOfIPs()
    {

        String x = m_con.get("NUMBER_OF_IPS");

        if (x == null || x.equals(""))
            return "0";
        else
            return x;

    }   // getNumberOfIPs


    /**
     * returns the number of stored IPs
     * @return
     */
    public String getLastUpdateTimeOfIPs()
    {
        return m_con.get("LAST_UPDATE_OF_IPS");
    }   // getLastUpdate TimeOfIPs


    /**
     * sets the number of stored IPs
     * @return
     */
    public void setLastUpdate(String date, int numberOfIps, List ips)
    {
        try {
            m_con.set("LAST_UPDATE_OF_IPS", date);

            if (m_verbose)
                System.out.println(new java.util.Date().toString() + ": Info: Set last update of database");

            setNumberOfIPs(numberOfIps);

            for (int runner = 0; runner <= numberOfIps - 1; runner++) {
                String ip = (String) ips.get(runner);
                setIP(ip, runner, false);
            }
        }
        catch (redis.clients.jedis.exceptions.JedisConnectionException e)
        {
            System.out.println("BadIpFetch: Warning, Redis database not available.");
        }

    }   // setLastUpdate

}
