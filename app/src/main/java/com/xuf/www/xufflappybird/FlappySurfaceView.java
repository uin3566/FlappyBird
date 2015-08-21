package com.xuf.www.xufflappybird;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.util.Random;

/**
 * Created by lenov0 on 2015/8/16.
 */
public class FlappySurfaceView extends SurfaceView implements Runnable, SurfaceHolder.Callback, View.OnTouchListener{

    private static final float mXSpeed = 10;
    private static final float mYDownSpeed = 6;

    private Context mContext;

    private enum GameStatus{
        WAITING,
        RUNNING,
        OVER
    }

    private GameStatus mGameStatus;

    private SurfaceHolder mSurfaceHolder;
    private Canvas mCanvas;
    private Thread mThread;
    private boolean mIsRunning;

    private Bitmap mDayBgBitmap;
    private Bitmap mNightBgBitmap;
    private Bitmap mLandBitmap;
    private Bitmap mBirdBitmap;
    private Bitmap mPipeDownBitmap;
    private Bitmap mPipeUpBitmap;

    private float mViewWidth;
    private float mViewHeight;
    private float mBirdY;
    private float mBirdWidth;
    private float mBirdHeight;
    private float mLandY;
    private float mLandX = 0;
    private float mMinPipeHeight;
    private float mMaxPipeHeight;
    private float mPipeGap;
    private float mPipeX1;
    private float mPipeX2;
    private float mPipeWidth;
    private float mUpPipeHeight1;
    private float mUpPipeHeight2;

    private float mBirdJump;
    private float mBirdDownDis;

    public FlappySurfaceView(Context context) {
        super(context);
        mContext = context;

        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);

        setZOrderOnTop(true);
        mSurfaceHolder.setFormat(PixelFormat.TRANSLUCENT);

        mGameStatus = GameStatus.WAITING;

        _initResources();
        setOnTouchListener(this);
    }

    private void _initResources(){
        mDayBgBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.bg_day);
        mNightBgBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.bg_night);
        mLandBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.land);
        mBirdBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.bird0_0);
        mPipeDownBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.pipe_down);
        mPipeUpBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.pipe_up);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mViewHeight = h;
        mViewWidth = w;

        mLandY = h / 5 * 4;

        mBirdWidth = mViewWidth / 10;
        mBirdHeight = mBirdWidth * 0.75f;
        mBirdY = mLandY / 2 - mBirdHeight;
        mBirdJump = mBirdHeight / 2.5f;

        mPipeGap = mLandY / 5;
        mMinPipeHeight = mPipeGap * 0.5f;
        mMaxPipeHeight = mPipeGap * 3.5f;
        mPipeWidth = mViewWidth / 7;
        mPipeX1 = mViewWidth;
        mPipeX2 = mPipeX1 + mViewWidth / 2 + mPipeWidth / 2;
        mUpPipeHeight1 = new Random().nextFloat() * (mMaxPipeHeight - mMinPipeHeight) + mMinPipeHeight;
        mUpPipeHeight2 = new Random().nextFloat() * (mMaxPipeHeight - mMinPipeHeight) + mMinPipeHeight;
    }

    private void _reset(){
        mBirdY = mLandY / 2 - mBirdHeight;
        mPipeX1 = mViewWidth;
        mPipeX2 = mPipeX1 + mViewWidth / 2 + mPipeWidth / 2;
        mUpPipeHeight1 = new Random().nextFloat() * (mMaxPipeHeight - mMinPipeHeight) + mMinPipeHeight;
        mUpPipeHeight2 = new Random().nextFloat() * (mMaxPipeHeight - mMinPipeHeight) + mMinPipeHeight;
        _draw();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mThread = new Thread(this);
        mThread.start();
        mIsRunning = true;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mIsRunning = false;
    }

    @Override
    public void run() {
        while (mIsRunning){
            long start = System.currentTimeMillis();
            _calc();
            _draw();
            long end = System.currentTimeMillis();
            if (end - start < 50){
                try {
                    mThread.sleep(50 - (end - start));
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if (mGameStatus == GameStatus.WAITING){
                    mGameStatus = GameStatus.RUNNING;
                    break;
                } else if (mGameStatus == GameStatus.RUNNING){
                    mBirdDownDis = -mBirdJump;
                    break;
                } else {
                    _reset();
                    mGameStatus = GameStatus.WAITING;
                }
        }

        return true;
    }

    private void _calc(){
        if (mGameStatus == GameStatus.RUNNING){
            mBirdDownDis += mYDownSpeed;
            mBirdY += mBirdDownDis;
            if (mBirdY < -mBirdHeight){
                mBirdY = -mBirdHeight;
            }
            if (mBirdY > mLandY - mBirdHeight){
                mGameStatus = GameStatus.OVER;
                mIsRunning = false;
                mBirdY = mLandY - mBirdHeight;
            }
        }
    }

    private void _draw(){
        mCanvas = mSurfaceHolder.lockCanvas();
        if (mCanvas != null){
            //draw
            _drawBackground();
            _drawBird();
            _drawPipe();
            _drawLand();
        }
        mSurfaceHolder.unlockCanvasAndPost(mCanvas);
    }

    private void _drawBackground(){
        RectF rectF = new RectF(0, 0, mViewWidth, mViewHeight);
        mCanvas.drawBitmap(mDayBgBitmap, null, rectF, null);
    }

    private void _drawLand(){
        mLandX -= mXSpeed;
        if (Math.abs(mLandX) >= mViewWidth){
            mLandX = 0;
        }
        RectF rectF = new RectF(mLandX, mLandY, mViewWidth + mLandX, mViewHeight);
        mCanvas.drawBitmap(mLandBitmap, null, rectF, null);
        rectF.set(mViewWidth + mLandX, mLandY, mViewWidth + mLandX + mViewWidth, mViewHeight);
        mCanvas.drawBitmap(mLandBitmap, null, rectF, null);
    }

    private void _drawBird(){
        float birdX = mViewWidth / 2 - mBirdWidth;
        RectF rectF = new RectF(birdX, mBirdY, birdX + mBirdWidth, mBirdY + mBirdHeight);
        mCanvas.drawBitmap(mBirdBitmap, null, rectF, null);
    }

    private void _drawPipe(){
        if (mPipeX1 > -mPipeWidth){
            _drawOnePipe(mPipeX1, mUpPipeHeight1);
        }
        if (mPipeX2 > -mPipeWidth && mPipeX2 < mViewWidth){
            _drawOnePipe(mPipeX2, mUpPipeHeight2);
        }

        if (mPipeX1 <= -mPipeWidth){
            mPipeX1 = mViewWidth;
            mUpPipeHeight1 = new Random().nextFloat() * (mMaxPipeHeight - mMinPipeHeight) + mMinPipeHeight;
        }
        if (mPipeX2 <= -mPipeWidth){
            mPipeX2 = mViewWidth;
            mUpPipeHeight2 = new Random().nextFloat() * (mMaxPipeHeight - mMinPipeHeight) + mMinPipeHeight;
        }

        mPipeX1 -= mXSpeed;
        mPipeX2 -= mXSpeed;
    }

    private void _drawOnePipe(float x, float height){
        RectF rectF = new RectF(x, 0, x + mPipeWidth, mLandY);
        mCanvas.save();
        mCanvas.translate(0, -(mLandY - height));
        mCanvas.drawBitmap(mPipeUpBitmap, null, rectF, null);
        mCanvas.translate(0, mLandY - height + height + mPipeGap);
        mCanvas.drawBitmap(mPipeDownBitmap, null, rectF, null);
        mCanvas.restore();
    }
}
