// Copyright (c) kotemaru.org  (APL/2.0)
package org.kotemaru.android.postit.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import org.kotemaru.android.postit.R;
import org.kotemaru.android.postit.layout.ToggleButtonViews;

public class ToggleButton extends LinearLayout {
	private String mLabel = null;
	private int mIcon = 0;
	private float mAlpha = 0.5F;
	private ToggleButtonViews mViews;

	public ToggleButton(Context context) {
		super(context);
		init(context, null);
	}

	public ToggleButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public ToggleButton(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context, attrs);
	}

	private void init(Context context, AttributeSet attrs) {
		if (attrs != null) {
			TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ToggleButton, 0, 0);
			try {
				mLabel = typedArray.getString(R.styleable.ToggleButton_label);
				mIcon = typedArray.getResourceId(R.styleable.ToggleButton_icon, 0);
				mAlpha = typedArray.getFloat(R.styleable.ToggleButton_alpha, 0.3F);
			} finally {
				typedArray.recycle();
			}
		}

		View layout = LayoutInflater.from(context).inflate(R.layout.toggle_button, this);
		mViews = new ToggleButtonViews(layout);
		mViews.mIcon.setImageResource(mIcon);
		if (mLabel == null) {
			mViews.mLabel.setVisibility(GONE);
		} else {
			mViews.mLabel.setText(mLabel);
		}
		this.setClickable(true);
		setSelected(false);
	}

	@Override
	public void setSelected(boolean selected) {
		super.setSelected(selected);
		setAlpha(selected ? 1.0F : mAlpha);
		invalidate();
	}

	@Override
	public boolean performClick() {
		super.performClick();
		setSelected(!isSelected());
		return true;
	}
}
