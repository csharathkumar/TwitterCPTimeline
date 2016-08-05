package com.codepath.apps.twittertimeline.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Sharath on 8/2/16.
 */
public class User {
    String name;
    long uid;
    String screenName;
    String profileImageUrl;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public static User fromJSONObject(JSONObject jsonObject){
        User user = new User();
        try{
            user.name = jsonObject.getString("name");
            user.uid = jsonObject.getLong("id");
            user.screenName = jsonObject.getString("screen_name");
            user.profileImageUrl = jsonObject.getString("profile_image_url");
        }catch(JSONException e){
            e.printStackTrace();
        }
        return user;
    }
}
