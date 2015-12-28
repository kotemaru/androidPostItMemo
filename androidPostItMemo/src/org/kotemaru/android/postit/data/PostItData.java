package org.kotemaru.android.postit.data;

import org.kotemaru.android.postit.PostItConst.PostItShape;

/**
 * 付箋データBean。
 * @author kotemaru.org
 */
public class PostItData {
	private long id;
	private int enabled;
	private int color; // 1-5
	private int posX; // px
	private int posY; // px
	private int width; // dp
	private int height; // dp
	private int fontSize; // sp
	private int timerIsRepeat;
	private String timerPattern;
	private long timer;
	private String memo;

	public PostItData() {
	}

	public PostItData(long id, int color, int posX, int posY) {
		this(id, 1, color, posX, posY, PostItShape.W_LONG, PostItShape.H_SMALL, 12, 0, null, 0, "");
	}

	public PostItData(long id, int enabled, int color, int posX, int posY, int width, int height,
			int fontSize, int timerIsRepeat, String timerPattern, long timer, String memo) {
		this.id = id;
		this.enabled = enabled;
		this.color = color;
		this.posX = posX;
		this.posY = posY;
		this.width = width;
		this.height = height;
		this.fontSize = fontSize;
		this.timerIsRepeat = timerIsRepeat;
		this.timerPattern = timerPattern;
		this.timer = timer;
		this.memo = memo;
	}

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public int getEnabled() {
		return enabled;
	}
	public boolean isEnabled() {
		return enabled != 0;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled ? 1 : 0;
	}

	public int getColor() {
		return color;
	}
	public void setColor(int color) {
		this.color = color;
	}
	public int getPosX() {
		return posX;
	}
	public void setPosX(int posX) {
		this.posX = posX;
	}
	public int getPosY() {
		return posY;
	}
	public void setPosY(int posY) {
		this.posY = posY;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getFontSize() {
		return fontSize;
	}

	public void setFontSize(int fontSize) {
		this.fontSize = fontSize;
	}

	public int getTimerIsRepeat() {
		return timerIsRepeat;
	}

	public boolean isTimerIsRepeat() {
		return timerIsRepeat != 0;
	}

	public void setTimerIsRepeat(boolean timerIsRepeat) {
		this.timerIsRepeat = timerIsRepeat ? 1 : 0;
	}

	public String getTimerPattern() {
		return timerPattern;
	}

	public void setTimerPattern(String timerPattern) {
		this.timerPattern = timerPattern;
	}

	public long getTimer() {
		return timer;
	}

	public void setTimer(long timer) {
		this.timer = timer;
	}
}
