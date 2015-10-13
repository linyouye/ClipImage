package com.linyouye.clipimage;

import android.view.MotionEvent;

public class RotateGestureDetector {

	private static final String TAG = "lyy-RotateGestureDetector";
	private static final boolean D = true;

	float x = 0, y = 0, lastX1 = 0, lastY1 = 0, lastX2 = 0, lastY2 = 0;

	private OnRotateGestureListener mOnRotateGestureListener;

	private boolean isRotating = false;

	public RotateGestureDetector(OnRotateGestureListener onRotateGestureListener) {
		if (onRotateGestureListener != null) {
			mOnRotateGestureListener = onRotateGestureListener;
		} else {
			mOnRotateGestureListener = new SimpleOnRotateGestureListener();
		}
	}

	public void onTouchEvent(MotionEvent event) {

		if (event.getPointerCount() != 2) {
			if (isRotating) {
				mOnRotateGestureListener.onRatateEnd(x, y);
				isRotating = false;
			}
			return;
		}

		switch (event.getAction()) {
		case MotionEvent.ACTION_POINTER_2_DOWN:

			lastX1 = event.getX(0);
			lastY1 = event.getY(0);
			lastX2 = event.getX(1);
			lastY2 = event.getY(1);

			break;
		case MotionEvent.ACTION_MOVE:

			float x1 = event.getX(0);
			float y1 = event.getY(0);
			float x2 = event.getX(1);
			float y2 = event.getY(1);

			x = (x1 + x2) / 2;
			y = (y1 + y2) / 2;

			float rotation = transferRotation((y2 - y1) / (x2 - x1),
					(lastY2 - lastY1) / (lastX2 - lastX1));

			// float distance = transferDistance(x1, y1, x2, y2)
			// - transferDistance(lastX1, lastX1, lastX2, lastY2);

			if (Math.abs(rotation) < 10 && !isRotating) {
				return;
			}

			if (isRotating) {
				mOnRotateGestureListener.onRotate(rotation, x, y);

			} else {
				mOnRotateGestureListener.onRotateBegin();
				isRotating = true;
			}

			lastX1 = x1;
			lastY1 = y1;
			lastX2 = x2;
			lastY2 = y2;

			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			if (isRotating) {
				mOnRotateGestureListener.onRatateEnd(x, y);
				isRotating = false;
			}
			break;
		}

	}

	public interface OnRotateGestureListener {

		void onRotateBegin();

		void onRotate(float rotation, float x, float y);

		void onRatateEnd(float x, float y);
	}

	public static class SimpleOnRotateGestureListener implements
			OnRotateGestureListener {

		@Override
		public void onRotateBegin() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onRotate(float rotation, float x, float y) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onRatateEnd(float x, float y) {
			// TODO Auto-generated method stub

		}

	}

	private float transferRotation(float slope1, float slope2) {

		float rotation = (float) ((Math.atan(slope1) - Math.atan(slope2)) * 180 / 3.14);

		if (180 - Math.abs(rotation) < 20) {
			rotation = 0;
		}

		return rotation * 1.6f;
	}

}
