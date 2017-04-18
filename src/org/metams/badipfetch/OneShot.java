package org.metams.badipfetch;

import java.io.UnsupportedEncodingException;
import java.util.List;


public class OneShot
{

    private String m_password = null;
    private String m_username = null;

    public void Startup()
    {

    }

    /**
     * run the EWS client
     * @param password
     * @param username
     * @param server
     * @throws InterruptedException
     */
    public void run(String username, String password, String server) throws InterruptedException
    {
        EWSClient x = new EWSClient(server, true);

            try
            {

                String authToken = x.getMessage(password, username);
                List ips = x.fetchIPs(authToken, false);

                if (ips == null)
                {
                    System.out.println("Error: Unable to retrieve IPs");
                }

                for (int runner = 0; runner <= ips.size() -1; runner++)
                {
                    System.out.println(ips.get(runner));
                }

            }
            catch (UnsupportedEncodingException ex2)
            {
                System.out.println("Error: SupportedEncoding exception caught");
            }

    }


    /**
     * main code for the startup / test class
     * @param args
     */
    public static void main(String[] args)
    {

        if (args.length != 3)
        {
            System.out.println("Error, wrong command line parameters");
            System.out.println("Please use ./start.sh username password server");
            return;
        }

        OneShot myOne = new OneShot();
        try
        {
            myOne.run(args[0], args[1], args[2]);
        }
        catch (InterruptedException e)
        {
            System.out.println("Info: Caught exception within wait loop");
        }

    }   // main

}
