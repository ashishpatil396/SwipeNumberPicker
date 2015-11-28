package com.vi.swipenumberpicker;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.TextView;

/**
 * Created by Vitalii.Ishchuk on 25-Jun-15
 */
public class SwipeNumberPicker extends TextView {

	private static final String TAG = "SwpNumPicker";

	private static final float GESTURE_STEP_DP = 5.0f;

	private OnValueChangeListener mOnValueChangeListener;

	private int mGestureStepPx;
	private float mStartX;
	private float mIntermediateX;
	private float mIntermediateValue;
	private int mPrimaryValue;
	private int mMinValue;
	private int mMaxValue;

	private int mArrowColor;
	private int mBackgroundColor;
	private int mActiveTextColor;

	private String mDialogTitle = "";

	private boolean mIsEnabled = true;
	private boolean mIsShowNumberPickerDialog = true;

	private AlertDialog numberPickerDialog;

	public SwipeNumberPicker(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public SwipeNumberPicker(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context, attrs);
	}

	private void init(Context context, AttributeSet attributeSet) {
		initAttributes(context, attributeSet);

		Drawable left = getDrawable(R.drawable.ic_arrow_left);
		Drawable right = getDrawable(R.drawable.ic_arrow_right);

		setCompoundDrawablesWithIntrinsicBounds(left, null, right, null);
		setBackgroundResource(R.drawable.bg_btn_default);

		float scale = getResources().getDisplayMetrics().density;
		mGestureStepPx = (int) (GESTURE_STEP_DP * scale + 0.5f);

		Paint textPaint = new Paint();
		textPaint.setTextSize(getTextSize());
		setMinWidth((int) (textPaint.measureText(Integer.toString(mMaxValue)) + left.getBounds().width() * 2));

		mIntermediateValue = mPrimaryValue;
		changeValue(mPrimaryValue);
		customizeTextView();
	}

	private void initAttributes(Context context, AttributeSet attributeSet) {
		TypedArray attrs = context.obtainStyledAttributes(attributeSet,
				R.styleable.SwipeNumberPicker, 0, 0);
		if (attrs != null) {
			try {
				mPrimaryValue = attrs.getInteger(R.styleable.SwipeNumberPicker_value, 0);
				mMinValue = attrs.getInteger(R.styleable.SwipeNumberPicker_min, -9999);
				mMaxValue = attrs.getInteger(R.styleable.SwipeNumberPicker_max, 9999);

				mArrowColor = attrs.getColor(R.styleable.SwipeNumberPicker_arrowColor, context.getResources().getColor(R.color.arrows));
				mBackgroundColor = attrs.getColor(R.styleable.SwipeNumberPicker_backgroundColor, context.getResources().getColor(R.color.background));
				mActiveTextColor = attrs.getColor(R.styleable.SwipeNumberPicker_numberColor, context.getResources().getColor(R.color.text));
			} finally {
				attrs.recycle();
			}
		}
	}

	private void customizeTextView() {
		int horizontalPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6, getContext().getResources().getDisplayMetrics());
		setPadding(0, horizontalPadding, 0, horizontalPadding);

		setGravity(Gravity.CENTER);
		setSingleLine(true);

		setTextColor(mActiveTextColor);
		setNormalBackground();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!mIsEnabled)
			return false;

		float currentX = event.getX();

		// if (currentY < - mGestureStepPx || currentY > getHeight() +
		// mGestureStepPx) {
		// // Touch is outside of the height.
		// return false;
		// }

		switch (event.getAction()) {

			case MotionEvent.ACTION_DOWN:
				mStartX = event.getX();
				mIntermediateX = mStartX;
				highlightBackground();
				return true;

			case MotionEvent.ACTION_MOVE:
				if (Math.abs(currentX - mStartX) > mGestureStepPx) {
					float distance = currentX - mIntermediateX;
					setPressed((int) distance);
					double swipedDistance = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, Math.abs(distance), getContext().getResources().getDisplayMetrics());
					float threshold;
					if (swipedDistance < 25) {
						return true;
					} else if (swipedDistance < 50) {
						threshold = 1;
					} else if (swipedDistance < 150) {
						threshold = 2;
					} else if (swipedDistance < 300) {
						threshold = 3;
					} else if (swipedDistance < 450) {
						threshold = 4;
					} else {
						threshold = 5;
					}
					mIntermediateValue += distance > 0 ? threshold : -threshold;
					changeValue((int) mIntermediateValue);
				}
				mIntermediateX = currentX;
				break;

			case MotionEvent.ACTION_CANCEL:

			case MotionEvent.ACTION_UP:
				setNormalBackground();

				if (Math.abs(currentX - mStartX) <= mGestureStepPx) {
					processClick();
				} else {
					notifyListener((int) mIntermediateValue);
				}
				return false;

			default:
				setNormalBackground();
				return false;
		}

		return true;
	}

	private void notifyListener(int newValue) {
		if (mOnValueChangeListener != null
				&& mOnValueChangeListener.onValueChange(this, mPrimaryValue, newValue)) {
			// Update primary value with new value.
			mPrimaryValue = newValue;
		} else {
			// Change value for primary value
			changeValue(mPrimaryValue);
		}
		mIntermediateValue = mPrimaryValue;
	}

	private void changeValue(int value) {
		if (value < mMinValue || value > mMaxValue) {
			// Value is greater or less the specified bounds, set the boundary value
			value = value < mMinValue ? mMinValue : mMaxValue;
			mIntermediateValue = value;
		}
		String result = Integer.toString(value);
		setText(result);
	}

	private void processClick() {
		if (mIsShowNumberPickerDialog)
			showNumberPickerDialog();
		else
			performClick();
	}


	public void showNumberPickerDialog() {
		if (numberPickerDialog != null && numberPickerDialog.isShowing())
			return;

		numberPickerDialog = getNumberPickerDialog();
		numberPickerDialog.show();
	}

	private AlertDialog getNumberPickerDialog() {
		final NumberPicker numberPicker = new NumberPicker(getContext());
		numberPicker.setLayoutParams(new NumberPicker.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT));

		numberPicker.setMaxValue(mMaxValue);
		numberPicker.setMinValue(0);
		numberPicker.setValue(mPrimaryValue);
		numberPicker.setWrapSelectorWheel(false);
		AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
		if (!mDialogTitle.equals(""))
			builder.setTitle(mDialogTitle);
		builder.setView(numberPicker).setPositiveButton(android.R.string.ok,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						int newValue = numberPicker.getValue();
						changeValue(newValue);
						notifyListener(newValue);
					}
				});

		return builder.create();
	}

	private void setPressed(int distance) {
		highlightArrows(distance);
		highlightBackground();
	}

	private void setNormalBackground() {
		customizeBackground(mBackgroundColor);
		customizeArrows(mArrowColor);
	}

	private void customizeBackground(int color) {
		setColorFilter(getBackground(), color);
	}

	private void customizeArrows(int color) {
		setColorFilter(getCompoundDrawables()[0], color);
		setColorFilter(getCompoundDrawables()[2], color);
	}

	private void highlightBackground() {
		setColorFilter(getBackground(), darkenColor(mBackgroundColor));
	}

	private void highlightArrows(int distance) {
		if (distance < 0) {
			// Highlight right arrow
			setColorFilter(getCompoundDrawables()[0], darkenColor(mArrowColor));
			setColorFilter(getCompoundDrawables()[2], mArrowColor);
		} else {
			// Highlight left arrow
			setColorFilter(getCompoundDrawables()[0], mArrowColor);
			setColorFilter(getCompoundDrawables()[2], darkenColor(mArrowColor));
		}
	}

	private void setColorFilter(Drawable drawable, int color) {
		drawable.mutate().setColorFilter(color, PorterDuff.Mode.SRC_IN);
	}


	private int darkenColor(int color) {
		return adjustColorBrightness(color, 0.9f);
	}

	private int lightenColor(int color) {
		return adjustColorBrightness(color, 1.1f);
	}

	private int adjustColorBrightness(int color, float factor) {
		float[] hsv = new float[3];
		Color.colorToHSV(color, hsv);
		hsv[2] = Math.min(hsv[2] * factor, 1f);
		return Color.HSVToColor(Color.alpha(color), hsv);
	}

	@Override
	public void setBackgroundColor(int color) {
		mBackgroundColor = color;
		customizeBackground(mBackgroundColor);

	}

	private void disable() {
		customizeArrows(lightenColor(mBackgroundColor));
		customizeBackground(lightenColor(mBackgroundColor));
		setTextColor(lightenColor(mActiveTextColor));
	}

	private void enable() {
		customizeArrows(mArrowColor);
		customizeBackground(mBackgroundColor);
		setTextColor(mActiveTextColor);
	}

	@Override
	public void setEnabled(boolean enabled) {
		this.mIsEnabled = enabled;
		if (enabled)
			enable();
		else
			disable();
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	private Drawable getDrawable(int resource) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			return getContext().getResources().getDrawable(resource, null);
		}
		return getContext().getResources().getDrawable(resource);
	}

	public void setArrowColor(int mArrowColor) {
		this.mArrowColor = mArrowColor;
		customizeArrows(mArrowColor);
	}

	public void setOnValueChangeListener(OnValueChangeListener valueChangeListener) {
		mOnValueChangeListener = valueChangeListener;
	}

	public int getValue() {
		return mPrimaryValue;
	}

	private void setValue(int value) {
		mPrimaryValue = value;
		mIntermediateValue = value;
	}

	public void setValue(int value, boolean isNotifyListener) {
		setValue(value);
		changeValue(value);
		if (isNotifyListener)
			notifyListener(value);
	}

	public void setMinValue(int minValue) {
		mMinValue = minValue;
	}

	public void setMaxValue(int maxValue) {
		mMaxValue = maxValue;
	}

	public void setTitle(String title) {
		mDialogTitle = title;
	}

	public void setShowNumberPickerDialog(boolean isShowNumberPickerDialog) {
		mIsShowNumberPickerDialog = isShowNumberPickerDialog;
	}
}
