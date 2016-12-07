package org.research.sink.logstash.request;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.metrics2.AbstractMetric;
import org.apache.hadoop.metrics2.MetricsRecord;
import org.apache.hadoop.metrics2.MetricsTag;
import org.joda.time.DateTime;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by centos on 12/5/16.
 */
public class LogstashRequestBuilder {

    MetricsRecord record;
    private String [] hosts = null;
    private String index;
    public final Log LOG = LogFactory.getLog( this.getClass() );

    public MetricsRecord getRecord() {
        return record;
    }

    public LogstashRequestBuilder setRecord(MetricsRecord record) {
        this.record = record;
        return this;
    }

    public String[] getHosts() {
        return hosts;
    }

    public LogstashRequestBuilder setHosts(String[] hosts) {
        this.hosts = hosts;
        return this;
    }

    public String getIndex() {
        return index;
    }

    public LogstashRequestBuilder setIndex(String index) {
        this.index = index;
        return this;
    }

    public LogstashRequest build(){
        return new LogstashRequest().setFields( extractMetrics(this.record) ).setHosts( hosts );
    }

    private Map<String, Object> extractMetrics(MetricsRecord record) {
        Map<String, Object> send = new HashMap();

        if( index != null && index.length() > 0 ){
            send.put("type", index);
        }

        send.put("@timestamp", new DateTime( record.timestamp() ).toString("yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
        send.put("context", record.context());
        send.put("name", record.name());

        String separator = ": ";
        for (MetricsTag tag : record.tags()) {
            Object o = getData(tag.value());
            if (o != null && tag.name() != null) {
                send.put(tag.name(), o);
            }
        }
        for (AbstractMetric metric : record.metrics()) {
            if (metric.value() != null && metric.name() != null) {
                send.put(metric.name(), metric.value());
            }
        }
        return send;
    }

    private Object getData(String in){
        if(in == null) return in;

        String s = in.trim();

        if(s == null){
            return s;
        }

        int len = s.length();

        if( len == 0 ){
            return s;
        }

        int i = 0;
        if(s.charAt(0) == '-' && len > 1){
            i = 1;
        }

        boolean decimalFound = false;
        for (; i < len; i++) {
            char c = s.charAt(i);
            if(c == '.') {
                if(!decimalFound){
                    decimalFound = true;
                }else{
                    return s;
                }
            }else if ( ( c < '0' || c > '9' ) && c != '.') {
                return s;
            }
        }

        if(decimalFound){
            return new Float(s);
        }

        return new Integer(s);
    }
}