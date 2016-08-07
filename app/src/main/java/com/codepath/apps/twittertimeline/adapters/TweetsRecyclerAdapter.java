package com.codepath.apps.twittertimeline.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.codepath.apps.twittertimeline.R;
import com.codepath.apps.twittertimeline.models.Media;
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
    // Define listener member variable
    private static OnItemClickListener listener;
    // Define the listener interface
    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }
    // Define the method that allows the parent activity or fragment to define the listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
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
        if(tweet.isFavorited()){
            holder.ivFavorite.setImageResource(R.drawable.ic_favorited);
        }else{
            holder.ivFavorite.setImageResource(R.drawable.ic_favorite_twitter);
        }
        Media media = tweet.getMedia();
        if(media != null){
            holder.mediaLayout.setVisibility(View.VISIBLE);
            if(media.getType().equals("")){
                holder.ivImage.setVisibility(View.VISIBLE);
                holder.videoView.setVisibility(View.GONE);
                holder.ivImage.setImageResource(0);
                Picasso.with(mContext)
                        .load(media.getMediaUrlHttps()+":small")
                        .fit().centerCrop()
                        .into(holder.ivImage);
            }else{
                holder.ivImage.setVisibility(View.GONE);
                holder.videoView.setVisibility(View.VISIBLE);
            }

        }else{
            holder.mediaLayout.setVisibility(View.GONE);
        }
    }
    public long getMaxTweetId(){
        if(mTweets != null && !mTweets.isEmpty()){
            Tweet tweet = mTweets.get(mTweets.size()-1);
            return tweet.getUid();
        }
        return 0;
    }

    public void addItemAtPosition(Tweet tweet, int position){
        mTweets.add(position,tweet);
        notifyItemInserted(position);
    }

    public void replaceItemAtPosition(Tweet tweet, int position){
        mTweets.set(position,tweet);
        notifyItemChanged(position);
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
        @BindView(R.id.mediaLayout)
        LinearLayout mediaLayout;
        @BindView(R.id.ivImage)
        ImageView ivImage;
        @BindView(R.id.videoView)
        VideoView videoView;
        @BindView(R.id.actionFavorite)
        ImageView ivFavorite;
        @BindView(R.id.actionRetweet)
        ImageView ivRetweet;
        @BindView(R.id.actionReply)
        ImageView ivReply;

        public TweetViewHolder(final View itemView){
            super(itemView);
            ButterKnife.bind(this,itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(itemView,getLayoutPosition());
                }
            });
            ivReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(v,getLayoutPosition());
                }
            });
            ivFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(v,getLayoutPosition());
                }
            });
            ivRetweet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(v,getLayoutPosition());
                }
            });
        }
    }
}
