package com.codepath.apps.twittertimeline.activities;

import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.codepath.apps.twittertimeline.R;
import com.codepath.apps.twittertimeline.TwitterApplication;
import com.codepath.apps.twittertimeline.TwitterClient;
import com.codepath.apps.twittertimeline.databinding.ActivityMediaBinding;
import com.codepath.apps.twittertimeline.models.Media;
import com.codepath.apps.twittertimeline.models.Tweet;
import com.codepath.apps.twittertimeline.utils.Constants;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class MediaActivity extends AppCompatActivity {
    public static final String TWEET_TO_DISPLAY = "tweet_to_display";
    public static final String TWEET_POSITION = "tweet_position";
    public static final String MODIFIED_TWEET = "modified_tweet";
    public static final String ACTION_PERFORMED = "action_performed";

    public static final int OPEN_MEDIA_ACTIVITY_REQUEST_CODE = 101;
    private static final String TAG = MediaActivity.class.getSimpleName();

    TwitterClient client;
    @BindView(R.id.ivImage)
    ImageView ivImage;
    @BindView(R.id.videoView)
    VideoView videoView;
    @BindView(R.id.actionFavorite)
    ImageView ivFavorite;
    @BindView(R.id.tvFavoritesCount)
    TextView tvFavoritesCount;
    @BindView(R.id.tvRetweetCount)
    TextView tvRetweetCount;
    @BindView(R.id.actionRetweet)
    ImageView ivRetweet;
    @BindView(R.id.actionReply)
    ImageView ivReply;
    int mPosition;
    Tweet tweet;
    Tweet modifiedTweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media);
        ButterKnife.bind(this);
        client = TwitterApplication.getRestClient();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tweet = getIntent().getParcelableExtra(TWEET_TO_DISPLAY);
        mPosition = getIntent().getIntExtra(TWEET_POSITION,0);
        Picasso.with(this)
                .load(tweet.getMedia().getMediaUrlHttps()+":large")
                .into(ivImage);
        if(tweet.isFavorited()){
            ivFavorite.setImageResource(R.drawable.ic_favorited);
        }else{
            ivFavorite.setImageResource(R.drawable.ic_favorite_white);
        }
        if(tweet.isRetweeted()){
            ivRetweet.setImageResource(R.drawable.ic_retweeted);
        }else{
            ivRetweet.setImageResource(R.drawable.ic_retweet_white);
        }
        ivReply.setImageResource(R.drawable.ic_reply_white);

        if(tweet.getFavoritesCount() > 0){
            tvFavoritesCount.setVisibility(View.VISIBLE);
            tvFavoritesCount.setText(String.valueOf(tweet.getFavoritesCount()));
            tvFavoritesCount.setTextColor(ContextCompat.getColor(MediaActivity.this,R.color.white));
        }else{
            tvFavoritesCount.setVisibility(View.GONE);
        }
        if(tweet.getRetweetCount() > 0){
            tvRetweetCount.setVisibility(View.VISIBLE);
            tvRetweetCount.setText(String.valueOf(tweet.getRetweetCount()));
            tvRetweetCount.setTextColor(ContextCompat.getColor(MediaActivity.this,R.color.white));
        }else{
            tvRetweetCount.setVisibility(View.GONE);
        }
        Media extendedMedia = tweet.getExtendedMedia();
        if(extendedMedia != null){
            if(extendedMedia.getType().equalsIgnoreCase(Constants.VIDEO_KEY)){
                MediaController videoControl = new MediaController(this);
                videoControl.setAnchorView(videoView);
                videoView.setMediaController(videoControl);
                ivImage.setVisibility(View.GONE);
                videoView.setVisibility(View.VISIBLE);
                Uri videoUri = Uri.parse(extendedMedia.getVideoUrlHttps());
                videoView.setVideoURI(videoUri);
                videoView.start();
            }else{
                ivImage.setVisibility(View.VISIBLE);
                videoView.setVisibility(View.GONE);
            }
        }
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void favoriteTweet(final int position, Tweet tweet) {
        boolean create = !tweet.isFavorited();
        client.favoriteTweet(create,tweet.getUid(),new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    //JSONObject jsonObject = response.getJSONObject(0);
                    modifiedTweet = Tweet.fromJSON(response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e(TAG,"Error while favoriting a tweet - "+errorResponse.toString());
                Toast.makeText(MediaActivity.this,"Favorite unsuccessful",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void retweetTweet(final int position, Tweet tweet){
        boolean create = !tweet.isRetweeted();
        client.retweet(create,tweet.getUid(),new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    modifiedTweet = Tweet.fromJSON(response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e(TAG,"Error while favoriting a tweet - "+errorResponse.toString());
                Toast.makeText(MediaActivity.this,"Retweet unsuccessful",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
