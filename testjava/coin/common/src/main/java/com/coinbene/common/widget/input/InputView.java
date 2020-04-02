package com.coinbene.common.widget.input;

import android.content.Context;
import android.os.SystemClock;
import android.text.Editable;
import android.text.Selection;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.coinbene.common.R;
import com.coinbene.common.R2;
import com.coinbene.common.utils.Tools;
import com.coinbene.common.widget.BaseTextWatcher;
import com.coinbene.common.widget.app.ClearEditText;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by june
 * on 2019-12-26
 */
public class InputView extends LinearLayout {

	@BindView(R2.id.btn_minus)
	ImageView mBtnMinus;
	@BindView(R2.id.edit_text)
	ClearEditText mEditText;
	@BindView(R2.id.btn_plus)
	ImageView mBtnPlus;
	@BindView(R2.id.view_container)
	View mViewContainer;
	private Context mContext;

	private int intervalPerSecond = 10;  //长按时每秒触发次数

	private ObservableEmitter btnEmitter;   //+ -按钮 长按事件发射器
	private Observable<String> btnObservable;
	private Disposable disposable;

	private ExecutorService threadPool = Executors.newSingleThreadExecutor();

	private int min = 1;
	private int max = 50;
	private OnValueChangeListener valueChangeListener;
	private BaseTextWatcher mEditTextWatcher;

	public InputView(Context context) {
		super(context);
		initView(context);
	}

	public InputView(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public InputView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initView(context);
	}

	public void initInputView(int min, int max) {
		this.min = min;
		this.max = max;
		clearFocus();
	}

	public void setValue(int value) {
		if (value <= min) {
			mEditText.setText(min + "");
			Selection.setSelection(mEditText.getText(), mEditText.getText().toString().length());
		} else if (value >= max) {
			mEditText.setText(max + "");
			Selection.setSelection(mEditText.getText(), mEditText.getText().toString().length());
		} else {
			mEditText.setText(value + "");
		}
	}

	public int getValue() {
		return Tools.parseInt(mEditText.getText().toString().trim(), 1);
	}

	private void initView(Context context) {
		this.mContext = context;
		LayoutInflater.from(mContext).inflate(R.layout.layout_input_view, this, true);
		ButterKnife.bind(this);

		if (isInEditMode()) {
			return;
		}

		setListener();

		btnObservable = Observable.create(e -> btnEmitter = e);
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		if (isInEditMode()) {
			return;
		}
		disposable = btnObservable
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(result -> {
					if ("+".equals(result)) {
						onPlusClick();
					} else if ("-".equals(result)) {
						onMinusClick();
					}
				});
	}


	private void onPlusClick() {
		int current = getValue();
		setValue(current + 1);
		clearFocus();
	}


	private void onMinusClick() {
		int current = getValue();
		setValue(current - 1);
		clearFocus();
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		if (disposable != null && !disposable.isDisposed()) {
			disposable.dispose();
		}
	}

	private void setListener() {
		mBtnPlus.setOnClickListener(v -> onPlusClick());

		mBtnMinus.setOnClickListener(v -> onMinusClick());

		mBtnPlus.setOnLongClickListener(v -> {
			clearFocus();
			threadPool.execute(() -> {
				while (mBtnPlus.isPressed()) {
					if (disposable != null && !disposable.isDisposed() && btnEmitter != null) {
						btnEmitter.onNext("+");
					}
					SystemClock.sleep(1000/intervalPerSecond);
				}
			});
			return false;
		});

		mBtnMinus.setOnLongClickListener(v -> {
			clearFocus();
			threadPool.execute(() -> {
				while (mBtnMinus.isPressed()) {
					if (disposable != null && !disposable.isDisposed() && btnEmitter != null) {
						btnEmitter.onNext("-");
					}
					SystemClock.sleep(1000/intervalPerSecond);
				}
			});
			return false;
		});

		if (mEditTextWatcher == null) {
			mEditTextWatcher = new BaseTextWatcher() {
				@Override
				public void afterTextChanged(Editable s) {
					mEditText.removeTextChangedListener(mEditTextWatcher);

					if (getValue() > max) {
						setValue(max);
					}
					if (getValue() == 0) {
						setValue(1);
					}
					if (valueChangeListener != null) {
						valueChangeListener.onValueChanged(getValue());
					}
					Selection.setSelection(mEditText.getText(), mEditText.getSelectionStart());
					mEditText.setSelection(mEditText.getSelectionStart());
					mEditText.addTextChangedListener(mEditTextWatcher);
				}
			};
		}
		mEditText.addTextChangedListener(mEditTextWatcher);

		mEditText.setOnFocusChangeListener((v, hasFocus) -> {
			if (hasFocus) {
				mViewContainer.setBackground(getResources().getDrawable(R.drawable.shape_edit_focused_background));
			} else {
				mViewContainer.setBackground(getResources().getDrawable(R.drawable.shape_edit_normal_background));
			}
		});
	}

	public void setValueChangeListener(OnValueChangeListener valueChangeListener) {
		this.valueChangeListener = valueChangeListener;
	}

	public interface OnValueChangeListener {
		void onValueChanged(int value);
	}

	public void clearFocus() {
		if (mEditText != null && mEditText.hasFocus()) {
			mEditText.clearFocus();
		}
	}
}
