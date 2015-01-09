package no.bekk.bootcamp;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.dstream.DStream;
import org.apache.spark.streaming.twitter.TwitterUtils;
import scala.Tuple2;
import twitter4j.Status;

import java.io.IOException;
import java.util.Properties;

import static java.util.Arrays.asList;

public class SparkApplication {

    public void run() throws InterruptedException, IOException {
        Properties twitterProperties = new Properties();
        twitterProperties.load(SparkApplication.class.getResourceAsStream("/twitter.properties"));
        System.getProperties().putAll(twitterProperties);

        SparkConf conf = new SparkConf().setMaster("local[2]").setAppName("twitterApp");
        JavaStreamingContext streamingContext = new JavaStreamingContext(conf, Durations.seconds(15));

        JavaReceiverInputDStream<Status> stream = TwitterUtils.createStream(streamingContext);

        JavaDStream<String> hashTags = stream.flatMap(status -> asList(status.getText().split(" ")))
                .filter(word -> word.startsWith("#"));
        JavaPairDStream<String, Long> counted = hashTags.countByValue();

        counted.foreach(rdd -> {
            JavaRDD<Tuple2<Long, String>> freqAndWord = rdd.map(tuple -> tuple.swap());
            System.out.println("---------------------------");
            JavaRDD<Tuple2<Long, String>> sorted = freqAndWord.sortBy(tuple -> tuple._1(), false, 1);
            sorted.foreach(tuple -> {
                System.out.println(tuple._2() + ",  " + tuple._1());

                CustomWebSocketServlet.broadcastMessage(tuple.toString());
            });
            return null;
        });
        
        streamingContext.start();
        streamingContext.awaitTermination();
    }
    
    class HashTag {
        public final String word;
        public final int count;

        public HashTag(String word, int count) {
            this.word = word;
            this.count = count;
        }
    }
}
