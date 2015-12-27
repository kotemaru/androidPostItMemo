package org.kotemaru.android.postit;

import org.kotemaru.android.postit.PostItConst.PostItColor;
import org.kotemaru.android.postit.PostItConst.PostItFontSize;
import org.kotemaru.android.postit.PostItConst.PostItShape;
import org.kotemaru.android.postit.data.TimerPattern;
import org.kotemaru.android.postit.data.PostItData;
import org.kotemaru.android.postit.data.PostItDataProvider;
import org.kotemaru.android.postit.dialog.DatetimePickerDialogFragment;
import org.kotemaru.android.postit.layout.PostItSettingActivityViews;
import org.kotemaru.android.postit.util.IntIntMap;
import org.kotemaru.android.postit.util.Launcher;
import org.kotemaru.android.postit.widget.RadioLayout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

/**
 * 付箋データの編集画面。
 * <li>メモ、形状、フォントサイズ、色の編集。
 * <li>Activityを終了すると自動的にデータを保存する。キャンセルはできない。
 *
 * @author kotemaru.org
 */
public class PostItSettingActivity extends Activity
		implements DatetimePickerDialogFragment.Callback {
	private static final String TAG = PostItSettingActivity.class.getSimpleName();

	/** ラジオボタンと色コードのマップ。 */
	private static final IntIntMap sColorRadioMap = new IntIntMap(new int[][] {
			{ R.id.color_blue, PostItColor.BLUE, },
			{ R.id.color_green, PostItColor.GREEN, },
			{ R.id.color_yellow, PostItColor.YELLOW, },
			{ R.id.color_pink, PostItColor.PINK, },
			{ R.id.color_red, PostItColor.RED, },
	});
	/** ラジオボタンとフォントサイズのマップ */
	private static final IntIntMap sFontRadioMap = new IntIntMap(new int[][] {
			{ R.id.font_small, PostItFontSize.SMALL, },
			{ R.id.font_middle, PostItFontSize.MIDDLE, },
			{ R.id.font_lage, PostItFontSize.LAGE, },
			{ R.id.font_huge, PostItFontSize.HUGE, },
	});
	/** ラジオボタンと付箋サイズのマップ */
	private static final IntIntMap sShapeRadioMap = new IntIntMap(new int[][] {
			{ R.id.shape_shot, PostItShape.W_SHORT, PostItShape.H_SMALL },
			{ R.id.shape_long, PostItShape.W_LONG, PostItShape.H_SMALL },
			{ R.id.shape_lage, PostItShape.W_LONG, PostItShape.H_LAGE },
	});

	private PostItData mPostItData;
	private PostItSettingActivityViews mViews;
	private TimerPattern mTimerPattern;

	/**
	 * intetntパラメータ
	 * <li>| POST_IT_ID | long型 | 必須 | 付箋ID。|
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.post_it_setting_activity);

		Intent intent = getIntent();
		long postItId = intent.getLongExtra(Launcher.POST_IT_ID, -1);
		mPostItData = PostItDataProvider.getPostItData(this, postItId);

		mViews = new PostItSettingActivityViews(this);
	}

	/**
	 * 付箋データから各Viewの値を設定。
	 */
	@Override
	public void onResume() {
		super.onResume();

		// restore settings.
		mViews.mMemo.setText(mPostItData.getMemo());
		mViews.mShapeRadioGroup.check(sShapeRadioMap.getFirst(mPostItData.getWidth(), mPostItData.getHeight()));
		mViews.mFontRadioGroup.check(sFontRadioMap.getFirst(mPostItData.getFontSize()));
		mViews.mColorRadioGroup.check(sColorRadioMap.getFirst(mPostItData.getColor()));
		mViews.mTimerRepeatable.setSelected(mPostItData.isTimerIsRepeat());

		setTimerPattern(TimerPattern.create(mPostItData.getTimerPattern()));
	}

	/**
	 * 各Viewの値から付箋データを更新。
	 */
	@Override
	public void onPause() {
		// save settings.
		mPostItData.setMemo(mViews.mMemo.getText().toString());

		int shapeResId = mViews.mShapeRadioGroup.getCheckedRadioButtonId();
		mPostItData.setWidth(sShapeRadioMap.getSecond(shapeResId));
		mPostItData.setHeight(sShapeRadioMap.getThird(shapeResId));

		int fontResId = mViews.mFontRadioGroup.getCheckedRadioButtonId();
		mPostItData.setFontSize(sFontRadioMap.getSecond(fontResId));

		int colorResId = mViews.mColorRadioGroup.getCheckedRadioButtonId();
		mPostItData.setColor(sColorRadioMap.getSecond(colorResId));

		mPostItData.setTimerIsRepeat(mViews.mTimerRepeatable.isSelected());

		if (mTimerPattern.isValid()) {
			// タイマーの設定。非表示にして発火時刻を設定する。
			mPostItData.setTimerPattern(mTimerPattern.toFormalString());
			mPostItData.setTimer(mTimerPattern.getNextDate().getTimeInMillis());
			mPostItData.setEnabled(false);
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Set timer:" + mTimerPattern.toFormalString() + ":" + mTimerPattern.getNextDate());
			}
		} else {
			mPostItData.setTimerPattern(null);
			mPostItData.setEnabled(true);
		}

		// DBに保存
		PostItDataProvider.updatePostItData(this, mPostItData);
		Launcher.notifyChangeData(this);

		if (mTimerPattern.isValid()) {
			AlarmReceiver.setAlarm(this);
		}
		super.onPause();
	}

	public void onClickTimerSetting(View view) {
		DatetimePickerDialogFragment.show(this, mTimerPattern);
	}

	@Override
	public void setTimerPattern(TimerPattern pattern) {
		mTimerPattern = pattern;
		if (mTimerPattern.isValid()) {
			mViews.mTimerSetting.setText(mTimerPattern.toLocaleString(this));
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Set timer:" + mTimerPattern.toFormalString() + ":" + mTimerPattern.getNextDate().getTime());
			}
			mViews.mTimerIcon.setImageResource(R.drawable.ic_clock_white);
			mViews.mTimerRepeatable.setVisibility(View.VISIBLE);
		} else {
			mViews.mTimerSetting.setText(R.string.timer_no_setting);
			mViews.mTimerIcon.setImageResource(R.drawable.ic_clock_gray);
			mViews.mTimerRepeatable.setSelected(false);
			mViews.mTimerRepeatable.setVisibility(View.GONE);
		}
	}
}
