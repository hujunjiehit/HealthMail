package com.coinbene.manbiwang.contract.contractbtc.activity.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.coinbene.common.Constants;
import com.coinbene.common.network.newokgo.NewJsonSubCallBack;
import com.coinbene.common.network.okgo.DialogCallback;
import com.coinbene.common.utils.Tools;
import com.coinbene.common.utils.TradeUtils;
import com.coinbene.common.widget.CustomSeekBar;
import com.coinbene.manbiwang.contract.R;
import com.coinbene.manbiwang.contract.R2;
import com.coinbene.manbiwang.model.http.LeverageLimitRes;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by june
 * on 2020-03-27
 */
public class ChooseLevelView extends ConstraintLayout {
	@BindView(R2.id.seek_bar)
	CustomSeekBar mSeekBar;
	@BindView(R2.id.tv_min_level)
	TextView mTvMinLevel;
	@BindView(R2.id.tv_max_level)
	TextView mTvMaxLevel;
	@BindView(R2.id.tv_label)
	TextView mTvLabel;
	@BindView(R2.id.tv_current_level)
	TextView mTvCurrentLevel;
	@BindView(R2.id.center_layout)
	LinearLayout mCenterLayout;

	private Context mContext;
	private LevelChangeListener mLevelChangeListener;

	private SparseArray<Integer> leverageMap;
	private int maxLevel = 0;
	private String currentLevel;

	public ChooseLevelView(Context context) {
		super(context);
		initView(context);
	}

	public ChooseLevelView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public ChooseLevelView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initView(context);
	}

	private void initView(Context context) {
		this.mContext = context;
		LayoutInflater.from(mContext).inflate(R.layout.choose_level_view, this, true);
		ButterKnife.bind(this);
		if (isInEditMode()) {
			return;
		}
	}

	public void setLevelChangeListener(LevelChangeListener mLevelChangeListener) {
		this.mLevelChangeListener = mLevelChangeListener;
	}

	public void initParams(String currentSymbol, String currentLevel) {
		this.currentLevel = currentLevel;
		String url = TradeUtils.isUsdtContract(currentSymbol) ? Constants.TRADE_USDT_LEVERAGE_LIMIT : Constants.TRADE_LEVERAGE_LIMIT;
		OkGo.<LeverageLimitRes>get(url)
				.params("symbol", currentSymbol)
				.tag(this)
				.execute(new NewJsonSubCallBack<LeverageLimitRes>() {
					@Override
					public void onSuc(Response<LeverageLimitRes> response) {
						if (leverageMap == null) {
							leverageMap = new SparseArray();
						}
						leverageMap.clear();
						maxLevel = 0;
						if (response.body() != null && response.body().getData() != null) {
							for( int i =0 ;i < response.body().getData().size(); i++) {
								LeverageLimitRes.DataBean dataBean = response.body().getData().get(i);
								for (int j = dataBean.getLevelMin(); j <= dataBean.getLevelMax(); j++) {
									leverageMap.put(j, dataBean.getQuantityLimit());
									maxLevel = j > maxLevel ? j : maxLevel;
								}
							}
							if (leverageMap.size() > 0) {
								initSeekBar();
							}
						}
					}

					@Override
					public void onE(Response<LeverageLimitRes> response) {

					}
				});
	}

	private void initSeekBar() {
		//初始化滑动条
		mSeekBar.setMax(maxLevel);
		mTvMinLevel.setText("1X");
		mTvMaxLevel.setText(String.format("%dX", maxLevel));

		mTvCurrentLevel.setText(currentLevel.replace("X","") + "X");
		mSeekBar.setProgress(Tools.parseFloat(currentLevel.replace("X","")));

		mSeekBar.setOnSeekChangeListener(new OnSeekChangeListener() {
			@Override
			public void onSeeking(SeekParams seekParams) {
				if (seekParams.progress <= 1) {
					mTvCurrentLevel.setText("1X");
				} else {
					mTvCurrentLevel.setText(String.format("%dX", seekParams.progress));
				}
				if (mLevelChangeListener != null) {
					mLevelChangeListener.onLevelChange(seekParams.progress >= 1 ? seekParams.progress : 1);
				}
			}

			@Override
			public void onStartTrackingTouch(IndicatorSeekBar seekBar) {

			}

			@Override
			public void onStopTrackingTouch(IndicatorSeekBar seekBar) {

			}
		});
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		OkGo.getInstance().cancelTag(this);
		leverageMap.clear();
	}

	public interface LevelChangeListener {
		void onLevelChange(int level);
	}
}
