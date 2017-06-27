Hadoop Metrics2  Plugin for Logstash
===============================

A plugin to sink the Hadoop Metrics2 framework with Logstash.

## Getting Started

Integrating the Sink within your hadoop environment.

#### Download the sink and the metrics configutation file onto your Hadoop server(s) 

The jar and config can be found here.
```
   https://github.com/Matt-Dee/hadoop-metrics2-logstash-sink/tree/master/metrics2-config    
   https://github.com/Matt-Dee/hadoop-metrics2-logstash-sink/tree/master/jar
```

#### Add the sink jar to the hadoop classpath

Add these JAR files to your Hadoop classpath, by either one of two ways:

 * Edit [hadoop_root]/conf/hadoop_env.sh and revise the classpath to include the JARs:

  ```
  # Extra Java CLASSPATH elements.  Optional.
  export HADOOP_CLASSPATH=/path/to/my/jar/lib/*
  ```
OR

 * Add the JARs to the existing [hadoop_root]/lib directory.

#### Add & edit the sink configuration

* The sink configuration is found at:
```
   https://github.com/Matt-Dee/hadoop-metrics2-logstash-sink/tree/master/metrics2-config
```
 
* Append the contents of this file to your existing hadoop-metrics2.properties file.

#### Restart your Hadoop processes! 

#### Feel free to contribute.  If you have better ways to implement or see ways to make it better, don't hesitate. 
