/**
 * Facilitates the HTTP operations required to attain
 * the menu data by way of a Gson script. 
 * 
 * @author Jeff Butterfield and Shouvik Dutta
 * 
 */

package com.appspot.berkeleydining;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;

import com.google.gson.Gson;

public class MenuGetter {

    /** Fetches the menu data from server with a Gson script */
    public DiningHalls menuFetch() {
        Gson gson = new Gson();
        HttpClient client = new DefaultHttpClient();
        String line;
        String line2 = "";
        try {
            HttpGet get = new HttpGet("http://berkeleydining.appspot.com/menu");
            BasicHttpContext context = new BasicHttpContext();
            HttpResponse response2 = client.execute(get, context);
            if (response2.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                throw new IOException(response2.getStatusLine().toString());
            }
            BufferedReader rd2 = new BufferedReader(new InputStreamReader(response2.getEntity()
                    .getContent()));
            while ((line = rd2.readLine()) != null) {
                line2 = line2 + line;
            }
            DiningHalls response = gson.fromJson(line2, DiningHalls.class);
            return response;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
}
