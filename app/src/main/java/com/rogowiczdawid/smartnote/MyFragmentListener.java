package com.rogowiczdawid.smartnote;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;

class MyFragmentListener {

    private final GestureDetector gestureDetector;

    MyFragmentListener(Context ctx) {
        gestureDetector = new GestureDetector(ctx, new GestureListener());
    }

    void onTouch(MotionEvent ev) {
        gestureDetector.onTouchEvent(ev);
    }

    public void fling(float v1) {
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            return super.onDoubleTap(e);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            fling(velocityY);
            return super.onFling(e1, e2, velocityX, velocityY);
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return super.onDown(e);
        }
    }
}
