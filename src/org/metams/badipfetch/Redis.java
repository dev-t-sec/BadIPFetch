package org.metams.badipfetch;

import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: flake
 * Date: 12/10/12
 * Time: 9:29 PM
 * To change this template use File | Settings | File Templates.
 */
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

        int counterFromDB = new Integer(m_con.get("NUMBER_OF_IPS")).intValue();

        if (m_verbose)
            System.out.println(new java.util.Date().toString() + ": Info: Retrieved " + new Integer(counterFromDB).toString() + " ips from database");

        for (int runner = 0; runner <= counterFromDB - 1; runner++)
        {
            String ip = m_con.get("IP_" + new Integer(runner).toString());
            ls.add(ip);
        }

        return ls;
    }   // getIPs


    /**
     * sets a new ip
     * @param ip
     */
    public void setIP(String ip, int ipCounter)
    {
        m_con.set("IP_" + new Integer(ipCounter).toString(), ip);
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
        return m_con.get("NUMBER_OF_IPS");
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
        m_con.set("LAST_UPDATE_OF_IPS", date);

        if (m_verbose)
            System.out.println(new java.util.Date().toString() + ": Info: Set last update of database");

        setNumberOfIPs(numberOfIps);

        for (int runner = 0; runner <=numberOfIps - 1; runner++)
        {
            String ip = (String)ips.get(runner);
            setIP(ip, runner);
        }

    }   // setLastUpdate

}
