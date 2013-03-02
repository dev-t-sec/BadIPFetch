package org.metams.badipfetch;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;

import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.UnsupportedEncodingException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: flake
 * Date: Jun 13, 2010
 * Time: 8:37:15 PM
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
    public List fetchIPs(String authToken)
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
            return null;
        }

        // in production mode, return real values
        long currentTime = new java.util.Date().getTime();
        String lastUpdateString = m_db.getLastUpdateTimeOfIPs();

        if (lastUpdateString == null)
        {
            // no data stored in db

            System.out.println(new java.util.Date().toString() + ": Info: Fecthing ips from EWS as local DB is empty");

            return fetchIPsFromCore(authToken);
        }
        else
        {
            // data already stored in DB

            long upd = Long.parseLong(lastUpdateString);

            if (currentTime - upd >= 1000 * 60 * 2)
            {
                System.out.println(new java.util.Date().toString() + ": Info: Fetching IPs from EWS as local DB is outdated");

                return fetchIPsFromCore(authToken);
            }
            else
            {
                System.out.println(new java.util.Date().toString() + ": Info: Fetching IPs from database as current");

                return m_db.getIPs();
            }
        }
    }


    /**
     * queries the server for the bad IPs
     * @param authToken
     * @return
     */
    public List fetchIPsFromCore(String authToken)
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
            DefaultHttpClient client = new DefaultHttpClient();

            //DefaultHttpClient client = getFake(base);
            client.getParams().setParameter("http.useragent", "Flake Test Client");

            StringEntity strent = new StringEntity(authToken);
            strent.setContentType("text/xml; charset=utf-8");
            method.setEntity(strent);
            ResponseHandler<String> response = new BasicResponseHandler();

            String returnCode = client.execute(method, response);
            System.out.println("Answer from server: " + returnCode);

            List newIPs = extractIPs(returnCode);

            m_db.setLastUpdate(String.valueOf(new java.util.Date().getTime()), newIPs.size(), newIPs);

            return newIPs;

        }
        catch (Exception e)
        {
            System.out.println("Error at BadIPFetch.EWSClient.fetch(" + new Date().toString() + "): Exception caugh");
            return null;
        }

    }	// fetchIPsfromCore


    /**
     * extract IPs from a given string
     * @param ips
     * @return
     */
    private List extractIPs(String ips)
    {
        String startValue = "<Source><Address>";
        String endValue = "</Address></Source>";
        int runner = 0;
        int counter = 0;

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


        System.out.println("Info: Extracted " + counter + " IPs at time " + new java.util.Date().toString());

        return ls;

    }   // extractIPs

}