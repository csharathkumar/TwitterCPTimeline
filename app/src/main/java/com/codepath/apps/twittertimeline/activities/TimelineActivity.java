package com.codepath.apps.twittertimeline.activities;

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

import com.codepath.apps.twittertimeline.R;
import com.codepath.apps.twittertimeline.TwitterApplication;
import com.codepath.apps.twittertimeline.TwitterClient;
import com.codepath.apps.twittertimeline.adapters.TweetArrayAdapter;
import com.codepath.apps.twittertimeline.adapters.TweetsRecyclerAdapter;
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
        client = TwitterApplication.getRestClient();
        populateTimeline(true,1);
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

}
