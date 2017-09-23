package com.example.android.cannongame;

import android.graphics.Point;

/**
 * Created by 92324 on 2017/9/7.
 */

public class JoyStick {

    private int mPivotX;
    private int mPivotY;
    private float mRotation;
    private int mWidth;
    private int mHeight;

    JoyStick(int pivotX,int pivotY,int width,int height,float rotation)
    {
        mPivotX = pivotX;
        mPivotY = pivotY;
        mWidth = width;
        mHeight = height;
        mRotation = rotation;
    }

    JoyStick(Point pivotPoint, int width, int height, float rotation)
    {
        this(pivotPoint.x,pivotPoint.y,width,height,rotation);
    }

    public int getPivotX() {
        return mPivotX;
    }

    public void setPivotX(int mPivotX) {
        this.mPivotX = mPivotX;
    }

    public int getPivotY() {
        return mPivotY;
    }

    public void setPivotY(int mPivotY) {
        this.mPivotY = mPivotY;
    }

    public float getRotation() {
        return mRotation;
    }

    public void setRotation(float mRotation) {
        this.mRotation = mRotation;
    }

    public int getWidth() {
        return mWidth;
    }

    public void setWidth(int mWidth) {
        this.mWidth = mWidth;
    }

    public int getHeight() {
        return mHeight;
    }

    public void setHeight(int mHeight) {
        this.mHeight = mHeight;
    }

    public void pointTo(float x, float y, float screenHeight)
    {
        int rotateDirection = 1; //顺时针为1，逆时针为-1
        if(y < screenHeight / 2)
            rotateDirection = -1;
        double tanValue = Math.abs(y - screenHeight / 2) / x;
        mRotation = (float)(Math.toDegrees(Math.atan(tanValue)) * rotateDirection);
    }
}
