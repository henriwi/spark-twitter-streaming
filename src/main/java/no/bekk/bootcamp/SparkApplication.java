package no.bekk.bootcamp;

import org.apache.spark.SparkConf;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.twitter.TwitterUtils;
import scala.Tuple2;
import twitter4j.HashtagEntity;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import static java.util.Arrays.asList;

public class SparkApplication {

    public void run() throws InterruptedException, IOException {
        Properties twitterProperties = new Properties();
        twitterProperties.load(SparkApplication.class.getResourceAsStream("/twitter.properties"));
        System.getProperties().putAll(twitterProperties);

        SparkConf conf = new SparkConf().setMaster("local[2]").setAppName("twitterApp");
        JavaStreamingContext streamingContext = new JavaStreamingContext(conf, Durations.milliseconds(100));

        TwitterUtils.createStream(streamingContext)
                .window(Durations.seconds(60), Durations.seconds(5))
                .flatMap(status -> asList(status.getHashtagEntities()))
                .countByValue()
                .foreach(rdd -> {
                    List<Tuple2<Long, HashtagEntity>> data = rdd.map(Tuple2::swap)
                            .sortBy(tuple -> tuple._1, false, 1)
                            .take(3);
                    //.forEach(tuple -> CustomWebSocketServlet.broadcastMessage(tuple._2.getText() + "," + tuple._1));
                    CustomWebSocketServlet.broadcastMessage(data);
                    return null;
                });

        streamingContext.start();
        streamingContext.awaitTermination();
    }
}
