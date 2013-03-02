package org.metams.badipfetch;

import java.io.UnsupportedEncodingException;
import java.util.List;

import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.util.Properties;



/**
 * Created with IntelliJ IDEA.
 * User: flake
 * Date: 12/9/12
 * Time: 9:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class Startup
{

    private String m_password = null;
    private String m_username = null;

    public void Startup()
    {

    }


    /**
     * handling the properties from a file
     * @return
     */
    private boolean handleProperties()
    {
        String fileName = System.getProperty("user.home")+"/config/configs.txt";

        try
        {
            Properties properties = new Properties();
            BufferedInputStream stream = new BufferedInputStream(new FileInputStream(fileName));
            properties.load(stream);
            stream.close();
            m_password = properties.getProperty("badipfetchpw");
            m_username = properties.getProperty("badipfetchname");
        }
        catch (Exception e )
        {
            System.out.println("Error: Not able to read properties file at " + fileName);
            return false;
        }

        return true;
    }

    /**
     * run the EWS client
     * @param args
     * @throws InterruptedException
     */
    public void run(String[] args) throws InterruptedException
    {
        EWSClient x = new EWSClient("https://www.t-sec-radar.de/ews-0.1/alert/retrieveIPs", true);
        handleProperties();

        for (int runner = 0; runner <= 100; runner++)
        {

            try
            {
                String authToken = x.getMessage(m_password, m_username);
                List ips = x.fetchIPs(authToken, true);
            }
            catch (UnsupportedEncodingException ex2)
            {
                System.out.println("Error: SupportedEncoding exception caught");
            }

            Thread.sleep(1000*30);

        }


    }


    /**
     * main code for the startup / test class
     * @param args
     */
    public static void main(String[] args)
    {
        Startup myOne = new Startup();
        try
        {
            myOne.run(args);
        }
        catch (InterruptedException e)
        {
            System.out.println("Info: Caught exception within wait loop");
        }
    }   // main

}
