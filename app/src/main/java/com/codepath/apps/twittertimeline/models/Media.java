package com.codepath.apps.twittertimeline.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Sharath on 8/7/16.
 */
public class Media implements Parcelable {
    String type;
    String mediaUrlHttps;
    String mediaUrl;

    public String getType() {
        return type;
    }

    public String getMediaUrlHttps() {
        return mediaUrlHttps;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public static Media fromJSONObject(JSONObject jsonObject){
        Media media = new Media();
        try {
            media.type = jsonObject.getString("type");
            media.mediaUrl = jsonObject.getString("media_url");
            media.mediaUrlHttps = jsonObject.getString("media_url_https");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return media;
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.type);
        dest.writeString(this.mediaUrlHttps);
        dest.writeString(this.mediaUrl);
    }

    public Media() {
    }

    protected Media(Parcel in) {
        this.type = in.readString();
        this.mediaUrlHttps = in.readString();
        this.mediaUrl = in.readString();
    }

    public static final Parcelable.Creator<Media> CREATOR = new Parcelable.Creator<Media>() {
        @Override
        public Media createFromParcel(Parcel source) {
            return new Media(source);
        }

        @Override
        public Media[] newArray(int size) {
            return new Media[size];
        }
    };
}
