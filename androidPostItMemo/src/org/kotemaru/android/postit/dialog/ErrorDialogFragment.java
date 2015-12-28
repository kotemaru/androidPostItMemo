// Copyright (c) kotemaru.org  (APL/2.0)
package org.kotemaru.android.postit.dialog;

import org.kotemaru.android.postit.R;
import org.kotemaru.android.postit.data.TimerPattern;
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

public class ErrorDialogFragment extends DialogFragment {
	private static final String TAG = ErrorDialogFragment.class.getSimpleName();
	private static final String KEY_MESSAGE = "KEY_MESSAGE";

	public static ErrorDialogFragment show(Activity activity, String message) {
		ErrorDialogFragment dialog = new ErrorDialogFragment();
		Bundle args = new Bundle();
		args.putString(KEY_MESSAGE, message);
		dialog.setArguments(args);
		dialog.show(activity.getFragmentManager(), ErrorDialogFragment.class.getCanonicalName());
		return dialog;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Bundle args = getArguments();
		final DialogInterface.OnClickListener okListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dismiss();
			}
		};
		return new AlertDialog.Builder(getActivity())
				.setTitle("Error!")
				.setMessage(args.getString(KEY_MESSAGE))
				.setPositiveButton("OK", okListener)
				.create();
	}

}
