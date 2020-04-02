package com.coinbene.common.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.coinbene.common.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ding
 * 基础适配器
 */
public abstract class BaseAdapter<T> extends RecyclerView.Adapter<BaseViewHolder> {

	/**
	 * FooterView 类型
	 */
	public final int LOOK_FOOTER_VIEW = 0x00000123;

	public List<T> list;
	public Context mContext;
	public int item;

	protected static final int HEADER_VIEW = 0x00000111;
	protected static final int ITEM_VIEW = 0x00000222;
	protected static final int FOOTER_VIEW = 0x00000333;
	protected static final int EMPTY_VIEW = 0x00000555;

	// 正在加载
	public static final int LOADING = 1;
	// 加载完成
	public static final int LOADING_COMPLETE = 2;
	// 加载结束
	public static final int LOADING_END = 3;

	/**
	 * 加载状态 默认加载完成
	 */
	public int state = LOADING;

	public BaseAdapter() {

	}

	public BaseAdapter(int item) {
		this.item = item;
	}

	@NonNull
	@Override
	public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

		this.mContext = parent.getContext();

		BaseViewHolder holder = null;

		switch (viewType) {
			case EMPTY_VIEW:
				holder = new BaseViewHolder(LayoutInflater.from(mContext).inflate(R.layout.common_base_empty, parent, false));
				return holder;


			case FOOTER_VIEW:
				holder = new BaseViewHolder(LayoutInflater.from(mContext).inflate(R.layout.common_recycle_footer, parent, false));
				return holder;


			default:
				holder = new BaseViewHolder(LayoutInflater.from(mContext).inflate(item, parent, false));
				return holder;
		}

	}

	@Override
	public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {

		int ViewType = holder.getItemViewType();

		switch (ViewType) {
			case EMPTY_VIEW:
				break;

			case FOOTER_VIEW:
				hideFooter(holder);
				footerSetting(holder);
				break;

			case ITEM_VIEW:
				convert(holder, position, list.get(position));
				break;

			default:

		}

	}

	/**
	 * @param holder 存在FooterView设置
	 */
	private void footerSetting(@NonNull BaseViewHolder holder) {
		switch (state) {

			case LOADING:
				holder.setVisibility(R.id.loading, View.VISIBLE);
				holder.setVisibility(R.id.loading_text, View.VISIBLE);
				break;

			case LOADING_COMPLETE:
//                加载完成动画效果
//                holder.setText(R.id.loading_end_text, "加载完成");
//                holder.getView(R.id.loading_end_layout).animate()
//                        .alpha(0)
//                        .setDuration(500)
//                        .setListener(new Animator.AnimatorListener() {
//                            @Override
//                            public void onAnimationStart(Animator animation) {
//                                holder.setVisibility(R.id.loading_end_layout, View.VISIBLE);
//                            }
//
//                            @Override
//                            public void onAnimationEnd(Animator animation) {
//                                holder.setVisibility(R.id.loading_end_layout, View.GONE);
//                            }
//
//                            @Override
//                            public void onAnimationCancel(Animator animation) {
//
//                            }
//
//                            @Override
//                            public void onAnimationRepeat(Animator animation) {
//
//                            }
//                        })
//                        .start();
				break;

			case LOADING_END:
				holder.setVisibility(R.id.loading_end_layout, View.VISIBLE);
				break;

			default:
		}
	}


	/**
	 * @param holder 隐藏footer
	 */
	private void hideFooter(BaseViewHolder holder) {
		holder.setVisibility(R.id.loading, View.GONE);
		holder.setVisibility(R.id.loading_text, View.GONE);
		holder.setVisibility(R.id.loading_end_layout, View.GONE);
	}


	@Override
	public int getItemCount() {
		return list == null || list.size() == 0 ? 1 : list.size() + 1;
	}


	@Override
	public int getItemViewType(int position) {

		if (list == null || list.size() == 0) {
			return EMPTY_VIEW;
		}

		if (getItemCount() > 1 && position + 1 == getItemCount()) {
			return FOOTER_VIEW;
		}

		//如果有数据，则使用ITEM的布局
		return ITEM_VIEW;
	}


	/**
	 * @param item 设置数据
	 */
	public void setItem(List<T> item) {
		this.list = item;
		notifyDataSetChanged();
	}


	public List<T> getList() {
		return list;
	}

	/**
	 * @param data 追加数据
	 */
	public void appendItem(List<T> data) {
		if (data != null && data.size() > 0) {
			if (list == null) {
				list = new ArrayList<>();
			}
			this.list.addAll(data);
			notifyDataSetChanged();
		}
	}


	/**
	 * @param holder
	 * @param item     当前条目
	 * @param position 当前条目下标
	 */
	protected abstract void convert(BaseViewHolder holder, int position, T item);

	/**
	 * @param state 加载状态
	 */
	public void setState(int state) {
		this.state = state;
		notifyDataSetChanged();
	}


	/**
	 * 清空数据
	 */
	public void clear() {
		if (list != null) {
			list.clear();
			list = null;
			notifyDataSetChanged();
		}
	}


	public int getState(){
		return state;
	}


}
