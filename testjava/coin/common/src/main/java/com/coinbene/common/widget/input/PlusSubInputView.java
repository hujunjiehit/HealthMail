package com.coinbene.common.widget.input;

import android.content.Context;
import android.os.SystemClock;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.Group;

import com.coinbene.common.R;
import com.coinbene.common.R2;
import com.coinbene.common.utils.BigDecimalUtils;
import com.coinbene.common.utils.PrecisionUtils;
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

public class PlusSubInputView extends LinearLayout {
	private Context mContext;

	@BindView(R2.id.btn_minus)
	ImageView mBtnMinus;
	@BindView(R2.id.edit_text)
	ClearEditText mEditText;
	@BindView(R2.id.btn_plus)
	ImageView mBtnPlus;
	@BindView(R2.id.view_group)
	Group viewGroup;


	private int intervalPerSecond = 10;  //长按时每秒触发次数

	private ObservableEmitter btnEmitter;   //+ -按钮 长按事件发射器
	private Observable<String> btnObservable;
	private Disposable disposable;

	private ExecutorService threadPool = Executors.newSingleThreadExecutor();
	private String minPriceChange = "";
	private boolean enable = true;//是否可以点击加减
	private OnPlusSubListener onPlusSubListener;
	private String  max;

	public void setMax(String max) {
		this.max = max;
	}

	public void setOnPlusSubListener(OnPlusSubListener onPlusSubListener) {
		this.onPlusSubListener = onPlusSubListener;
	}

	public PlusSubInputView(Context context) {
		super(context);
		initView(context);
	}

	public PlusSubInputView(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public PlusSubInputView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initView(context);
	}


	public ClearEditText getmEditText() {
		return mEditText;
	}

	private void initView(Context context) {
		this.mContext = context;
		LayoutInflater.from(mContext).inflate(R.layout.layout_add_sub_input_view, this, true);
		ButterKnife.bind(this);

		if (isInEditMode()) {
			return;
		}

		setListener();

		btnObservable = Observable.create(e -> btnEmitter = e);
	}

	public String getValue() {
		return TextUtils.isEmpty(getText()) ? "0" : getText();
	}

	public void setPlusSubWirth(int dp){

		ViewGroup.LayoutParams layoutParams = mBtnPlus.getLayoutParams();
		layoutParams.width = dp;
		mBtnPlus.setLayoutParams(layoutParams);

		ViewGroup.LayoutParams layoutParams1 = mBtnMinus.getLayoutParams();
		layoutParams.width = dp;
		mBtnMinus.setLayoutParams(layoutParams1);
	}


	private void onPlusClick() {
		if (TextUtils.isEmpty(mEditText.getText().toString()) && onPlusSubListener != null) {
			onPlusSubListener.onPlus();
			return;
		}
		String result = BigDecimalUtils.add(getValue(), minPriceChange);
		if(!TextUtils.isEmpty(max)&&BigDecimalUtils.isGreaterThan(result,max)){
			return;
		}


		mEditText.setText(result);
		Selection.setSelection(mEditText.getText(), mEditText.getText().toString().length());
	}


	private void onMinusClick() {

		if (TextUtils.isEmpty(mEditText.getText().toString()) && onPlusSubListener != null) {
			onPlusSubListener.onSub();
			return;
		}
		if (TextUtils.isEmpty(mEditText.getText().toString())) {
			return;
		}
		String value = BigDecimalUtils.subtract(getValue(), minPriceChange);
		if (BigDecimalUtils.compareZero(value) <= 0) {
			value = "0";
		}
		mEditText.setText(value);
		Selection.setSelection(mEditText.getText(), mEditText.getText().toString().length());
	}

	public void setMinPriceChange(String minPriceChange) {
		this.minPriceChange = minPriceChange;
		if (BigDecimalUtils.isEmptyOrZero(minPriceChange)) {
			this.minPriceChange = "1";
		}
	}


	public void setEnablePlusSub(boolean enable) {
		this.enable = enable;
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


	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		if (disposable != null && !disposable.isDisposed()) {
			disposable.dispose();
		}
	}

	private void setListener() {
		mBtnPlus.setOnClickListener(v -> {
			if (!enable) {
				return;
			}
			onPlusClick();
			Selection.setSelection(mEditText.getText(), mEditText.getText().toString().length());
		});

		mBtnMinus.setOnClickListener(v -> {
			if (!enable) {
				return;
			}
			onMinusClick();
			Selection.setSelection(mEditText.getText(), mEditText.getText().toString().length());
		});

		mBtnPlus.setOnLongClickListener(v -> {
			if (!enable) {
				return false;
			}

			threadPool.execute(() -> {
				while (mBtnPlus.isPressed()) {
					if (disposable != null && !disposable.isDisposed() && btnEmitter != null) {
						btnEmitter.onNext("+");
					}
					SystemClock.sleep(1000 / intervalPerSecond);
				}
			});
			return false;
		});

		mBtnMinus.setOnLongClickListener(v -> {
			if (!enable) {
				return false;
			}


			threadPool.execute(() -> {
				while (mBtnMinus.isPressed()) {
					if (disposable != null && !disposable.isDisposed() && btnEmitter != null) {
						btnEmitter.onNext("-");
					}
					SystemClock.sleep(1000 / intervalPerSecond);
				}
			});
			return false;
		});

//		mEditText.addTextChangedListener(new BaseTextWatcher() {
//			@Override
//			public void afterTextChanged(Editable s) {
//
//
////				Selection.setSelection(mEditText.getText(), mEditText.getText().toString().length());
//			}
//		});
	}

	public void setText(String s) {
		mEditText.setText(s);
	}


	public void setSeparatorText(TextWatcher textWatcher) {
		if (TextUtils.isEmpty(getText())) {
			return;
		}
		clearTextChangedListeners();
		setText(PrecisionUtils.getQuantityContractRule(getText()));
		setSelection(mEditText.getText().length());
		addTextChangedListener(textWatcher);

	}

	public void removeTextChangedListener(TextWatcher priceTextWatcher) {
		mEditText.removeTextChangedListener(priceTextWatcher);
	}

	public void addTextChangedListener(TextWatcher textWatcher) {
		mEditText.addTextChangedListener(textWatcher);
	}

	public String getText() {
		return mEditText.getText().toString().trim().replace(",", "");
	}

	public void setSelection(int length) {
		mEditText.setSelection(length);
	}

	public void setOnFocusChangeListener(OnFocusChangeListener l) {
		mEditText.setOnFocusChangeListener(l);
	}

	public void setHint(int price) {
		mEditText.setHint(price);
	}

	public void setHint(String format) {
		mEditText.setHint(format);
	}


	public void setEnabled(boolean enable) {

		if (enable) {
			viewGroup.setVisibility(VISIBLE);
		} else {
			viewGroup.setVisibility(GONE);
			mEditText.setText("");
		}
		mEditText.setEnabled(enable);

	}

	@Override
	public void clearFocus() {
		super.clearFocus();
		mEditText.clearFocus();
	}

	public void clearTextChangedListeners() {
		mEditText.clearTextChangedListeners();
	}

	public void setInputType(int typeClassNumber) {
		getmEditText().setInputType(typeClassNumber);
	}

	public interface OnPlusSubListener {
		void onPlus();

		void onSub();
	}
}
