package com.example.flappysomething;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class FlappyBirdBackgroundView extends SurfaceView implements SurfaceHolder.Callback {
    private Paint paint;
    private BackgroundThread thread;

    public FlappyBirdBackgroundView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
        paint = new Paint();
        paint.setColor(Color.CYAN); // Set sky blue color for the background
        thread = new BackgroundThread(getHolder(), this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread.setRunning(true);
        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        thread.setRunning(false);
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (canvas != null) {
            // Draw sky
            canvas.drawColor(Color.CYAN);

            // Draw ground
            paint.setColor(Color.GREEN);
            canvas.drawRect(0, getHeight() - 200, getWidth(), getHeight(), paint);

            // Draw clouds or other background elements here
            // For example:
            paint.setColor(Color.WHITE);
            canvas.drawCircle(150, 100, 50, paint);
            canvas.drawCircle(200, 120, 40, paint);
            canvas.drawCircle(250, 90, 60, paint);
        }
    }

    private class BackgroundThread extends Thread {
        private SurfaceHolder surfaceHolder;
        private FlappyBirdBackgroundView backgroundView;
        private boolean running;

        public BackgroundThread(SurfaceHolder surfaceHolder, FlappyBirdBackgroundView backgroundView) {
            this.surfaceHolder = surfaceHolder;
            this.backgroundView = backgroundView;
        }

        public void setRunning(boolean running) {
            this.running = running;
        }

        @Override
        public void run() {
            Canvas canvas;
            while (running) {
                canvas = null;
                try {
                    canvas = surfaceHolder.lockCanvas();
                    synchronized (surfaceHolder) {
                        backgroundView.draw(canvas);
                    }
                } finally {
                    if (canvas != null) {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
            }
        }
    }
}