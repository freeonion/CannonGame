package com.example.android.cannongame;

import android.graphics.Point;

/**
 * Created by 92324 on 2017/9/10.
 */

public class CannonBall {
    private float X;
    private float Y;
    private int radius;
    private int moveSpeed;
    private float flyDirection;
    private boolean isMoveDown = false;


    CannonBall(float X, float Y, int radius,int moveSpeed,float flyDirection)
    {
        this.X = X;
        this.Y = Y;
        this.radius = radius;
        this.moveSpeed = moveSpeed;
        this.flyDirection = flyDirection;
    }

    CannonBall(Point point, int radius, int moveSpeed, float flyDirection)
    {
        this(point.x,point.y,radius,moveSpeed,flyDirection);
    }

    public float getX() {
        return X;
    }

    public void setX(float x) {
        X = x;
    }

    public float getY() {
        return Y;
    }

    public void setY(float y) {
        Y = y;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public int getMoveSpeed() {
        return moveSpeed;
    }

    public void setMoveSpeed(int moveSpeed) {
        this.moveSpeed = moveSpeed;
    }

    public float getFlyDirection() {
        return flyDirection;
    }

    public void setFlyDirection(float flyDirection) {
        this.flyDirection = flyDirection;
    }

    public void updateBall()
    {
        if(!isMoveDown) {
            X += moveSpeed * Math.cos(Math.toRadians(flyDirection));
            Y += moveSpeed * Math.sin(Math.toRadians(flyDirection));
        }
        else
        {
            X -= moveSpeed * Math.cos(Math.toRadians(flyDirection));
            Y += moveSpeed * Math.sin(Math.toRadians(flyDirection));
        }
    }

    public void setIsMoveDown(boolean isMoveDown) {
        this.isMoveDown = isMoveDown;
    }
}
