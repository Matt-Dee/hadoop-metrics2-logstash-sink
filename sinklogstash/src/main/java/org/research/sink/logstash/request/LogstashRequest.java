package org.research.sink.logstash.request;

import com.google.gson.Gson;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

/**
 * Created by centos on 12/5/16.
 */
public class LogstashRequest extends TreeMap{
    Random r = new Random();
    Map<String, Object> fields = new HashedMap();
    private String [] hosts;
    Gson gson = new Gson();
    public final Log LOG = LogFactory.getLog(this.getClass());

    public Map<String, Object> getFields() {
        return fields;
    }

    public LogstashRequest setFields(Map<String, Object> fields) {
        this.fields = fields;
        return this;
    }

    public String[] getHosts() {
        return hosts;
    }

    public LogstashRequest setHosts(String[] hosts) {
        this.hosts = hosts;
        return this;
    }

    public boolean sendRequest(){
        try {
            HttpClient httpClient = new DefaultHttpClient();
            String logstashUrl = String.format( "%s://%s", "http", getHost() );
            HttpPost httpPost = new HttpPost(logstashUrl);
            httpPost.addHeader("Content-Type", "application/json");
            HttpResponse httpResponse;

            httpPost.setEntity( new StringEntity( gson.toJson( getFields() ) ) );
            httpResponse = httpClient.execute(httpPost);
            HttpEntity responseEntity = httpResponse.getEntity();
            InputStream contentStream = responseEntity.getContent();
            int bytesRead = 0;
            int contentLength = (int) responseEntity.getContentLength();
            String responseBody = "";
            if (contentLength > 0) {
                byte[] bytes = new byte[contentLength];
                while (bytesRead < contentLength) {
                    long currentBytesRead = contentStream.read(bytes, bytesRead, contentLength - bytesRead);
                    bytesRead += currentBytesRead;
                }
                responseBody = new String(bytes);
            }

            logStatusLine( httpResponse.getStatusLine(), responseBody);
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String error = sw.toString();
            LOG.error( error );
        }
        return true;
    }

    private void logStatusLine(StatusLine statusLine, String responseBody){
        if (statusLine.getStatusCode() >= 400) {
            LOG.info("Failed to POST events to Logstash: " + statusLine + ", " + responseBody);
        }
        else if (statusLine.getStatusCode() >= 300) {
            LOG.info("3xx status attempting to POST events to Logstash: " + statusLine + ", " + responseBody);
        }
        else {
            LOG.info("Successful POST of events to Logstash.");
        }
    }

    private String getHost(){
        return hosts[ r.nextInt( hosts.length ) ];
    }
}