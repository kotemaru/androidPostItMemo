// Copyright (c) kotemaru.org  (APL/2.0)
package org.kotemaru.android.postit.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

import org.kotemaru.android.postit.data.TimerPattern;
import org.kotemaru.android.postit.util.Util;

import java.util.GregorianCalendar;

public class DatePicker extends LinearLayout {

	private NumberPicker mYearPicker;
	private NumberPicker mMonthPicker;
	private NumberPicker mDatePicker;
	private NumberPicker mDayOfWeekPicker;
	private NumberPicker mDayOfWeekInMonthPicker;

	public DatePicker(Context context) {
		this(context, null);
	}

	public DatePicker(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public DatePicker(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context, attrs, defStyleAttr);
	}

	private void init(Context context, AttributeSet attrs, int defStyleAttr) {
		this.setOrientation(LinearLayout.HORIZONTAL);
		int margin = 0;

		char[] order = android.text.format.DateFormat.getDateFormatOrder(context);
		if (order[0] != 'y') {
			mDayOfWeekPicker = createNumberPicker(this, 0, 7, margin, 50, new DayOfWeekFormatter(), new DayOfWeekListener());
			mDayOfWeekInMonthPicker = createNumberPicker(this, 0, 5, 0, 40, new DayOfWeekInMouthFormatter(), null);
			margin = 20;
		}

		for (char ch : order) {
			switch (ch) {
			case 'y':
				GregorianCalendar cal = new GregorianCalendar();
				final int currentYear = cal.get(GregorianCalendar.YEAR);
				mYearPicker = createNumberPicker(this, currentYear - 1, currentYear + 10, margin, 50,
						new NumberPicker.Formatter() {
							@Override
							public String format(int value) {
								if (value < currentYear) return "****";
								return Integer.toString(value);
							}
						}, null);
				break;
			case 'M':
				mMonthPicker = createNumberPicker(this, 0, 12, margin, 35, new ZeroFormatter(), new MonthListener());
				break;
			case 'd':
				mDatePicker = createNumberPicker(this, 0, 31, margin, 35, new ZeroFormatter(), new DateListener());
				break;
			}
			margin = 5;
		}
		if (order[0] == 'y') {
			mDayOfWeekPicker = createNumberPicker(this, 0, 7, 20, 50, new DayOfWeekFormatter(), new DayOfWeekListener());
			mDayOfWeekInMonthPicker = createNumberPicker(this, 0, 5, 0, 40, new DayOfWeekInMouthFormatter(), null);
		}
	}

	public void assignTimerPattern(TimerPattern pattern) {
		GregorianCalendar cal = new GregorianCalendar();
		final int currentYear = cal.get(GregorianCalendar.YEAR);
		int yyyy = mYearPicker.getValue();
		pattern.setYear(yyyy < currentYear ? 0 : yyyy);
		pattern.setMonth(mMonthPicker.getValue());
		pattern.setDate(mDatePicker.getValue());
		pattern.setDayOfWeek(mDayOfWeekPicker.getValue());
		pattern.setDayOfWeekInMonth(mDayOfWeekInMonthPicker.getValue());
	}

	public void setValue(TimerPattern pattern) {
		mYearPicker.setValue(pattern.hasYear() ? pattern.getYear() : mYearPicker.getMinValue());
		mMonthPicker.setValue(pattern.getMonth());
		mDatePicker.setValue(pattern.getDate());
		mDayOfWeekPicker.setValue(pattern.getDayOfWeek());
		mDayOfWeekInMonthPicker.setValue(pattern.getDayOfWeekInMonth());
	}

	private class MonthListener implements NumberPicker.OnValueChangeListener {
		@Override
		public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
			if (newVal == 2) {
				mDatePicker.setMaxValue(29);
			} else if (newVal == 4 || newVal == 6 || newVal == 9 || newVal == 11) {
				mDatePicker.setMaxValue(30);
			} else {
				mDatePicker.setMaxValue(31);
			}
		}
	}

	private class DateListener implements NumberPicker.OnValueChangeListener {
		@Override
		public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
			if (newVal != 0) {
				mDayOfWeekPicker.setValue(0);
			} else {

			}
		}
	}

	private class DayOfWeekListener implements NumberPicker.OnValueChangeListener {
		@Override
		public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
			if (newVal != 0) {
				mDatePicker.setValue(0);
				mMonthPicker.setValue(0);
				mYearPicker.setValue(mYearPicker.getMinValue());
			} else {
				mDayOfWeekInMonthPicker.setValue(0);
			}
		}
	}

	private static class ZeroFormatter implements NumberPicker.Formatter {
		@Override
		public String format(int value) {
			if (value == 0) return "**";
			return Integer.toString(value);
		}
	}

	private static class DayOfWeekFormatter implements NumberPicker.Formatter {
		@Override
		public String format(int value) {
			return TimerPattern.DAY_OF_WEEKS[value];
		}
	}

	private static class DayOfWeekInMouthFormatter implements NumberPicker.Formatter {
		@Override
		public String format(int value) {
			if (value == 0) return " ";
			return TimerPattern.DAY_OF_WEEKS_IN_MONTH[value];
		}
	}

	public static NumberPicker createNumberPicker(ViewGroup parent,
			int min, int max,
			int margin, int width,
			NumberPicker.Formatter formatter,
			NumberPicker.OnValueChangeListener listener) {
		Context context = parent.getContext();
		NumberPicker picker = new NumberPicker(context);
		picker.setFormatter(formatter);
		picker.setMinValue(min);
		picker.setMaxValue(max);
		picker.setValue(picker.getMinValue());
		picker.setOnValueChangedListener(listener);

		String[] values = new String[max - min + 1];
		for (int i = min; i <= max; i++) {
			values[i - min] = formatter.format(i);
		}
		picker.setDisplayedValues(values);

		parent.addView(picker);
		LinearLayout.LayoutParams params = (LayoutParams) picker.getLayoutParams();
		params.setMargins(Util.dp2px(context, margin), 0, 0, 0);
		params.width = Util.dp2px(context, width);
		return picker;
	}

}