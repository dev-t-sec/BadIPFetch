package org.metams.badipfetch;

import java.io.UnsupportedEncodingException;
import java.util.List;

import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.util.Properties;



/**
 *
 */
public class Startup
{

    private String m_password = null;
    private String m_username = null;
    private String m_server   = null;

    /**
     * default constructor
     */
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
            m_server = properties.getProperty("badipfetchserver");

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

        handleProperties();

        // if no data can be read, we have to abort
        if (m_password == null || m_username == null || m_server == null)
        {
            return;
        }

        EWSClient x = new EWSClient(m_server, true);

        for (int runner = 0; runner <= 10000; runner++)
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
