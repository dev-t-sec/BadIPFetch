package org.metams.badipfetch;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.UnsupportedEncodingException;
import java.util.List;

public class Monitor {

    private String m_password = null;
    private String m_username = null;

    public void Startup()
    {

    }


    private void sendSlack(String token, String message, boolean verbose)
    {

        try {

            HttpPost method = new HttpPost(token);

            CloseableHttpClient client = HttpClients.createDefault();

            StringEntity strent = new StringEntity("payload={\"text\":\"" + message + "\"}");
            strent.setContentType("application/x-www-form-urlencoded");
            method.setEntity(strent);
            ResponseHandler<String> response = new BasicResponseHandler();

            String returnCode = client.execute(method, response);

            if (verbose)
                System.out.println("Answer from server: " + returnCode);
            else
                System.out.println(returnCode);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }


    /**
     * run the EWS client
     * @param password
     * @param username
     * @param server
     * @param slack-api-token
     * @throws InterruptedException
     */
    public void run(String username, String password, String server, String slackAPIToken) throws InterruptedException
    {
        EWSClient x = new EWSClient(server, true);

        try
        {

            String authToken = x.getMessage(password, username);
            List ips = x.fetchIPs(authToken, false);

            if (ips == null)
            {
                System.out.println("Error: Unable to retrieve IPs");
                sendSlack(slackAPIToken, "Error at Honeypotbackend IP retrieval", true);
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

        if (args.length != 4)
        {
            System.out.println("Error, wrong command line parameters");
            System.out.println("Please use ./start.sh username password server slack-api-token");
            return;
        }

        Monitor myOne = new Monitor();
        try
        {
            myOne.run(args[0], args[1], args[2], args[3]);
        }
        catch (InterruptedException e)
        {
            System.out.println("Info: Caught exception within wait loop");
        }

    }   // main



}
