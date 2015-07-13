package org.metams.badipfetch;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.NTCredentials;

import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.HttpResponseException;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;

import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.*;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;

import java.io.UnsupportedEncodingException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * core class for sending data to the DTAG early warning system
 */
public class EWSClient
{

    private String          m_url = null;
    private Redis           m_db = new Redis();
    private boolean         m_error = false;
    private boolean         m_productionMode = true;


    /**
     * constructor for the EWS client class
     * @param url
     * @param productionMode
     */
    public EWSClient(String url, boolean productionMode)
    {
        if (productionMode)
        {
            m_url = url;
            m_error = !m_db.open();
        }
        else
        {
            m_productionMode = productionMode;
        }
    }


    /**
     * returns the query message including account data to be send to the server
     * @param token
     * @param userName
     * @return
     * @throws java.io.UnsupportedEncodingException
     */
    public String getMessage(String token, String userName) throws UnsupportedEncodingException
    {

        return "<EWS-SimpleMessage version=\"1.0\">\n" +
                "        <Authentication>\n" +
                "                <username>" + userName + "</username>\n" +
                "                <token>" + token + "</token>\n" +
                "        </Authentication>\n" +
                "</EWS-SimpleMessage>";

    }     // getMessage




    /**
     *
     * @param authToken
     * @return
     */
    public List fetchIPs(String authToken, boolean verbose)
    {

        // check for productive / demo mode
        if (!m_productionMode)
        {
            List<String> s = new ArrayList<String>();
            s.add("127.0.0.1");
            return s;
        }

        // if somekind of error appeared before, just quick
        if (m_error)
        {
            return fetchIPsFromCore(authToken, verbose);
        }

        // in production mode, return real values
        long currentTime = new java.util.Date().getTime();
        String lastUpdateString = m_db.getLastUpdateTimeOfIPs();

        if (lastUpdateString == null)
        {
            // no data stored in db

            if (verbose) System.out.println(new java.util.Date().toString() + ": Info: Fetching ips from EWS as local DB is empty");

            return fetchIPsFromCore(authToken, verbose);
        }
        else
        {
            // data already stored in DB

            long upd = Long.parseLong(lastUpdateString);

            if (currentTime - upd >= 1000 * 60 * 2)
            {
                if (verbose) System.out.println(new java.util.Date().toString() + ": Info: Fetching IPs from EWS as local DB is outdated");

                return fetchIPsFromCore(authToken, verbose);
            }
            else
            {
                if (verbose) System.out.println(new java.util.Date().toString() + ": Info: Fetching IPs from database as current");

                return m_db.getIPs();
            }
        }
    }


    /**
     * queries the server for the bad IPs
     * @param authToken
     * @return
     */
    public List fetchIPsFromCore(String authToken, boolean verbose)
    {


        if (!m_productionMode)
        {
            List<String> s = new ArrayList<String>();
            s.add("127.0.0.1");
            return s;
        }

        try
        {
            HttpPost method = new HttpPost(m_url);

            CloseableHttpClient client = HttpClients.createDefault();

            StringEntity strent = new StringEntity(authToken);
            strent.setContentType("text/xml; charset=utf-8");
            method.setEntity(strent);
            ResponseHandler<String> response = new BasicResponseHandler();

            String returnCode = client.execute(method, response);

            if (verbose)
                System.out.println("Answer from server: " + returnCode);
            else
                System.out.println(returnCode);

            List newIPs = extractIPs(returnCode, verbose);

            m_db.setLastUpdate(String.valueOf(new java.util.Date().getTime()), newIPs.size(), newIPs);

            return newIPs;

        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("Error at BadIPFetch.EWSClient.fetch(" + new Date().toString() + "): Exception caught");
            return null;
        }

    }	// fetchIPsfromCore



    /**
     * extract IPs from a given string
     * @param ips
     * @return
     */
    private List extractIPs(String ips, boolean verbose)
    {
        String startValue = "<Address>";
        String endValue = "</Address>";
        int runner = 0;
        int counter = 0;


        // fix for extended API introduced June 2013
        if (m_url.contains("/api/"))
        {
            startValue = "'>";
            endValue = "</Source>";
        }

        List<String> ls=new ArrayList<String>();

        while (runner <= ips.length() -1)
        {
            int startIndex = ips.indexOf(startValue, runner);
            int endIndex = ips.indexOf(endValue, runner + startValue.length());

            if (startIndex == -1)
                runner = ips.length();
            else if (endIndex == -1)
                runner = ips.length();
            else
            {
                // handle found data, correct indices and increase counter


                String ip = ips.substring(startIndex +startValue.length(), endIndex);
                ls.add(ip);
                runner = endIndex + endValue.length();
                counter++;
            }

        }


        if (verbose) System.out.println("Info: Extracted " + counter + " IPs at time " + new java.util.Date().toString());

        return ls;

    }   // extractIPs

}
