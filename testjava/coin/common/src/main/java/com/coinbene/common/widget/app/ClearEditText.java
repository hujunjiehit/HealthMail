package com.coinbene.common.widget.app;

import android.content.Context;
import android.text.TextWatcher;
import android.util.AttributeSet;

import java.util.ArrayList;

public class ClearEditText extends androidx.appcompat.widget.AppCompatEditText {

	private ArrayList<TextWatcher> mListeners = null;

	public ClearEditText(Context ctx) {
		super(ctx);
	}

	public ClearEditText(Context ctx, AttributeSet attrs) {
		super(ctx, attrs);
	}

	public ClearEditText(Context ctx, AttributeSet attrs, int defStyle) {
		super(ctx, attrs, defStyle);
	}

	@Override
	public void addTextChangedListener(TextWatcher watcher) {
		if (mListeners == null) {
			mListeners = new ArrayList<TextWatcher>();
		}
		if (mListeners.contains(watcher)) {
			return;
		}
		mListeners.add(watcher);
		super.addTextChangedListener(watcher);
	}

	@Override
	public void removeTextChangedListener(TextWatcher watcher) {
		if (mListeners != null) {
			int i = mListeners.indexOf(watcher);
			if (i >= 0) {
				mListeners.remove(i);
			}
		}

		super.removeTextChangedListener(watcher);
	}

	public void clearTextChangedListeners() {
		if (mListeners != null) {
			for (TextWatcher watcher : mListeners) {
				super.removeTextChangedListener(watcher);
			}

			mListeners.clear();
			//mListeners = null;
		}
	}

	public void destroy(){
		if (mListeners != null) {
			mListeners.clear();
			mListeners = null;
		}
	}
}
