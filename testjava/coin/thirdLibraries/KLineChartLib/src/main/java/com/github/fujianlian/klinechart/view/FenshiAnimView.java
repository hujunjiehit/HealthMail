package com.github.fujianlian.klinechart.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.github.fujianlian.klinechart.R;
import com.github.fujianlian.klinechart.utils.ViewUtil;

public class FenshiAnimView extends AbsoluteLayout {

	private ImageView bigOval;
	private ImageView smallOval;
	private LayoutParams bigOvalParams;
	private LayoutParams smallOvalParams;

	private AnimatorSet curAnimation;
	private long curTimeStamp = -1;

	private float lastX;
	private float lastY;

	public FenshiAnimView(@NonNull Context context) {
		super(context);
		bigOval = new ImageView(getContext());
		bigOval.setImageDrawable(getResources().getDrawable(R.drawable.big_oval));
		bigOval.setVisibility(View.GONE);
		smallOval = new ImageView(getContext());
		smallOval.setImageDrawable(getResources().getDrawable(R.drawable.small_oval));
		smallOval.setVisibility(View.GONE);

		bigOvalParams = new LayoutParams(
				LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0, 0);
		smallOvalParams = new LayoutParams(
				LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0, 0);
		addView(bigOval, bigOvalParams);
		addView(smallOval, smallOvalParams);

	}

	public void updateLocation(float x, float y) {
		if (lastX == x && lastY == y) {
			return;
		}

		LayoutParams bigParams = bigOvalParams;
		//移动位置
		bigParams.x = (int) (x - ViewUtil.Dp2Px(getContext(), 10)/2);
		bigParams.y = (int) (y - ViewUtil.Dp2Px(getContext(), 10)/2);
		bigOval.requestLayout();

		LayoutParams samllParams = smallOvalParams;
		//移动位置
		samllParams.x = (int) (x - ViewUtil.Dp2Px(getContext(), 5)/2);
		samllParams.y = (int) (y - ViewUtil.Dp2Px(getContext(), 5)/2);
		smallOval.requestLayout();

		lastX = x;
		lastY = y;

		if (curAnimation != null && curAnimation.isRunning()) {
			return;
		}

		showAnimation(x, y);
	}


	public void showAnimation(float x, float y) {

		//如果当前正在动画，先取消
		//dismissCurrentAnimation();

		//动画
		curAnimation = new AnimatorSet();

		ObjectAnimator alpha = ObjectAnimator.ofFloat(bigOval, "alpha", 1.0f, 0.0f);
		alpha.setRepeatCount(ValueAnimator.INFINITE);//无限循环
		ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(smallOval, "scaleX", 1f, 0.8f, 1f);
		ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(smallOval, "scaleY", 1f, 0.8f, 1f);
		scaleXAnimator.setRepeatCount(ValueAnimator.INFINITE);
		scaleYAnimator.setRepeatCount(ValueAnimator.INFINITE);
		curAnimation.play(alpha).with(scaleXAnimator).with(scaleYAnimator);
		curAnimation.setDuration(1000);
		curAnimation.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationCancel(Animator animation) {

			}

			@Override
			public void onAnimationEnd(Animator animation) {
			}

			@Override
			public void onAnimationStart(Animator animation) {
				setVisibility(View.VISIBLE);
				bigOval.setVisibility(View.VISIBLE);
				smallOval.setVisibility(VISIBLE);
			}
		});
		curAnimation.start();
	}


	public void dismissCurrentAnimation() {
		//不需要重复调
		if (curAnimation == null && getVisibility() == View.GONE) {
			return;
		}
		if (curAnimation != null) {
			curAnimation.cancel();
			curAnimation = null;
		}
		curTimeStamp = -1;
		lastX = -1;
		lastY = -1;
		hideView();
	}

	private void hideView() {
		smallOval.setVisibility(View.GONE);
		bigOval.setVisibility(View.GONE);
		setVisibility(View.GONE);
	}

}
