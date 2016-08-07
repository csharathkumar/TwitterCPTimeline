package com.codepath.apps.twittertimeline.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.codepath.apps.twittertimeline.R;
import com.codepath.apps.twittertimeline.TwitterApplication;
import com.codepath.apps.twittertimeline.TwitterClient;
import com.codepath.apps.twittertimeline.adapters.TweetArrayAdapter;
import com.codepath.apps.twittertimeline.adapters.TweetsRecyclerAdapter;
import com.codepath.apps.twittertimeline.fragments.ComposeTweetDialogFragment;
import com.codepath.apps.twittertimeline.models.Tweet;
import com.codepath.apps.twittertimeline.utils.DividerItemDecoration;
import com.codepath.apps.twittertimeline.utils.EndlessRecyclerViewScrollListener;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity {
    private static final String TAG = TimelineActivity.class.getSimpleName();
    TwitterClient client;
    @BindView(R.id.rvTweets)
    RecyclerView rvTweets;
    TweetsRecyclerAdapter tweetsRecyclerAdapter;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.fab)
    FloatingActionButton fabCompose;
    List<Tweet> tweets;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvTweets.setLayoutManager(linearLayoutManager);
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST);
        rvTweets.addItemDecoration(itemDecoration);
        tweets = new ArrayList<>();
        tweetsRecyclerAdapter = new TweetsRecyclerAdapter(this,tweets);
        rvTweets.setAdapter(tweetsRecyclerAdapter);
        tweetsRecyclerAdapter.setOnItemClickListener(new TweetsRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                Tweet tweet = tweets.get(position);
                switch(itemView.getId()){
                    case R.id.actionReply:
                        Intent intent = new Intent(TimelineActivity.this,ComposeActivity.class);
                        intent.putExtra(ComposeActivity.IS_REPLY,true);
                        intent.putExtra(ComposeActivity.BASE_TWEET_OBJECT,tweet);
                        startActivityForResult(intent,ComposeActivity.REPLY_TWEET_REQUEST_CODE);
                        Toast.makeText(getApplicationContext(), "Reply icon clicked",Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.actionFavorite:
                        Toast.makeText(getApplicationContext(), "Favorite action clicked",Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.actionRetweet:
                        Toast.makeText(getApplicationContext(), "Retweet action clicked",Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(getApplicationContext(), tweet.getBody(),Toast.LENGTH_SHORT).show();
                }

            }
        });
        rvTweets.setOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {

                populateTimeline(false,tweetsRecyclerAdapter.getMaxTweetId());
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                populateTimeline(true,1);
            }
        });
        fabCompose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*ComposeTweetDialogFragment composeTweetDialogFragment = ComposeTweetDialogFragment.newInstance();
                composeTweetDialogFragment.show(getSupportFragmentManager(),"Compose");*/
                Intent intent = new Intent(TimelineActivity.this, ComposeActivity.class);
                startActivityForResult(intent,ComposeActivity.COMPOSE_TWEET_REQUEST_CODE);
            }
        });
        client = TwitterApplication.getRestClient();
        populateTimeline(true,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == ComposeActivity.COMPOSE_TWEET_REQUEST_CODE){
            if(resultCode == Activity.RESULT_OK){
                if(data != null){
                    Tweet tweet = data.getParcelableExtra(ComposeActivity.TWEET_OBJECT);
                    tweetsRecyclerAdapter.addItemAtPosition(tweet,0);
                }
            }
        }else if(requestCode == ComposeActivity.REPLY_TWEET_REQUEST_CODE){

        }
    }

    private void populateTimeline(final boolean initial, long id) {
        client.getHomeTimeline(initial,id,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d(TAG,"Response returned is - "+response.toString());
                if(initial){
                    tweets.clear();
                }
                tweets.addAll(Tweet.fromJSONArray(response));
                tweetsRecyclerAdapter.notifyDataSetChanged();
                if(swipeRefreshLayout.isRefreshing()){
                    swipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e(TAG,"Error returned is - "+errorResponse.toString());
                if(swipeRefreshLayout.isRefreshing()){
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    public void postNewTweet(String status){
        client.postNewTweet(status,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Tweet tweet = Tweet.fromJSON(response);
                tweetsRecyclerAdapter.addItemAtPosition(tweet,0);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

}
