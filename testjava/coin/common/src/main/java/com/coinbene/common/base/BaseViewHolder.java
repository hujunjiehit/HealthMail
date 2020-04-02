package com.coinbene.common.base;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.coinbene.common.widget.CircleProgressBar;

/**
 * @author ding
 */
public class BaseViewHolder extends RecyclerView.ViewHolder {

	private SparseArray<View> mViews;
	public View itemView;


	public BaseViewHolder(View itemView) {
		super(itemView);
		mViews = new SparseArray<>();
		this.itemView = itemView;
	}

	public <T extends View> T getView(int id) {
		View view = mViews.get(id);
		if (view == null) {
			view = itemView.findViewById(id);
			mViews.put(id, view);
		}
		return (T) view;
	}

	public void setText(int id, String text) {
		TextView view = getView(id);
		view.setText(text);
	}

	public void setTypeface(int id, int resourceId) {
		TextView view = getView(id);
		view.setTypeface(ResourcesCompat.getFont(view.getContext(), resourceId));
	}

	public void setText(int id, int textResource) {
		TextView view = getView(id);
		view.setText(textResource);
	}


	public String getText(int id) {
		TextView view = getView(id);
		return view.getText().toString();
	}

	public void setTextColor(int Id, String color) {
		TextView view = getView(Id);
		view.setTextColor(Color.parseColor(color));
	}

	public void setTextColor(int id, int colorRes) {
		TextView view = getView(id);
		int color = view.getContext().getResources().getColor(colorRes);
		view.setTextColor(color);
	}

	public void setBackGroundResource(int id, int resId) {
		View view = getView(id);
		view.setBackgroundResource(resId);
	}

	public void setBackGroundColor(int id, int color) {
		View view = getView(id);
		view.setBackgroundColor(color);
	}

	public void setBackGroundDrable(int id, Drawable drawable) {
		View view = getView(id);
		view.setBackground(drawable);
	}

	public void setImage(int id, int resId) {
		ImageView imageView = getView(id);
		imageView.setImageResource(resId);
	}

	public void setImageDrawable(int id, Drawable drawable) {
		ImageView imageView = getView(id);
		imageView.setImageDrawable(drawable);
	}

	public void setVisibility(int id, int visbility) {
		getView(id).setVisibility(visbility);
	}

	public void setItemBackGroud(int color) {
		itemView.setBackgroundColor(color);
	}

	public void setChecked(int tb_bind_status, boolean b) {
		ToggleButton toggleButton = getView(tb_bind_status);
		toggleButton.setChecked(b);
	}

	public void setProgress(int id, int pregress) {
		CircleProgressBar progressBar = getView(id);
		progressBar.setProgress(pregress);
	}

	public void setProgressStartColor(int id, int resColor) {
		CircleProgressBar progressBar = getView(id);
		int color = progressBar.getContext().getResources().getColor(resColor);
		progressBar.setProgressStartColor(color);
	}

	public void setProgressEndColor(int id, int resColor) {
		CircleProgressBar progressBar = getView(id);
		int color = progressBar.getContext().getResources().getColor(resColor);
		progressBar.setProgressEndColor(color);
	}

	public void setProgressTextColor(int id, int resColor) {
		CircleProgressBar progressBar = getView(id);
		int color = progressBar.getContext().getResources().getColor(resColor);
		progressBar.setProgressTextColor(color);
	}

	public void setEnabled(int redId, boolean b) {
		View view = getView(redId);
		view.setEnabled(b);
	}
}
