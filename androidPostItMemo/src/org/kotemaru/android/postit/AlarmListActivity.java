package org.kotemaru.android.postit;

import org.kotemaru.android.postit.PostItConst.PostItColor;
import org.kotemaru.android.postit.PostItConst.PostItFontSize;
import org.kotemaru.android.postit.PostItConst.PostItShape;
import org.kotemaru.android.postit.data.PostItData;
import org.kotemaru.android.postit.data.PostItDataProvider;
import org.kotemaru.android.postit.data.TimerPattern;
import org.kotemaru.android.postit.dialog.DatetimePickerDialogFragment;
import org.kotemaru.android.postit.layout.AlarmListActivityViews;
import org.kotemaru.android.postit.layout.AlarmListItemViews;
import org.kotemaru.android.postit.util.IntIntMap;
import org.kotemaru.android.postit.util.Launcher;
import org.kotemaru.android.postit.util.Util;
import org.kotemaru.android.postit.widget.PostItView;
import org.kotemaru.android.postit.widget.RadioLayout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import de.timroes.android.listview.EnhancedListView;

/**
 * タイマーが設定されている付箋の一覧を表示する。
 */
public class AlarmListActivity extends Activity {
	private static final String TAG = AlarmListActivity.class.getSimpleName();

	private EnhancedListView mListView;
	private PostItDataAdapter mAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alarm_list_activity);
		final Context context = this;

		mListView = (EnhancedListView) findViewById(R.id.list_view);
		mAdapter = new PostItDataAdapter(this);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				PostItData data = mAdapter.getPostItData(position);
				if (data == null) return;
				Launcher.startPostItSettingsActivity(context, data);
			}
		});
		mListView.setDismissCallback(new EnhancedListView.OnDismissCallback() {
			@Override
			public EnhancedListView.Undoable onDismiss(EnhancedListView listView, final int position) {
				final PostItData data = mAdapter.getPostItData(position);
				if (data == null) return null;
				PostItDataProvider.removePostItData(context, data);
				mAdapter.updateData(context);
				return new EnhancedListView.Undoable() {
					@Override
					public void undo() {
						PostItDataProvider.createPostItData(context, data);
						mAdapter.updateData(context);
					}
				};
			}
		});
		mListView.enableSwipeToDismiss();
	}

	/**
	 * 付箋データから各Viewの値を設定。
	 */
	@Override
	public void onResume() {
		super.onResume();

		mAdapter.updateData(this);
	}

	private class PostItDataAdapter extends BaseAdapter {
		private List<PostItData> mList;
		private TimerPattern mTimerPattern = new TimerPattern();

		public PostItDataAdapter(AlarmListActivity alarmListActivity) {
		}

		public void updateData(Context context) {
			List<PostItData> list = PostItDataProvider.getAllPostItData(context);
			Iterator<PostItData> ite = list.iterator();
			while (ite.hasNext()) {
				PostItData data = ite.next();
				if (data.getTimerPattern() == null) ite.remove();
			}
			Collections.sort(list, new Comparator<PostItData>() {
				@Override
				public int compare(PostItData lhs, PostItData rhs) {
					return lhs.getTimer() > rhs.getTimer() ? 1 : -1;
				}
			});
			mList = list;
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			if (mList == null) return 0;
			return mList.size();
		}

		@Override
		public Object getItem(int position) {
			return getPostItData(position);
		}
		public PostItData getPostItData(int position) {
			if (mList == null) return null;
			return mList.get(position);
		}
		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			if (view == null) {
				view = getLayoutInflater().inflate(R.layout.alarm_list_item, null);
				AlarmListItemViews tag = new AlarmListItemViews(view);
				view.setTag(tag);
			}
			Context context = getBaseContext();
			PostItData data = getPostItData(position);
			if (data == null) return view;
			AlarmListItemViews tag = (AlarmListItemViews) view.getTag();
			String dateTime = mTimerPattern.fromTime(data.getTimer()).toLocaleString(context, false);
			tag.mDate.setText(dateTime.substring(0, dateTime.length() - 6));
			tag.mTime.setText(dateTime.substring(dateTime.length() - 5));
			tag.mMemo.setText(data.getMemo());
			tag.mMemo.setBackgroundResource(PostItView.sColorResourceMap.getFirst(data.getColor()));
			int pad = Util.dp2px(context, 2);
			tag.mMemo.setPadding(pad, 0, pad, 0);
			return view;
		}
	}
}
