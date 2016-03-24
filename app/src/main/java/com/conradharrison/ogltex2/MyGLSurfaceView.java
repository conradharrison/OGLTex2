package com.conradharrison.ogltex2;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

/**
 * A view container where OpenGL ES graphics can be drawn on screen.
 * This view can also be used to capture touch events, such as a user
 * interacting with drawn objects.
 */
public class MyGLSurfaceView extends GLSurfaceView {

    private final MyGLRenderer mRenderer;

    private GestureDetector mDetector;
    private ScaleGestureDetector mScaleGestureDetector;

    public MyGLSurfaceView(Context context) {
        super(context);

        // Create an OpenGL ES 2.0 context.
        setEGLContextClientVersion(2);

        // Set the Renderer for drawing on the GLSurfaceView
        mRenderer = new MyGLRenderer(context);
        setRenderer(mRenderer);

        // Render the view only when there is a change in the drawing data
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        mScaleGestureDetector = new ScaleGestureDetector(context, new ScaleListener());
        mDetector = new GestureDetector(context, new MyGestureListener());
    }

    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    private final float TOUCH_SCALE_FACTOR_LINEAR = 0.01f;
    private float mPreviousX;
    private float mPreviousY;

    @Override
    public boolean onTouchEvent(MotionEvent e) {

        boolean retVal = mScaleGestureDetector.onTouchEvent(e);
        retVal = mDetector.onTouchEvent(e) || retVal;

        float x = e.getX();
        float y = e.getY();

        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:

                float dx = x - mPreviousX;
                float dy = y - mPreviousY;
/*
                // reverse direction of rotation above the mid-line
                if (y > getHeight() / 2) {
                    dx = dx * -1 ;
                }

                // reverse direction of rotation to left of the mid-line
                if (x < getWidth() / 2) {
                    dy = dy * -1 ;
                }

                mRenderer.setAngle(
                        mRenderer.getAngle() +
                                ((dx + dy) * TOUCH_SCALE_FACTOR));  // = 180.0f / 320
*/
                mRenderer.setStrength(mRenderer.getStrength() + TOUCH_SCALE_FACTOR_LINEAR*dx);
                requestRender();
        }

        mPreviousX = x;
        mPreviousY = y;

        return true;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {

            mRenderer.setZoom(
                    Math.max(0.01f,
                            Math.min( mRenderer.getZoom() / detector.getScaleFactor(),
                            100.0f)));
            requestRender();

            return true;
        }
    }

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDoubleTap(MotionEvent event) {

            mRenderer.setZoom(1.0f);
            mRenderer.setStrength(3.0f);
            mRenderer.setAngle(0.0f);
            requestRender();

            return true;
        }
    }

}