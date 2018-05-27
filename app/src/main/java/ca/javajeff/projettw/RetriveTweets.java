package ca.javajeff.projettw;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import twitter4j.Query;
import twitter4j.GeoLocation;
import twitter4j.QueryResult;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

/**
 * This class retrives the tweets asked in the query as a timeline using twitter4j library.
 * Then returns the sentiment of the tweets to the caller.
 * Those activity are done in background, on a different async thread.
 *
 */

class RetriveTweets extends AsyncTask<MainActivity.QueryWrapper, Void, Integer[]> {

    private SentimentAnalyzer sentimentAnalyzer;
    MainActivity sentimentQuery;
    
    private int positive;
    private int negative;
    private int neutral;
    private int nd;

    /**
     * Search for the tweets asked in the quary and performs sentiment analysis
     * @param queryWrappers
     * @return
     */

    @Override
    protected Integer[] doInBackground(MainActivity.QueryWrapper... queryWrappers) {

        Query query = queryWrappers[0].getQuery();
        this.sentimentQuery = queryWrappers[0].getSentimentQuery();

        sentimentAnalyzer = new SentimentAnalyzer();
        ArrayList<String> tweets = search(query);
        compute_sentiment(tweets);

        return new Integer[] {positive, negative};
    }

    /**
     * Shows results to main activity
     * @param result
     */

    @Override
    protected void onPostExecute(Integer[] result) {
        this.sentimentQuery.onQueryReturn(this.positive, this.negative);
    }

    private ArrayList<String> search(Query query) {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(this.sentimentQuery.getResources().getString(R.string.CONSUMER_KEY))
                .setOAuthConsumerSecret(this.sentimentQuery.getResources().getString(R.string.CONSUMER_SECRET))
                .setOAuthAccessToken(this.sentimentQuery.getResources().getString(R.string.USER_KEY))
                .setOAuthAccessTokenSecret(this.sentimentQuery.getResources().getString(R.string.USER_SECRET));

        TwitterFactory tf = new TwitterFactory(cb.build());
        Twitter twitter = tf.getInstance();

        ArrayList<String> tweets_text = new ArrayList<String>();

        try {


            QueryResult result;
            do {
                result = twitter.search(query);
                List<twitter4j.Status> tweets = result.getTweets();

                for (twitter4j.Status tweet : tweets) {
                    String user = tweet.getUser().getScreenName();
                    String text = tweet.getText();
                    tweets_text.add(text);
                }
            } while ((query = result.nextQuery()) != null);
        } catch (com.twitter.sdk.android.core.TwitterException te) {
            te.printStackTrace();
        } catch (TwitterException e) {
            e.printStackTrace();
        }
        return tweets_text;
    }

    private void compute_sentiment(ArrayList<String> args) {

        for (String tweet : args) {
            Double weightedSentiment = sentimentAnalyzer.findSentiment(tweet);
            if (weightedSentiment <= 0.0)
                this.nd++;
            else if (weightedSentiment < 1.6)
                this.negative++;
            else if (weightedSentiment <= 2.0)
                this.neutral++;
            else if (weightedSentiment < 5.0)
                this.positive++;
            else this.nd++;
        }
    }


}
