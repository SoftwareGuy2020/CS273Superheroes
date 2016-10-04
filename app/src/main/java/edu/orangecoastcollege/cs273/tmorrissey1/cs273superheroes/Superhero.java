package edu.orangecoastcollege.cs273.tmorrissey1.cs273superheroes;

/**
 * Created by Travis on 10/4/2016.
 */

public class Superhero {
    private String mUsername;
    private String mName;
    private String mSuperpower;
    private String mOneThing;
    private String mImageName;

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String username) {
        mUsername = username;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getSuperpower() {
        return mSuperpower;
    }

    public void setSuperpower(String superpower) {
        mSuperpower = superpower;
    }

    public String getOneThing() {
        return mOneThing;
    }

    public void setOneThing(String oneThing) {
        mOneThing = oneThing;
    }

    public String getImageName() {
        return mImageName;
    }

    public void setImageName(String imageName) {
        mImageName = imageName;
    }
}
