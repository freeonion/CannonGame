package com.example.android.cannongame;


import android.graphics.Point;

/**
 * Created by 92324 on 2017/9/7.
 */

public class Block {
    private int X;
    private int Y;
    private int moveSpeed;           //每一帧移动多少像素
    private boolean moveDown;        //是否向下移动
    private int blockWidth;
    private int blockHeight;
    private int heightLimit1;
    private int heightLimit2;
    private int color;


    Block(int x,int y,int width,int height, int moveSpeed, int heightLimit1, int heightLimit2,boolean isMoveDown,int color)
    {
        this.moveSpeed = moveSpeed;
        this.X = x;
        this.Y = y;
        this.blockHeight = height;
        this.blockWidth = width;
        this.moveDown = isMoveDown;
        this.heightLimit1 = heightLimit1;
        this.heightLimit2 = heightLimit2;
        this.color = color;
    }

    Block(Point point,int width,int height,int moveSpeed,int heightLimit1, int heightLimit2, boolean isMoveDown,int color)
    {
        this(point.x - width / 2, point.y - height / 2,width,height,moveSpeed, heightLimit1, heightLimit2, isMoveDown,color);
    }


    public int getX() {
        return X;
    }

    public int getY() {
        return Y;
    }

    public int getMoveSpeed() {
        return moveSpeed;
    }

    public void setMoveSpeed(int moveSpeed) {
        this.moveSpeed = moveSpeed;
    }

    public int getBlockWidth() {
        return blockWidth;
    }

    public void setBlockWidth(int blockWidth) {
        this.blockWidth = blockWidth;
    }

    public int getBlockHeight() {
        return blockHeight;
    }

    public void setBlockHeight(int blockHeight) {
        this.blockHeight = blockHeight;
    }

    public void setX(int x) {
        X = x;
    }

    public void setY(int y) {
        Y = y;
    }

    public void updateModel()
    {
        int moveDirection = moveDown? 1: -1;
        int tempY = Y + moveDirection * moveSpeed;
        if(tempY <= heightLimit1)
        {
            Y = heightLimit1;
            moveDown = true;
        }else if(tempY >= heightLimit2)
        {
            Y = heightLimit2;
            moveDown = false;
        }
        else
        {
            Y = tempY;
        }
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getColor() {
        return color;
    }
}
