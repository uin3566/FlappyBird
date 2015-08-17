package com.xuf.www.xufflappybird;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Random;

/**
 * Created by lenov0 on 2015/8/16.
 */
public class FlappySurfaceView extends SurfaceView implements Runnable, SurfaceHolder.Callback{

    private static final float mXSpeed = 10;

    private Context mContext;

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
    private float mLandY;
    private float mLandX = 0;
    private float mMinPipeHeight;
    private float mMaxPipeHeight;
    private float mPipeGap;
    private float mPipeX;
    private float mPipeWidth;

    public FlappySurfaceView(Context context) {
        super(context);
        mContext = context;

        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);

        setZOrderOnTop(true);
        mSurfaceHolder.setFormat(PixelFormat.TRANSLUCENT);

        _initResources();
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

        mPipeGap = mLandY / 7;
        mMinPipeHeight = mPipeGap * 1;
        mMaxPipeHeight = mPipeGap * 5;

        mPipeX = mViewWidth;
        mPipeWidth = mViewWidth / 7;

        height = new Random().nextFloat() * (mMaxPipeHeight - mMinPipeHeight) + mMinPipeHeight;
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
        float birdSize = mViewWidth / 10;
        float birdX = mViewWidth / 2 - birdSize;
        float birdY = mViewHeight / 2 - birdSize;
        RectF rectF = new RectF(birdX, birdY, birdX + birdSize, birdY + birdSize);
        mCanvas.drawBitmap(mBirdBitmap, null, rectF, null);
    }

    float height;
    private void _drawPipe(){
        RectF rectF = new RectF(mViewWidth / 2, 0, mViewWidth / 2 + mPipeWidth, mLandY);
        mCanvas.save();
        mCanvas.translate(0, -(mLandY - height));
        mCanvas.drawBitmap(mPipeUpBitmap, null, rectF, null);
        mCanvas.translate(0, mLandY - height + height + mPipeGap);
        mCanvas.drawBitmap(mPipeDownBitmap, null, rectF, null);
        mCanvas.restore();
    }

}
