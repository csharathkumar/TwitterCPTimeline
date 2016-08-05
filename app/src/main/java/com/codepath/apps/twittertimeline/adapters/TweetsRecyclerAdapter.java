package com.codepath.apps.twittertimeline.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.twittertimeline.R;
import com.codepath.apps.twittertimeline.models.Tweet;
import com.codepath.apps.twittertimeline.utils.UtilityMethods;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Sharath on 8/3/16.
 */
public class TweetsRecyclerAdapter extends RecyclerView.Adapter<TweetsRecyclerAdapter.TweetViewHolder> {
    private Context mContext;
    private List<Tweet> mTweets;

    public TweetsRecyclerAdapter(Context context, List<Tweet>tweets){
        mContext = context;
        mTweets = tweets;
    }

    private Context getContext() {
        return mContext;
    }

    @Override
    public TweetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View tweetView = inflater.inflate(R.layout.item_tweet,parent,false);
        TweetViewHolder tweetViewHolder = new TweetViewHolder(tweetView);
        return tweetViewHolder;
    }

    @Override
    public void onBindViewHolder(TweetViewHolder holder, int position) {
        Tweet tweet = mTweets.get(position);
        holder.tvUsername.setText(tweet.getUser().getName());
        holder.tvScreenName.setText("@"+tweet.getUser().getScreenName());
        holder.tvTimestamp.setText(UtilityMethods.getTimeDifference(tweet.getCreatedAt()));
        holder.tvBody.setText(tweet.getBody());
        holder.ivProfileImage.setImageResource(0);
        Picasso.with(mContext)
                .load(tweet.getUser().getProfileImageUrl())
                .into(holder.ivProfileImage);
    }
    public long getMaxTweetId(){
        if(mTweets != null && !mTweets.isEmpty()){
            Tweet tweet = mTweets.get(mTweets.size()-1);
            return tweet.getUid();
        }
        return 0;
    }
    @Override
    public int getItemCount() {
        return mTweets.size();
    }

    public static class TweetViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.ivProfile)
        ImageView ivProfileImage;
        @BindView(R.id.tvBody)
        TextView tvBody;
        @BindView(R.id.tvUserName)
        TextView tvUsername;
        @BindView(R.id.tvScreenName)
        TextView tvScreenName;
        @BindView(R.id.tvTimestamp)
        TextView tvTimestamp;

        public TweetViewHolder(final View itemView){
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
