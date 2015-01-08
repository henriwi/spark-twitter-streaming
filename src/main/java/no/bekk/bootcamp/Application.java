package no.bekk.bootcamp;

import org.apache.spark.SparkConf;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.twitter.TwitterUtils;
import twitter4j.Status;

import java.io.IOException;
import java.util.Properties;

public class Application {

    public static void main(String[] args) throws InterruptedException, IOException {
        Properties twitterProperties = new Properties();
        twitterProperties.load(Application.class.getResourceAsStream("/twitter.properties"));
        System.getProperties().putAll(twitterProperties);

        SparkConf conf = new SparkConf().setMaster("local[2]").setAppName("twitterApp");
        JavaStreamingContext streamingContext = new JavaStreamingContext(conf, Durations.seconds(1));

        JavaReceiverInputDStream<Status> stream = TwitterUtils.createStream(streamingContext);

        stream.print();

        streamingContext.start();
        streamingContext.awaitTermination();
    }
}
