package no.bekk.bootcamp;

import org.apache.spark.SparkConf;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.twitter.TwitterUtils;
import twitter4j.Status;

public class Application {

    public static void main(String[] args) throws InterruptedException {
        System.setProperty("twitter4j.oauth.consumerKey", "");
        System.setProperty("twitter4j.oauth.consumerSecret", "");
        System.setProperty("twitter4j.oauth.accessToken", "");
        System.setProperty("twitter4j.oauth.accessTokenSecret", "");

        SparkConf conf = new SparkConf().setMaster("local[2]").setAppName("twitterApp");
        JavaStreamingContext javaStreamingContext = new JavaStreamingContext(conf, Durations.seconds(1));

        JavaReceiverInputDStream<Status> stream = TwitterUtils.createStream(javaStreamingContext);

        stream.filter(status -> status.isRetweet()).print();
    }
}
