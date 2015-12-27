// Copyright (c) kotemaru.org  (APL/2.0)
package org.kotemaru.android.postit.dialog;

import org.kotemaru.android.postit.R;
import org.kotemaru.android.postit.data.TimerPattern;
import org.kotemaru.android.postit.layout.DatetimePickerDialogViews;
import org.kotemaru.android.postit.widget.DatePicker;
import org.kotemaru.android.postit.widget.TimePicker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.sql.Time;

/**
 * タイマーパターン設定ダイアログ。
 * <li>日付と時刻を設定する。</li>
 */
public class DatetimePickerDialogFragment extends DialogFragment {
	private static final String TAG = DatetimePickerDialogFragment.class.getSimpleName();
	private static final String KEY_TIMER_PATTERN = "KEY_TIMER_PATTERN";

	public interface Callback {
		/**
		 * OKがタップされた時のタイマーパターンをコールバックする。
		 * @param pattern タイマーパターン
		 */
		public void setTimerPattern(TimerPattern pattern);
	}

	public static DatetimePickerDialogFragment show(Activity activity, TimerPattern timerPattern) {
		DatetimePickerDialogFragment dialog = new DatetimePickerDialogFragment();
		Bundle args = new Bundle();
		args.putString(KEY_TIMER_PATTERN, timerPattern.toFormalString());
		dialog.setArguments(args);
		dialog.show(activity.getFragmentManager(), DatetimePickerDialogFragment.class.getCanonicalName());
		return dialog;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Bundle args = getArguments();
		View view = getActivity().getLayoutInflater().inflate(R.layout.datetime_picker_dialog, null);
		final DatetimePickerDialogViews views = new DatetimePickerDialogViews(view);
		TimerPattern pattern = TimerPattern.create(args.getString(KEY_TIMER_PATTERN));
		views.mDatePicker.setValue(pattern);
		views.mTimePicker.setValue(pattern);

		final DialogInterface.OnClickListener okListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				TimerPattern pattern = new TimerPattern();
				views.mDatePicker.assignTimerPattern(pattern);
				views.mTimePicker.assignTimerPattern(pattern);
				if (pattern.hasData() && !pattern.isValid()) {
					ErrorDialogFragment.show(getActivity(), getString(R.string.dialog_timer_error));
				} else {
					dismiss();
					((Callback) getActivity()).setTimerPattern(pattern);
				}
			}
		};
		final DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dismiss();
			}
		};
		final DialogInterface.OnClickListener resetListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				TimerPattern pattern = new TimerPattern();
				views.mDatePicker.setValue(pattern);
				views.mTimePicker.setValue(pattern);
			}
		};

		return new AlertDialog.Builder(getActivity())
				.setTitle(R.string.dialog_timer_title)
				.setView(view)
				.setPositiveButton("OK", okListener)
				.setNegativeButton("Cancel", cancelListener)
				.setNeutralButton("Reset", resetListener)
				.create();
	}

	@Override
	public void onStart() {
		super.onStart();
		// Note: 何かのバグでここで設定しないと BUTTON_NEUTRAL が反応しないので。
		AlertDialog dialog = (AlertDialog) getDialog();
		if (dialog == null) return;

		final DatePicker datePicker = (DatePicker) dialog.findViewById(R.id.date_picker);
		final TimePicker timePicker = (TimePicker) dialog.findViewById(R.id.time_picker);
		Button neutralButton = dialog.getButton(Dialog.BUTTON_NEUTRAL);
		neutralButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				TimerPattern pattern = new TimerPattern();
				datePicker.setValue(pattern);
				timePicker.setValue(pattern);
			}
		});
	}

}
