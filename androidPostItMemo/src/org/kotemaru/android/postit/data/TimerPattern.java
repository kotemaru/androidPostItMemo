// Copyright (c) kotemaru.org  (APL/2.0)
package org.kotemaru.android.postit.data;

import android.content.Context;
import android.util.Log;

import org.kotemaru.android.postit.R;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * タイマーパターン。
 * <li>以下の抽象的な日付を表現する。<ul>
 * <li>毎月の n 日</li>
 * <li>毎週の m 曜日</li>
 * <li>毎月の第 n , m 曜日</li>
 * <li>毎年の n 月 m 日</li>
 * </ul>
 * </li>
 * <li>従って、年,月,日,曜日はそれぞれ不定値の状態を持つ。</li>
 * <li>但し、日が特定出来ない状態は Bad データとなる。</li>
 * <li>DB上では正規文字列化して保存する。<ul>
 * <li>書式："yyyy/mm/dd/曜日/第n HH:MM"</li>
 * <li>例："＊＊＊＊/12/＊＊＊/Mon/1 13:00"</li>
 * </ul>
 * </li>
 */

public class TimerPattern {

	/**
	 * 正規化文字列用 文字列表現。
	 */
	public static final String[] DAY_OF_WEEKS = {
			"***", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"
	};
	public static String[] sDayOfWeeks;

	/**
	 * 表示用、曜日の第n
	 */
	public static final String[] DAY_OF_WEEKS_IN_MONTH = new String[] {
			"", "/1st", "/2nd", "/3rd", "/4th", "/5th"
	};

	private int mYear;   // yyyy, 0=****
	private int mMonth;  // 1-12, 0=**
	private int mDate;   // 1-31, 0=**
	private int mDayOfWeek; // 1-7, 0=***
	private int mDayOfWeekInMonth; // 1-5, 0=*
	private int mHour;
	private int mMinute;

	/**
	 * 表示用の曜日の配列を返す。
	 * @param context
	 * @return 表示用の曜日の配列
	 */
	public static String[] getDayOfWeeks(Context context) {
		if (sDayOfWeeks == null) {
			sDayOfWeeks = context.getString(R.string.array_of_week).split(",");
		}
		return sDayOfWeeks;
	}

	/**
	 * 正規化文字列からインスタンスを生成する。
	 * @param formal 正規化文字列
	 * @return TimerPatternインスタンス
	 */
	public static TimerPattern create(String formal) {
		return new TimerPattern().fromFormalString(formal);
	}

	public TimerPattern() {
	}

	/**
	 * 有効なデータか否かを返す。
	 * <li>日付が特定できかつ曜日と日の設定が衝突しない事。</li>
	 * @return true=有効データ。
	 */
	public boolean isValid() {
		if (!hasData()) return false;
		if (mDate != 0) return mDayOfWeek == 0;
		if (mDayOfWeek != 0) return mDate == 0;
		return false;
	}

	/**
	 * 何らかのデータを持っているかいなか。
	 * @return true=データ有り
	 */
	public boolean hasData() {
		return mYear != 0 || mMonth != 0 || mDate != 0 || mDayOfWeek != 0;
	}

	/**
	 * 正規化文字列からデータを設定する。
	 * @param formal 正規化文字列
	 * @return this
	 */
	public TimerPattern fromFormalString(String formal) throws NumberFormatException {
		if (formal == null) return this;
		String[] date_time = formal.split(" ");
		String[] dates = date_time[0].split("/");
		if (dates.length != 5) throw new NumberFormatException(formal);
		mYear = getIntValue(dates[0]);
		mMonth = getIntValue(dates[1]);
		mDate = getIntValue(dates[2]);
		mDayOfWeek = getWeekValue(dates[3]);
		mDayOfWeekInMonth = getIntValue(dates[4]);

		if (date_time.length == 1) return this;
		String[] times = date_time[1].split(":");
		if (times.length != 2) throw new NumberFormatException(formal);
		mHour = getIntValue(times[0]);
		mMinute = getIntValue(times[1]);
		return this;
	}

	/**
	 * 現在時刻を設定する。
	 * @param time 現在時刻(ms)
	 * @return this
	 * @throws NumberFormatException
	 */
	public TimerPattern fromTime(long time) throws NumberFormatException {
		Calendar cal = new GregorianCalendar();
		cal.setTimeInMillis(time);
		mYear = cal.get(Calendar.YEAR);
		mMonth = cal.get(Calendar.MONTH) + 1;
		mDate = cal.get(Calendar.DATE);
		mDayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
		mDayOfWeekInMonth = cal.get(Calendar.DAY_OF_WEEK_IN_MONTH);
		mHour = cal.get(Calendar.HOUR_OF_DAY);
		mMinute = cal.get(Calendar.MINUTE);
		return this;
	}

	private int getIntValue(String str) {
		char ch = str.charAt(0);
		if (ch == '*') return 0;
		if (ch == '0') str = str.substring(1);
		return Integer.valueOf(str);
	}

	private int getWeekValue(String str) {
		for (int i = 0; i < DAY_OF_WEEKS.length; i++) {
			if (DAY_OF_WEEKS[i].equals(str)) return i;
		}
		return 0;
	}

	/**
	 * 正規化文字列を返す
	 * <li>書式："yyyy/mm/dd/曜日/第n HH:MM"</li>
	 * <li>不定値は桁数分の * </li>
	 * <li>曜日は３文字英語表記</li>
	 * <li>第nは１桁数値</li>
	 * @return 正規化文字列
	 */
	public String toFormalString() {
		StringBuilder sbuf = new StringBuilder(30);
		sbuf.append(mYear == 0 ? "****" : Integer.toString(mYear)).append('/');
		sbuf.append(toIntStr2(mMonth)).append('/');
		sbuf.append(toIntStr2(mDate)).append('/');
		sbuf.append(DAY_OF_WEEKS[mDayOfWeek]).append('/');
		sbuf.append(toIntStr1(mDayOfWeekInMonth)).append(' ');
		sbuf.append(toIntStr00(mHour)).append(':');
		sbuf.append(toIntStr00(mMinute));
		return sbuf.toString();
	}

	private String toIntStr1(int val) {
		if (val == 0) return "*";
		return Integer.toString(val);
	}
	private String toIntStr2(int val) {
		if (val == 0) return "**";
		if (val <= 9) return "0" + val;
		return Integer.toString(val);
	}
	private String toIntStr00(int val) {
		if (val <= 9) return "0" + val;
		return Integer.toString(val);
	}

	public String toLocaleString(Context context) {
		return toLocaleString(context, true);
	}

	/**
	 * ロケールに合わせた表示文字列表現。
	 * <li>年,月,日,曜日の順序がロケールで異なるのでその対応。</li>
	 * @param context
	 * @param withDayOfWeekInMonth 曜日の第n 表記を伴う
	 * @return 表示文字列表現
	 */
	public String toLocaleString(Context context, boolean withDayOfWeekInMonth) {
		StringBuilder sbuf = new StringBuilder(30);

		char[] order = android.text.format.DateFormat.getDateFormatOrder(context);
		for (char ch : order) {
			switch (ch) {
			case 'y':
				sbuf.append(mYear == 0 ? "****" : Integer.toString(mYear)).append('/');
				break;
			case 'M':
				sbuf.append(toIntStr2(mMonth)).append('/');
				break;
			case 'd':
				sbuf.append(toIntStr2(mDate)).append('/');
				break;
			}
		}
		sbuf.setLength(sbuf.length() - 1);
		String[] dayOfWeeks = getDayOfWeeks(context);
		String dayOfWeek = dayOfWeeks[mDayOfWeek];
		if (withDayOfWeekInMonth) {
			dayOfWeek += DAY_OF_WEEKS_IN_MONTH[mDayOfWeekInMonth];
		}
		if (order[0] == 'y') {
			sbuf.append('(').append(dayOfWeek).append(')');
		} else {
			sbuf.insert(0, dayOfWeek + ", ");
		}

		sbuf.append(' ');
		sbuf.append(toIntStr00(mHour)).append(':');
		sbuf.append(toIntStr00(mMinute));
		return sbuf.toString();
	}

	/**
	 * 次の一致日時を返す。
	 * <li>このパターンに一致する現在時刻から一番近い日時を返す。</li>
	 * @return 次の一致日時
	 */
	public Calendar getNextDate() {
		Calendar cal = new GregorianCalendar();
		long currentTime = cal.getTimeInMillis();

		if (mYear != 0) cal.set(Calendar.YEAR, mYear);
		if (mMonth != 0) cal.set(Calendar.MONTH, mMonth - 1);
		if (mDate != 0) cal.set(Calendar.DAY_OF_MONTH, mDate);
		if (mDayOfWeek != 0) cal.set(Calendar.DAY_OF_WEEK, mDayOfWeek);
		cal.set(Calendar.HOUR_OF_DAY, mHour);
		cal.set(Calendar.MINUTE, mMinute);
		cal.set(Calendar.SECOND, 0);
		// Log.d("DEBUG", "Set timer:moto=" + cal.getTime());

		if (mYear != 0 && cal.getTimeInMillis() < currentTime) return cal;

		if (mDayOfWeek != 0) {
			while (cal.getTimeInMillis() < currentTime)
				cal.add(Calendar.DATE, 7);
			if (mDayOfWeekInMonth != 0) {
				while (mDayOfWeekInMonth != cal.get(Calendar.DAY_OF_WEEK_IN_MONTH)) {
					cal.add(Calendar.DATE, 7);
				}
			} else {
				while (cal.getTimeInMillis() < currentTime) {
					cal.add(Calendar.DATE, 7);
				}
			}
		} else {
			if (mDate != 0 && cal.getTimeInMillis() < currentTime) {
				cal.add(mMonth != 0 ? Calendar.YEAR : Calendar.MONTH, 1);
			}
		}
		return cal;
	}

	// ---------------------------------
	public boolean hasYear() {
		return mYear != 0;
	}

	public boolean hasMonth() {
		return mMonth != 0;
	}

	public boolean hasDate() {
		return mDate != 0;
	}

	public boolean hasDayOfWeek() {
		return mDayOfWeek != 0;
	}

	public int getYear() {
		return mYear;
	}

	public void setYear(int year) {
		mYear = year;
	}

	public int getMonth() {
		return mMonth;
	}

	public void setMonth(int month) {
		mMonth = month;
	}

	public int getDate() {
		return mDate;
	}

	public void setDate(int date) {
		mDate = date;
	}

	public int getDayOfWeek() {
		return mDayOfWeek;
	}

	public void setDayOfWeek(int dayOfWeek) {
		mDayOfWeek = dayOfWeek;
	}

	public int getHour() {
		return mHour;
	}

	public void setHour(int hour) {
		mHour = hour;
	}

	public int getMinute() {
		return mMinute;
	}

	public void setMinute(int minute) {
		mMinute = minute;
	}

	public int getDayOfWeekInMonth() {
		return mDayOfWeekInMonth;
	}

	public void setDayOfWeekInMonth(int dayOfWeekInMonth) {
		mDayOfWeekInMonth = dayOfWeekInMonth;
	}
}
