package com.example.android.cannongame;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Random;

/**
 * Created by 92324 on 2017/9/7.
 */

public class DrawView extends SurfaceView  implements SurfaceHolder.Callback{

    private SurfaceHolder holder;
    private RenderThread renderThread;
    private ArrayList<Block> targetList;
    private Block mBlock;
    private JoyStick mJoyStick;
    private CannonBall mBall;
    private final int screenWidth;
    private final int screenHeight;
    private final static int blockWidth = 40;
    private final static int blockHeight = 250;
    private final static int targetSize = 9;
    private final static int targetWidth = 40;
    private final static int targetHeight = 200;
    private final static int joystickWidth = 200;
    private final static int joystickHeight = 60;
    private final static int framePerSec = 120;
    private final static int blockSpeed = 5;
    private final static int targetSpeedMin = 1;
    private final static int targetSpeedMax = 10;
    private final static int ballSpeed = 20;
    private final static int ballRadius = 30;
//  private boolean newBallCreated = false;
    private SoundPool mSoundPool;
    private static final int MAX_SOUNDS = 3;
    private int targetHitID;
    private int blockHitID;
    private int cannonFileID;
    private long timePast; // in milliseconds
    private long timeInit;
    private float timeLeft = 10.0f;
    private int fireCount = 0;
    private boolean isDialogShow = false;
    private boolean isDrawing = false;

    private Paint paint;
    private Paint targetPaint;
    private Paint textPaint;
    private AlertDialog mDialog;

    public DrawView(Context context) {
        super(context);
        holder = getHolder();
        holder.addCallback(this);
        screenHeight = getScreenHeight();
        screenWidth = getScreenWidth();

        initPaint();
//      init();
        //这一句会将Canvas所画的内容全部覆盖
 //       setBackgroundColor(Color.WHITE);
        AudioAttributes audioAttributes = new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).setUsage(AudioAttributes.USAGE_GAME).build();
        mSoundPool = new SoundPool.Builder().setAudioAttributes(audioAttributes).setMaxStreams(MAX_SOUNDS).build();
        blockHitID = mSoundPool.load(getContext(),R.raw.blocker_hit,1);
        cannonFileID = mSoundPool.load(getContext(),R.raw.cannon_fire,1);
        targetHitID = mSoundPool.load(getContext(),R.raw.target_hit,1);

    }

    private void init()
    {
        targetList = new ArrayList<>();
        timePast = new GregorianCalendar().getTimeInMillis();
        timeInit = timePast;
        isDialogShow = false;
        timeLeft = 10.0f;
        fireCount = 0;

        mJoyStick = new JoyStick(0,screenHeight / 2,joystickWidth,joystickHeight,0);
        mBlock = new Block(screenWidth / 2 - blockWidth / 2,screenHeight / 2 - blockHeight / 2,blockWidth,blockHeight,blockSpeed,0,screenHeight - blockHeight,true,Color.BLACK);
        int targetGap = 100;
        Random randomGenerator = new Random();
        for(int i = 0; i < targetSize; i++)
        {
            Point p = new Point(screenWidth / 2 + (i + 1)  * targetGap, screenHeight / 2);
            int blockSpeed = randomGenerator.nextInt(targetSpeedMax);
            int blockDirection = randomGenerator.nextInt(2);
            int color = Color.GREEN;
            if(i % 2 == 0)
                color = Color.BLUE;
            Block block = new Block(p,targetWidth,targetHeight,blockSpeed + targetSpeedMin,0,screenHeight - targetHeight,blockDirection == 0 ? true:false,color);
            targetList.add(block);
        }
    }


    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        isDrawing = true;
        //如果这一句放在构造函数中时，当按返回键后，这个线程就变成terminated状态，再返回调用start函数，会出现异常Thread already started（线程运行完成或其它原因终止），因此这里重新建一个线程对象，就不会出现这个异常
        renderThread = new RenderThread();
        init();
        renderThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        isDrawing = false;
        mSoundPool.release();
        //目标是为了按了home键后，再返回，对话框会消失，这种做法就是当按了home键后，对话框就消失。
        if(mDialog != null)
            mDialog.dismiss();
    }

    private  class RenderThread extends Thread{
        @Override
        public void run() {
            while(isDrawing)
            {
                updateModel();
                drawUi();
            }
        }
    }

    private void updateModel()
    {

        try{
            Thread.sleep(1000 / framePerSec);
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        long newTime = new GregorianCalendar().getTimeInMillis();
        if(newTime - timePast > 100 && timeLeft > 0 && !(targetList.isEmpty()))
        {
            timeLeft = timeLeft - (newTime - timePast)/1000.0f;
            if(timeLeft < 0)
                timeLeft = 0;
            timePast = newTime;
        }
        if((timeLeft == 0 || targetList.isEmpty()) && !isDialogShow)
        {
            isDialogShow = true;
            showTheDialog();
        }
        mBlock.updateModel();
        if(mBall != null)
        {
            mBall.updateBall();
        }
        for(Block block : targetList)
        {
            block.updateModel();
        }
        collideDetect();
    }

    private void drawUi()
    {
        Canvas canvas = holder.lockCanvas();
        try{
            drawCanvas(canvas);
        }catch(Exception e)
        {
            e.printStackTrace();
        }finally {
            //这个判断的目的是避免出现 Surface has already been released 的异常.
            if(canvas != null)
                holder.unlockCanvasAndPost(canvas);
        }
    }

    private void drawCanvas(Canvas canvas)
    {

        canvas.drawColor(Color.WHITE);
        canvas.drawCircle(0, screenHeight / 2, mJoyStick.getWidth() / 2, paint);
        canvas.drawRect(mBlock.getX(),mBlock.getY(),mBlock.getX() + mBlock.getBlockWidth(),mBlock.getY() + mBlock.getBlockHeight(), paint);

        canvas.drawText(getResources().getString(R.string.view_time_left,timeLeft),100,100,textPaint);
        for(int i = 0; i < targetList.size(); i++)
        {
            targetPaint.setColor(targetList.get(i).getColor());
            canvas.drawRect(targetList.get(i).getX(),targetList.get(i).getY(),targetList.get(i).getX() + targetList.get(i).getBlockWidth(),targetList.get(i).getY() + targetList.get(i).getBlockHeight(), targetPaint);
        }
        if(mBall != null) {
            if(mBall.getX() > screenWidth || mBall.getY() > screenHeight || mBall.getY() < 0 || mBall.getX() < 0)
            {
                mBall = null;
            }
            else
            {
                canvas.drawCircle(mBall.getX(), mBall.getY(), mBall.getRadius(), paint);
            }
        }
        canvas.save();
        canvas.rotate(mJoyStick.getRotation(),0,screenHeight / 2);
        canvas.drawRect(0,mJoyStick.getPivotY() - mJoyStick.getHeight() / 2,mJoyStick.getWidth(),mJoyStick.getPivotY() + mJoyStick.getHeight() / 2, paint);
//        if(newBallCreated)
//        {
//            mBall = new CannonBall(mJoyStick.getHeight(),mJoyStick.getPivotY(),ballRadius,ballSpeed);
//            newBallCreated = false;
//        }

        canvas.restore();



    }

    private int getScreenHeight()
    {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager()
                .getDefaultDisplay()
                .getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    private int getScreenWidth()
    {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager()
                .getDefaultDisplay()
                .getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    private void initPaint()
    {
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);

        targetPaint = new Paint();
        targetPaint.setStyle(Paint.Style.FILL);

        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextSize(50.0f);
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked())
        {
            case MotionEvent.ACTION_DOWN:
                mJoyStick.pointTo(event.getX(),event.getY(),screenHeight);
//              newBallCreated = true;
                newBall();
                break;
        }
        return true;
    }

    private void newBall()
    {
        if(mBall == null)
        {
            double x = mJoyStick.getWidth() * Math.cos(Math.toRadians(mJoyStick.getRotation()));
            double y = mJoyStick.getWidth() * Math.sin(Math.toRadians(mJoyStick.getRotation())) + screenHeight / 2;
            mBall = new CannonBall((float)x,(float)y,ballRadius,ballSpeed,mJoyStick.getRotation());
            fireCount++;
            mSoundPool.play(cannonFileID,0.5f,0.5f,0,0,1.0f);
        }
    }

    private void collideDetect()
    {
        if(mBall != null)
        {
            Rect rectBall = new Rect((int)(mBall.getX()-mBall.getRadius()),(int)(mBall.getY()-mBall.getRadius()),(int)(mBall.getX()+mBall.getRadius()),(int)(mBall.getY()+mBall.getRadius()));
            if(Rect.intersects(new Rect(mBlock.getX(),mBlock.getY(),mBlock.getX()+blockWidth,mBlock.getY()+blockHeight),rectBall))
            {
                mBall.setIsMoveDown(true);
                mSoundPool.play(blockHitID,0.5f,0.5f,0,0,1.0f);
                if(timeLeft >= 2)
                    timeLeft -= 2;
                return;
            }
            for(int i = 0; i < targetList.size(); i++)
            {
                if(Rect.intersects(new Rect(targetList.get(i).getX(),targetList.get(i).getY(),targetList.get(i).getX()+targetWidth,targetList.get(i).getY()+targetHeight),rectBall))
                {
                    targetList.remove(targetList.get(i));
                    mBall = null;
                    mSoundPool.play(targetHitID,0.5f,0.5f,0,0,1.0f);
                    if(timeLeft > 0)
                        timeLeft += 3;
                    return;
                }
            }
        }
    }

    private void showTheDialog()
    {
        Activity activity = (Activity) getContext();
        activity.runOnUiThread(new Runnable()
        {
           public void run()
           {
                float timeUsed = (new GregorianCalendar().getTimeInMillis() - timeInit) / 1000.0f;
                String title = getResources().getString(R.string.title_dialog_lose);
                if(targetList.size() == 0)
                    title = getResources().getString(R.string.title_dialog_win);
               mDialog =  new AlertDialog.Builder(getContext()).setTitle(title).setMessage(getResources().getString(R.string.content_dialog,fireCount,timeUsed)).setPositiveButton(R.string.dialog_set,null).setOnDismissListener(new DialogInterface.OnDismissListener() {
                   @Override
                   public void onDismiss(DialogInterface dialogInterface) {
                       init();
                   }
               }).show();
           }
        });
    }
}
