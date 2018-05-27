package ca.javajeff.projettw;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.Twitter;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;

import twitter4j.GeoLocation;
import twitter4j.Query;

import static twitter4j.Query.*;

/**
 * The MainActivity is the application entry point. Sets up the twitter session and provides
 * twitter authentication trough the twitter login botton. This activity also launches twitter
 * query.
 */

public class MainActivity extends AppCompatActivity {

    /**
     * This class wraps Query and twitter classes toghether
     * in an object to be passed to the Async Task
     */
    protected class QueryWrapper {
        private Query query;
        private MainActivity sentimentQuery;

        public Query getQuery() {
            return query;
        }

        public MainActivity getSentimentQuery() {
            return sentimentQuery;
        }

        public QueryWrapper(Query query, MainActivity sentimentQuery){
            this.query = query;
            this.sentimentQuery = sentimentQuery;
        }
    }

    TwitterLoginButton loginButton;
    TwitterSession mySession;

    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        /**
         * Twitter session set up
         */
        TwitterConfig config = new TwitterConfig.Builder(this)
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(new TwitterAuthConfig(getString(R.string.CONSUMER_KEY), getString(R.string.CONSUMER_SECRET)))
                .debug(true)
                .build();
        Twitter.initialize(config);
        setContentView(R.layout.activity_main);

        /**
         * twitter login button
         */
        loginButton = (TwitterLoginButton) findViewById(R.id.login_button);
        loginButton.setCallback(new Callback<TwitterSession>() {

            @Override
            public void success(Result<TwitterSession> result) {
                TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();
                TwitterAuthToken authToken = session.getAuthToken();
                String token = authToken.token;
                String secret = authToken.secret;
                
                login(session);

            }

            @Override
            public void failure(TwitterException exception) {
                Toast.makeText(MainActivity.this, "Authentication Failed", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void login(TwitterSession session) {
        String username = session.getUserName();

    }

    /**
     * called when the user taps the login button
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result to the login button.
        loginButton.onActivityResult(requestCode, resultCode, data);

    }

    /**
     * Called when the user taps the Send button
     * @param view
     */
    protected void sendQuery(View view) {
        EditText editText = (EditText) findViewById(R.id.editQuery);
        String message = editText.getText().toString();

        Intent intent = new Intent(this, TweetQuery.class);
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    /**
     * Called when the user taps the sentiment button
     * @param view
     */
    protected void getSentiment(View view) {

        // clear text view
        TextView textView = findViewById(R.id.SentimentView);
        textView.setText(String.valueOf(""));

        EditText editQuery = (EditText) findViewById(R.id.editQuery);

        EditText editLoc = (EditText) findViewById(R.id.editLoc);


        Query query = new Query(editQuery.getText().toString());
        query.setLang("en");
        query.setResultType(POPULAR);

        String loc = editLoc.getText().toString();
        if (loc != "Location") {
            try {
                LatLng coord = this.getLocationFromAddress(this, loc);
                GeoLocation geoLocation = new GeoLocation(coord.latitude, coord.longitude);
                double radius = 50.;
                query.setGeoCode(geoLocation, radius, KILOMETERS);
            }
            catch (Exception e) {
                Toast.makeText(this, "Location not found", Toast.LENGTH_LONG).show();
            }
        }

        QueryWrapper wrap = new QueryWrapper(query, this);

        new RetriveTweets().execute(wrap);
    }

    /**
     * Called by show the result of a sentiment analysis
     * @param positive
     * @param negative
     */
    protected void onQueryReturn(int positive, int negative) {
        TextView textView = findViewById(R.id.SentimentView);
        textView.setText(String.valueOf(positive) + " positive tweets out of " + String.valueOf(positive + negative));
    }

    private LatLng getLocationFromAddress(Context context,String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 1);
            if (address == null) {
                return null;
            }

            Address location = address.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude() );

        } catch (IOException ex) {

            ex.printStackTrace();
        }

        return p1;
    }

}
