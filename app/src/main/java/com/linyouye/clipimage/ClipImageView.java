package com.linyouye.clipimage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;


/**
 * http://blog.csdn.net/lmj623565791/article/details/39761281
 *
 * @author zhy
 *         <p/>
 *         edited by lyy 20141016
 */
public class ClipImageView extends ImageView implements
        ViewTreeObserver.OnGlobalLayoutListener {

    private static final String TAG = "lyy-ClipImageView";
    private static final boolean D = true;

    private static final int AUTO_ANIM_INTERVAL = 20;

    private float minScale = 1.0f;
    public static float SCALE_MAX = 4.0f;
    private static float SCALE_MID = 2.0f;

    private boolean isScaling = false;
    private boolean isRotating = false;

    private ClipImageBorderView mBorderView;
    private Rect mClipRect;
    private float mRatio = 1.0f;
    private int mPadding = 50;

    private float mCurrentRotation = 0;
    private float mCurrentScale = 1;

    private boolean once = true;

    private final Matrix mMatrix = new Matrix();
    private ScaleGestureDetector mScaleGestureDetector;
    private RotateGestureDetector mRotateGestureDetector;
    private GestureDetector mGestureDetector;

    public ClipImageView(Context context) {
        this(context, null);
    }

    public ClipImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setScaleType(ScaleType.MATRIX);
        initGestureDetector(context);
        mClipRect = new Rect();
        mBorderView = new ClipImageBorderView(context);
        mBorderView.setClipRect(mClipRect);
    }

    private void initGestureDetector(Context context) {

        this.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                if (getDrawable() == null) {
                    return true;
                }

                if (mGestureDetector.onTouchEvent(event)) {
                    return true;
                }

                // if (mStatus == NONE || mStatus == SCALING) {
                // mScaleGestureDetector.onTouchEvent(event);
                // }
                // if (mStatus == NONE || mStatus == ROTATING) {
                // mRotateGestureDetector.onTouchEvent(event);
                // }
                // if (mStatus == NONE || mStatus == DRAGING) {
                // mDragGestureDetector.onTouchEvent(event);
                // }

                mScaleGestureDetector.onTouchEvent(event);
                mRotateGestureDetector.onTouchEvent(event);
                // mDragGestureDetector.onTouchEvent(event);

                return true;
            }
        });

        mGestureDetector = new GestureDetector(context,
                new SimpleOnGestureListener() {
                    @Override
                    public boolean onDoubleTap(MotionEvent e) {

                        // 双击，根据当前比例，缩小或放大

                        float x = e.getX();
                        float y = e.getY();
                        if (mCurrentScale < SCALE_MID) {
                            ClipImageView.this.postDelayed(
                                    new AutoScaleRunnable(SCALE_MID, x, y),
                                    AUTO_ANIM_INTERVAL);
                        } else {
                            ClipImageView.this
                                    .postDelayed(new AutoScaleRunnable(
                                                    minScale * 1.1f, x, y),
                                            AUTO_ANIM_INTERVAL);
                        }
                        return true;
                    }

                    @Override
                    public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                            float distanceX, float distanceY) {
                        // TODO Auto-generated method stub
                        if (D)
                            Log.i(TAG, "onScroll==>" + distanceX + ","
                                    + distanceY);
                        if (!isRotating && !isScaling) {
                            float dx = -distanceX;
                            float dy = -distanceY;
                            // translateImage(-distanceX, -distanceY);

                            RectF rectF = getMatrixRectF();
                            dx = Math.min(dx, mClipRect.left - rectF.left);
                            dx = Math.max(dx, mClipRect.right - rectF.right);
                            dy = Math.min(dy, mClipRect.top - rectF.top);
                            dy = Math.max(dy, mClipRect.bottom - rectF.bottom);
                            translateImage(dx, dy);
                            // checkBorder();

                        }
                        return super.onScroll(e1, e2, distanceX, distanceY);
                    }

                });
        mScaleGestureDetector = new ScaleGestureDetector(context,
                new OnScaleGestureListener() {

                    @Override
                    public boolean onScaleBegin(ScaleGestureDetector detector) {
                        // isDragingEnabled = false;
                        isScaling = true;
                        return true;
                    }

                    @Override
                    public void onScaleEnd(ScaleGestureDetector detector) {
                        if (!isRotating) {
                            checkBorder();
                        }

                    }

                    @Override
                    public boolean onScale(ScaleGestureDetector detector) {

                        if (getDrawable() == null) {
                            return true;
                        }

                        float scale = mCurrentScale;
                        float scaleFactor = detector.getScaleFactor();

                        if ((scale < SCALE_MAX && scaleFactor > 1.0f)
                                || (scale > minScale && scaleFactor < 1.0f)) {
                            if (scaleFactor * scale < minScale) {
                                scaleFactor = minScale / scale;
                            }
                            if (scaleFactor * scale > SCALE_MAX) {
                                scaleFactor = SCALE_MAX / scale;
                            }
                            scaleImage(scaleFactor, detector.getFocusX(),
                                    detector.getFocusY());
                        }
                        return true;

                    }

                });
        mRotateGestureDetector = new RotateGestureDetector(
                new RotateGestureDetector.OnRotateGestureListener() {

                    @Override
                    public void onRotate(float rotation, float x, float y) {
                        // TODO Auto-generated method stub
                        rotateImage(rotation, x, y);
                    }

                    @Override
                    public void onRatateEnd(float x, float y) {
                        // TODO Auto-generated method stub
                        postDelayed(new AutoRotateRunnable(
                                        getProximalRotation(mCurrentRotation), x, y),
                                AUTO_ANIM_INTERVAL);

                    }

                    @Override
                    public void onRotateBegin() {
                        // TODO Auto-generated method stub
                        isRotating = true;
                    }
                });

    }

    public void setPadding(int padding) {
        this.mPadding = padding;
        caculateClipRect();
    }

    public void setRatio(float ratio) {
        if (ratio > 0) {
            this.mRatio = ratio;
            caculateClipRect();
        }

    }

    private void caculateClipRect() {
        int horizontalPadding = 0;
        int verticalPadding = 0;

        if (getWidth() * 1.0f / getHeight() < mRatio) {
            horizontalPadding = mPadding;
            float frameWidth = getWidth() - 2 * horizontalPadding;
            float frameHeight = frameWidth / mRatio;
            verticalPadding = (int) (getHeight() - frameHeight) / 2;
        } else {
            verticalPadding = mPadding;
            float frameHeight = getHeight() - 2 * verticalPadding;
            float frameWidth = frameHeight * mRatio;
            horizontalPadding = (int) (getWidth() - frameWidth) / 2;
        }
        mClipRect.left = horizontalPadding;
        mClipRect.right = getWidth() - horizontalPadding;
        mClipRect.top = verticalPadding;
        mClipRect.bottom = getHeight() - verticalPadding;
    }

    private class AutoScaleRunnable implements Runnable {
        static final float SCALE_FACTOR = 1.1f;
        private float mTargetScale;
        private float tmpScale;

        private float x;
        private float y;

        public AutoScaleRunnable(float targetScale, float x, float y) {
            this.mTargetScale = targetScale;
            this.x = x;
            this.y = y;
            tmpScale = (mCurrentScale < mTargetScale ? SCALE_FACTOR
                    : 1 / SCALE_FACTOR);

        }

        @Override
        public void run() {

            if (((tmpScale > 1f) && (mCurrentScale < mTargetScale))
                    || ((tmpScale < 1f) && (mTargetScale < mCurrentScale))) {
                scaleImage(tmpScale, x, y);
                postDelayed(this, AUTO_ANIM_INTERVAL);
            } else {
                scaleImage(mTargetScale / mCurrentScale, x, y);
                checkBorder();
            }

        }
    }

    private class AutoRotateRunnable implements Runnable {

        private static final int ROTATION_ANGLE = 5;

        private float targetRotation;
        private float rotationTmp;

        private float x;
        private float y;

        public AutoRotateRunnable(float targetRotation, float x, float y) {

            this.x = x;
            this.y = y;

            this.targetRotation = targetRotation;
            if (mCurrentRotation <= 180) {
                if (targetRotation > mCurrentRotation
                        && targetRotation < mCurrentRotation + 180) {
                    rotationTmp = ROTATION_ANGLE;
                } else {
                    rotationTmp = -ROTATION_ANGLE;
                }
            } else {
                if (targetRotation < mCurrentRotation
                        && targetRotation > mCurrentRotation - 180) {
                    rotationTmp = -ROTATION_ANGLE;
                } else {
                    rotationTmp = ROTATION_ANGLE;
                }
            }

        }

        @Override
        public void run() {
            // TODO Auto-generated method stub

            if (Math.abs(targetRotation - mCurrentRotation) > ROTATION_ANGLE) {
                rotateImage(rotationTmp, x, y);
                postDelayed(this, AUTO_ANIM_INTERVAL);
            } else {
                rotateImage(targetRotation - mCurrentRotation, x, y);
                checkBorder();
            }

        }
    }

    private class AutoTranslateRunnable implements Runnable {

        private static final int TRANSLATE_TIMES = 5;

        private float dx;
        private float dy;

        private float dxTmp;
        private float dyTmp;

        public AutoTranslateRunnable(float dx, float dy) {

            this.dx = dx;
            this.dy = dy;

            dxTmp = dx / TRANSLATE_TIMES;
            dyTmp = dy / TRANSLATE_TIMES;

        }

        @Override
        public void run() {
            // TODO Auto-generated method stub

            if (Math.abs(dx) > 0.001 || Math.abs(dy) > 0.001) {
                translateImage(dxTmp, dyTmp);
                dx -= dxTmp;
                dy -= dyTmp;
                postDelayed(this, AUTO_ANIM_INTERVAL);
            } else {
                isScaling = false;
                isRotating = false;
            }

        }
    }

    private RectF getMatrixRectF() {
        Matrix matrix = mMatrix;
        RectF rect = new RectF();
        Drawable d = getDrawable();
        if (null != d) {
            rect.set(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            matrix.mapRect(rect);
        }
        return rect;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getViewTreeObserver().removeGlobalOnLayoutListener(this);
    }

    @Override
    public void onGlobalLayout() {
        if (once) {
            Drawable d = getDrawable();
            if (d == null)
                return;

            caculateClipRect();

            int dw = d.getIntrinsicWidth();
            int dh = d.getIntrinsicHeight();

            if (dw * 1.0f / dh > mRatio) {
                minScale = mClipRect.height() * 1.0f / dh;
            } else {
                minScale = mClipRect.width() * 1.0f / dw;
            }

            SCALE_MID = minScale * 2;
            SCALE_MAX = minScale * 4;
            mMatrix.postTranslate((getWidth() - dw) / 2, (getHeight() - dh) / 2);
            scaleImage(minScale, getWidth() / 2, getHeight() / 2);
            once = false;
        }

    }

    public Bitmap clip() {
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(),
                Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bitmap);
        draw(canvas);
        return Bitmap.createBitmap(bitmap, mClipRect.left + 1,
                mClipRect.top + 1, mClipRect.width() - 2,
                mClipRect.height() - 2);

    }

    private void checkBorder() {

        RectF rect = getMatrixRectF();
        float deltaX = 0;
        float deltaY = 0;

        float wRatio = mClipRect.width() / rect.width();
        float hRatio = mClipRect.height() / rect.height();
        float ratio = Math.max(wRatio, hRatio);
        if (ratio > 1f) {
            // scaleImage(ratio, width / 2, height / 2);
            rect = getMatrixRectF();
        }

        if (rect.left > mClipRect.left) {
            deltaX = mClipRect.left - rect.left;
        }
        if (rect.right < mClipRect.right) {
            deltaX = mClipRect.right - rect.right;
        }
        if (rect.top > mClipRect.top) {
            deltaY = mClipRect.top - rect.top;
        }
        if (rect.bottom < mClipRect.bottom) {
            deltaY = mClipRect.bottom - rect.bottom;
        }
        // translateImage(deltaX, deltaY);
        postDelayed(new AutoTranslateRunnable(deltaX, deltaY),
                AUTO_ANIM_INTERVAL);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        caculateClipRect();
        super.onDraw(canvas);
        mBorderView.draw(canvas);

    }

    private void scaleImage(float scaleFactor, float x, float y) {
        mMatrix.postScale(scaleFactor, scaleFactor, x, y);
        mCurrentScale *= scaleFactor;
        setImageMatrix(mMatrix);
    }

    private void rotateImage(float rotation, float x, float y) {
        mMatrix.postRotate(rotation, getWidth() / 2, getHeight() / 2);
        mCurrentRotation += rotation;
        setImageMatrix(mMatrix);
    }

    private void translateImage(float dx, float dy) {
        mMatrix.postTranslate(dx, dy);
        setImageMatrix(mMatrix);
    }

    /**
     * 根据当前角度得到0，90，180，270中最接近的角度
     *
     * @param currentRotation
     * @return
     */
    private float getProximalRotation(float currentRotation) {

        // 将当前角度转换为0~360度
        float rotationTmp = currentRotation;
        while (rotationTmp < 0) {
            rotationTmp += 360;
        }
        rotationTmp %= 360;

        float targetTmp = 0;

        switch ((int) rotationTmp / 45) {
            case 0:
                targetTmp = 0;
                break;
            case 1:
            case 2:
                targetTmp = 90;
                break;
            case 3:
            case 4:
                targetTmp = 180;
                break;
            case 5:
            case 6:
                targetTmp = 270;
                break;
            default:
                targetTmp = 360;
                break;
        }

        return currentRotation + targetTmp - rotationTmp;

    }

}
