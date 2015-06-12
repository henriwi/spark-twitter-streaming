package twitter.stream;

import org.apache.spark.SparkConf;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.twitter.TwitterUtils;
import scala.Tuple2;
import twitter4j.HashtagEntity;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import static java.util.Arrays.asList;

public class SparkApplication {

    public void run() throws InterruptedException, IOException {
        setTwitterConfig();

        SparkConf conf = new SparkConf().setMaster("local[2]").setAppName("twitterApp");
        JavaStreamingContext streamingContext = new JavaStreamingContext(conf, Durations.milliseconds(1000));

        TwitterUtils.createStream(streamingContext)
                .window(Durations.seconds(180), Durations.seconds(3))
                .flatMap(status -> asList(status.getHashtagEntities()))
                .countByValue()
                .foreachRDD(rdd -> {
                    List<Tuple2<Long, HashtagEntity>> data = rdd.map(Tuple2::swap)
                            .sortBy(tuple -> tuple._1, false, 1)
                            .take(10);
                    CustomWebSocketServlet.broadcastMessage(data);
                    return null;
                });

        streamingContext.start();
        streamingContext.awaitTermination();
    }

    private static void setTwitterConfig() throws IOException {
        Properties twitterProperties = new Properties();
        InputStream twitterConfig = SparkApplication.class.getResourceAsStream("/twitter.properties");

        if (twitterConfig != null) {
            twitterProperties.load(twitterConfig);
            System.getProperties().putAll(twitterProperties);
        }
    }
}
