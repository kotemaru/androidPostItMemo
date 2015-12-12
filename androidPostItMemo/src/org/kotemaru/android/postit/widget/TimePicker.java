// Copyright (c) kotemaru.org  (APL/2.0)
package org.kotemaru.android.postit.widget;

import android.content.Context;
import android.text.InputFilter;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

import org.kotemaru.android.postit.data.TimerPattern;
import org.kotemaru.android.postit.util.Util;

import java.lang.reflect.Field;
import java.util.GregorianCalendar;

public class TimePicker extends LinearLayout {

	private NumberPicker mHourPicker;
	private NumberPicker mMinutePicker;
	private MinuteFormatter mMinuteFormatter;

	public TimePicker(Context context) {
		this(context, null);
	}

	public TimePicker(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public TimePicker(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context, attrs, defStyleAttr);
	}

	private void init(Context context, AttributeSet attrs, int defStyleAttr) {
		this.setOrientation(LinearLayout.HORIZONTAL);
		mMinuteFormatter = new MinuteFormatter();
		mHourPicker = DatePicker.createNumberPicker(this, 0, 23, 0, 50, new ZeroFormatter(), null);
		mMinutePicker = DatePicker.createNumberPicker(this, 0, 59 / 5, 5, 50, mMinuteFormatter, null);
	}
	public void assignTimerPattern(TimerPattern pattern) {
		pattern.setHour(mHourPicker.getValue());
		pattern.setMinute(mMinuteFormatter.toMinute(mMinutePicker.getValue()));
	}

	public String getValue() {
		return getValue(mHourPicker) + ":" + getValue(mMinutePicker);
	}

	private String getValue(NumberPicker picker) {
		return picker.getDisplayedValues()[picker.getValue()];
	}

	public void setValue(TimerPattern pattern) {
		mHourPicker.setValue(pattern.getHour());
		mMinutePicker.setValue(mMinuteFormatter.fromMinute(pattern.getMinute()));
	}

	private static class ZeroFormatter implements NumberPicker.Formatter {
		@Override
		public String format(int value) {
			// if (value == 0) return "**";
			if (value < 10) {
				return "0" + Integer.toString(value);
			} else {
				return Integer.toString(value);
			}
		}
	}

	private static class MinuteFormatter implements NumberPicker.Formatter {
		@Override
		public String format(int value) {
			// if (value == 0) return "**";
			value = value * 5;
			if (value < 10) {
				return "0" + Integer.toString(value);
			} else {
				return Integer.toString(value);
			}
		}

		public int fromMinute(int minute) {
			return minute / 5;
		}
		public int toMinute(int value) {
			return value * 5;
		}
	}
}
