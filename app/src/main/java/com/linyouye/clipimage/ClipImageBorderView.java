package com.linyouye.clipimage;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

/**
 * @author zhy http://blog.csdn.net/lmj623565791/article/details/39761281
 * 
 *         edited by lyy 20141016
 */
public class ClipImageBorderView extends View {

	private static final String TAG = "lyy-BorderView";
	private static final boolean D = true;

	private Rect mClipRect;

	private Paint mBorderPaint;
	private Paint mCornerPaint;
	private Paint mShadowPaint;

	private int mBorderWidth = 1;
	private int mCornerWidth = 5;
	private int mCornetLength = 30;

	private int mBorderColor = 0xFFFFFFFF;
	private int mShadowColor = 0xCC000000;
	private int mCornerColor = 0xFFFF0000;

	public ClipImageBorderView(Context context) {
		this(context, null);
	}

	public ClipImageBorderView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ClipImageBorderView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		initPaint();
	}

	private void initPaint() {
		mBorderPaint = new Paint();
		mBorderPaint.setAntiAlias(true);
		mBorderPaint.setColor(mBorderColor);
		mBorderPaint.setStyle(Style.FILL);

		mCornerPaint = new Paint();
		mCornerPaint.setAntiAlias(true);
		mCornerPaint.setColor(mCornerColor);
		mCornerPaint.setStyle(Style.FILL);

		mShadowPaint = new Paint();
		mShadowPaint.setAntiAlias(true);
		mShadowPaint.setColor(mShadowColor);
		mShadowPaint.setStyle(Style.FILL);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (mClipRect == null || mClipRect.left <= 0 || mClipRect.right <= 0
				|| mClipRect.top <= 0 || mClipRect.bottom <= 0) {
			return;
		}

		drawShadow(canvas);
		drawBorder(canvas);
		drawCorners(canvas);

	}

	private void drawBorder(Canvas canvas) {
		canvas.drawRect(mClipRect.left - mBorderWidth, mClipRect.top,
				mClipRect.left, mClipRect.bottom, mBorderPaint);
		canvas.drawRect(mClipRect.right, mClipRect.top, mClipRect.right
				+ mBorderWidth, mClipRect.bottom, mBorderPaint);
		canvas.drawRect(mClipRect.left - mBorderWidth, mClipRect.top
				- mBorderWidth, mClipRect.right + mBorderWidth, mClipRect.top,
				mBorderPaint);
		canvas.drawRect(mClipRect.left - mBorderWidth, mClipRect.bottom,
				mClipRect.right + mBorderWidth,
				mClipRect.bottom + mBorderWidth, mBorderPaint);
	}

	private void drawShadow(Canvas canvas) {
		canvas.drawRect(0, 0, mClipRect.left, canvas.getHeight(), mShadowPaint);
		canvas.drawRect(mClipRect.right, 0, canvas.getWidth(),
				canvas.getHeight(), mShadowPaint);
		canvas.drawRect(mClipRect.left, 0, mClipRect.right, mClipRect.top,
				mShadowPaint);
		canvas.drawRect(mClipRect.left, mClipRect.bottom, mClipRect.right,
				canvas.getHeight(), mShadowPaint);
	}

	private void drawCorners(Canvas canvas) {

		// Top left

		canvas.drawRect(mClipRect.left - mCornerWidth, mClipRect.top
				- mCornerWidth, mClipRect.left + mCornetLength, mClipRect.top,
				mCornerPaint);
		canvas.drawRect(mClipRect.left - mCornerWidth, mClipRect.top,
				mClipRect.left, mClipRect.top + mCornetLength, mCornerPaint);

		// Top right
		canvas.drawRect(mClipRect.right - mCornetLength, mClipRect.top
				- mCornerWidth, mClipRect.right + mCornerWidth, mClipRect.top,
				mCornerPaint);
		canvas.drawRect(mClipRect.right, mClipRect.top, mClipRect.right
				+ mCornerWidth, mClipRect.top + mCornetLength, mCornerPaint);

		// Bottom left
		canvas.drawRect(mClipRect.left - mCornerWidth, mClipRect.bottom,
				mClipRect.left + mCornetLength,
				mClipRect.bottom + mCornerWidth, mCornerPaint);
		canvas.drawRect(mClipRect.left - mCornerWidth, mClipRect.bottom
				- mCornetLength, mClipRect.left, mClipRect.bottom
				+ mBorderWidth, mCornerPaint);
		// Bottom right
		canvas.drawRect(mClipRect.right - mCornetLength, mClipRect.bottom,
				mClipRect.right + mCornerWidth,
				mClipRect.bottom + mCornerWidth, mCornerPaint);
		canvas.drawRect(mClipRect.right, mClipRect.bottom - mCornetLength,
				mClipRect.right + mCornerWidth,
				mClipRect.bottom + mCornerWidth, mCornerPaint);

	}

	public void setClipRect(Rect rect) {
		this.mClipRect = rect;
	}
}
