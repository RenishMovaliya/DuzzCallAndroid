package com.logycraft.duzzcalll.helper.compatibility;

import android.content.Context;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

/* loaded from: classes2.dex */
public class CompatibilityScaleGestureDetector extends ScaleGestureDetector.SimpleOnScaleGestureListener {
    private ScaleGestureDetector detector;
    private CompatibilityScaleGestureListener listener;

    public CompatibilityScaleGestureDetector(Context context) {
        this.detector = new ScaleGestureDetector(context, this);
    }

    public void setOnScaleListener(CompatibilityScaleGestureListener newListener) {
        this.listener = newListener;
    }

    public void onTouchEvent(MotionEvent event) {
        this.detector.onTouchEvent(event);
    }

    @Override // android.view.ScaleGestureDetector.SimpleOnScaleGestureListener, android.view.ScaleGestureDetector.OnScaleGestureListener
    public boolean onScale(ScaleGestureDetector detector) {
        CompatibilityScaleGestureListener compatibilityScaleGestureListener = this.listener;
        if (compatibilityScaleGestureListener == null) {
            return false;
        }
        return compatibilityScaleGestureListener.onScale(this);
    }

    public float getScaleFactor() {
        return this.detector.getScaleFactor();
    }

    public void destroy() {
        this.listener = null;
        this.detector = null;
    }
}
