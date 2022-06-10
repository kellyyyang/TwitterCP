package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

public class ReplyActivity extends AppCompatActivity {

    public static final String TAG = ReplyActivity.class.getSimpleName();
    public static final int MAX_TWEET_REPLY_LENGTH = 140;

    EditText etReplyText;
    Button btnTweetReply;

    TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // change entire background color
        getWindow().getDecorView().setBackgroundColor(Color.parseColor("#161618"));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);

        client = TwitterApp.getRestClient(this);

        etReplyText = findViewById(R.id.etReplyText);
        btnTweetReply = findViewById(R.id.btnTweetReply);

        // set click listener on button
        btnTweetReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = getIntent();
                String userName = "@" + Parcels.unwrap(i.getParcelableExtra("user")) + " ";
                String tweetContent = userName + etReplyText.getText().toString();
                if (tweetContent.isEmpty()) {
                    Toast.makeText(ReplyActivity.this, "Sorry, your reply cannot be empty.", Toast.LENGTH_LONG).show();
                    return;
                } if (tweetContent.length() > MAX_TWEET_REPLY_LENGTH) {
                    Toast.makeText(ReplyActivity.this, "Sorry, your reply is too long.", Toast.LENGTH_LONG).show();
                    return;
                }
                Toast.makeText(ReplyActivity.this, tweetContent, Toast.LENGTH_LONG).show();
                // make an API call to Twitter to publish the tweet
                client.publishTweet(tweetContent, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.i(TAG, "onSuccess to publish tweet reply");
                        try {
                            Tweet tweet = Tweet.fromJson(json.jsonObject);
                            Log.i(TAG, "Published tweet reply says: " + tweet.body);
                            Intent intent = new Intent();
                            intent.putExtra("tweet", Parcels.wrap(tweet));
                            // set result code and bundle data for response
                            setResult(RESULT_OK, intent);
                            finish(); // closes the activity, pass data to parent
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e(TAG, "onFailure to publish tweet reply", throwable);
                    }
                });
            }
        });

    }
}