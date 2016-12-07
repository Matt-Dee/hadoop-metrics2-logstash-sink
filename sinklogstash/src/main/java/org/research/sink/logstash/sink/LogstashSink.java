package org.research.sink.logstash.sink;

import com.google.gson.Gson;
import org.apache.commons.configuration.SubsetConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.metrics2.*;
import org.research.sink.logstash.request.LogstashRequest;
import org.research.sink.logstash.request.LogstashRequestBuilder;

import java.io.*;

public class LogstashSink implements MetricsSink, Closeable {
    private static final String INDEX = "index";
    private static final String HOST = "host";
    private PrintWriter writer;
    private String [] hosts;
    private Gson gson = new Gson();
    private String index;
    public final Log LOG = LogFactory.getLog(this.getClass());

    public void init(SubsetConfiguration conf) {
        this.hosts = conf.getStringArray(HOST);
        index = conf.getString(INDEX);
        LOG.info( "Index that will be updated = " + index );
        LOG.info( "Hosts: ");
        for(String host: hosts){
            LOG.info( "\t" + host);
        }
    }

    public void putMetrics(MetricsRecord record) {
        try {
            LogstashRequest request = new LogstashRequestBuilder().setHosts( this.hosts ).setIndex( index ).setRecord( record ).build();
            request.sendRequest();
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            LOG.error( sw.toString() );
        }
    }

    public void flush() {
        writer.flush();
    }

    public void close() throws IOException {
        writer.close();
    }
}