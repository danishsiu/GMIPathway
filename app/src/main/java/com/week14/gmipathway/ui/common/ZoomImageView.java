package com.week14.gmipathway.ui.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

/**
 * An image that can be zoomed with a pinch or a double tap, and moved around while zoomed.
 * At normal size it fits the screen, and swipes pass through to the photo pager.
 * While zoomed, swipes move the photo instead, so zoom out to change photos.
 */
public class ZoomImageView extends AppCompatImageView {

    private static final float MAX_ZOOM = 4f;
    private static final float DOUBLE_TAP_ZOOM = 2.5f;
    /** Treat anything this close to 1x as "not zoomed", to absorb rounding. */
    private static final float ZOOM_EPSILON = 0.01f;

    /** Fits the whole image on screen, centred. Zoom is measured from here. */
    private final Matrix baseMatrix = new Matrix();
    /** The zoom and movement added on top of the base. */
    private final Matrix zoomMatrix = new Matrix();
    private final Matrix drawMatrix = new Matrix();
    private final RectF bounds = new RectF();
    private final float[] values = new float[9];

    private final ScaleGestureDetector scaleDetector;
    private final GestureDetector gestureDetector;

    public ZoomImageView(@NonNull Context context) {
        this(context, null);
    }

    public ZoomImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setScaleType(ScaleType.MATRIX);
        scaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        gestureDetector = new GestureDetector(context, new GestureListener());
    }

    /** A new photo always starts at normal size. */
    @Override
    public void setImageDrawable(@Nullable Drawable drawable) {
        super.setImageDrawable(drawable);
        resetZoom();
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        resetZoom();
    }

    public void resetZoom() {
        zoomMatrix.reset();
        updateBaseMatrix();
        applyMatrix();
    }

    private void updateBaseMatrix() {
        Drawable drawable = getDrawable();
        baseMatrix.reset();
        if (drawable == null || getWidth() == 0 || getHeight() == 0) {
            return;
        }
        RectF image = new RectF(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        RectF view = new RectF(0, 0, getWidth(), getHeight());
        baseMatrix.setRectToRect(image, view, Matrix.ScaleToFit.CENTER);
    }

    private float currentZoom() {
        zoomMatrix.getValues(values);
        return values[Matrix.MSCALE_X];
    }

    private boolean isZoomed() {
        return currentZoom() > 1f + ZOOM_EPSILON;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleDetector.onTouchEvent(event);
        gestureDetector.onTouchEvent(event);
        // While zoomed (or mid-pinch), keep the pager from turning the page.
        boolean keepTouch = isZoomed() || scaleDetector.isInProgress();
        if (getParent() != null) {
            getParent().requestDisallowInterceptTouchEvent(keepTouch);
        }
        return true;
    }

    /** Zooms by a factor around a point, staying between 1x and the maximum. */
    private void zoomBy(float factor, float focusX, float focusY) {
        float target = Math.max(1f, Math.min(MAX_ZOOM, currentZoom() * factor));
        float applied = target / currentZoom();
        zoomMatrix.postScale(applied, applied, focusX, focusY);
        keepInside();
        applyMatrix();
    }

    /** Stops the photo being dragged away from the screen edges, and centres it when smaller. */
    private void keepInside() {
        Drawable drawable = getDrawable();
        if (drawable == null) {
            return;
        }
        drawMatrix.set(baseMatrix);
        drawMatrix.postConcat(zoomMatrix);
        bounds.set(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawMatrix.mapRect(bounds);
        zoomMatrix.postTranslate(correction(bounds.left, bounds.right, getWidth()),
                correction(bounds.top, bounds.bottom, getHeight()));
    }

    private static float correction(float start, float end, float viewSize) {
        float size = end - start;
        if (size <= viewSize) {
            return (viewSize - size) / 2f - start;
        }
        if (start > 0) {
            return -start;
        }
        if (end < viewSize) {
            return viewSize - end;
        }
        return 0;
    }

    private void applyMatrix() {
        drawMatrix.set(baseMatrix);
        drawMatrix.postConcat(zoomMatrix);
        setImageMatrix(drawMatrix);
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(@NonNull ScaleGestureDetector detector) {
            zoomBy(detector.getScaleFactor(), detector.getFocusX(), detector.getFocusY());
            return true;
        }
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(@NonNull MotionEvent event) {
            return true;
        }

        /** Double tap zooms in on that spot, or back out if already zoomed. */
        @Override
        public boolean onDoubleTap(@NonNull MotionEvent event) {
            if (isZoomed()) {
                zoomMatrix.reset();
                applyMatrix();
            } else {
                zoomBy(DOUBLE_TAP_ZOOM, event.getX(), event.getY());
            }
            return true;
        }

        @Override
        public boolean onScroll(@Nullable MotionEvent first, @NonNull MotionEvent current,
                                float distanceX, float distanceY) {
            if (!isZoomed()) {
                return false;
            }
            zoomMatrix.postTranslate(-distanceX, -distanceY);
            keepInside();
            applyMatrix();
            return true;
        }
    }
}
