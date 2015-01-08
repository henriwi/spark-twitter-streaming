package no.bekk.bootcamp;

import org.apache.spark.SparkConf;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.twitter.TwitterUtils;
import org.eclipse.jetty.websocket.WebSocket;
import twitter4j.Status;

import java.io.IOException;
import java.util.Properties;

public class SparkApplication {

    public void run() throws InterruptedException, IOException {
        Properties twitterProperties = new Properties();
        twitterProperties.load(SparkApplication.class.getResourceAsStream("/twitter.properties"));
        System.getProperties().putAll(twitterProperties);

        SparkConf conf = new SparkConf().setMaster("local[2]").setAppName("twitterApp");
        JavaStreamingContext streamingContext = new JavaStreamingContext(conf, Durations.seconds(1));

        JavaReceiverInputDStream<Status> stream = TwitterUtils.createStream(streamingContext);

        stream.filter(s -> s.getGeoLocation() != null)
                .foreach(rdd -> {
                    rdd.foreach(s -> {
                        System.out.println(s);
                        WebSocket.Connection connection = CustomWebSocketServlet.getConnection();
                        if (connection != null) {
                            connection.sendMessage(s.getText());
                        }
                    });
                    return null;
                });

        streamingContext.start();
        streamingContext.awaitTermination();
    }
}
