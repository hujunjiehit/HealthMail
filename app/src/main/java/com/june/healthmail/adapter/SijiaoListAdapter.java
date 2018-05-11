package com.june.healthmail.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.june.healthmail.R;
import com.june.healthmail.model.SijiaoInfo;

import java.util.HashMap;
import java.util.List;

/**
 * Created by june on 2017/12/13.
 */

public class SijiaoListAdapter extends BaseAdapter {

  private List<SijiaoInfo> mSijiaoInfos;
  private Context mContext;
  private LayoutInflater mInflater;
  private Callback mCallback;

  //用来记录所有ListView记录对应checkbox的状态
  public HashMap<Integer, Integer> selectedMap;

  public SijiaoListAdapter(Context context, List<SijiaoInfo> sijiaoInfos) {
    this.mSijiaoInfos = sijiaoInfos;
    this.mContext = context;
    this.mInflater = LayoutInflater.from(context);
    selectedMap = new HashMap<Integer, Integer>();
  }

  public void setCallback(Callback callback) {
    mCallback = callback;
  }

  @Override
  public int getCount() {
    return mSijiaoInfos == null ? 0 : mSijiaoInfos.size();
  }

  @Override
  public Object getItem(int position) {
    return mSijiaoInfos.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    ViewHolder viewHolder;
    if (convertView == null) {
      convertView = mInflater.inflate(R.layout.item_sijiao_info, parent, false);
      viewHolder = new ViewHolder(convertView);
      convertView.setTag(viewHolder);
    } else {
      viewHolder = (ViewHolder) convertView.getTag();
    }
    fillData(viewHolder, position);
    addListener(viewHolder,position);
    return  convertView;
  }

  private void addListener(ViewHolder viewHolder, final int position) {
    viewHolder.layoutItem.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (mCallback != null) {
          mCallback.onItemClick(position);
        }
      }
    });

    viewHolder.layoutItem.setOnLongClickListener(new View.OnLongClickListener() {

      @Override
      public boolean onLongClick(View v) {
        if (mCallback != null) {
          mCallback.onItemLongClick(position);
          return true;
        }
        return false;
      }
    });
  }

  private void fillData(ViewHolder viewHolder, int position) {
    SijiaoInfo sijiaoInfo = mSijiaoInfos.get(position);
    viewHolder.mTvIndex.setText((position + 1) + "");
    viewHolder.mTvMallId.setText(sijiaoInfo.getMallId());
    viewHolder.mTvName.setText(sijiaoInfo.getName());

    if (selectedMap.containsKey(position) && sijiaoInfo.isSelected()) {
      viewHolder.mCbSelected.setChecked(true);
    } else {
      if(sijiaoInfo.isSelected()) {
        viewHolder.mCbSelected.setChecked(true);
        if (!selectedMap.containsKey((Integer) viewHolder.mCbSelected.getTag())) {
          selectedMap.put((Integer) viewHolder.mCbSelected.getTag(),position);
        }
      } else {
        viewHolder.mCbSelected.setChecked(false);
        if (selectedMap.containsKey(viewHolder.mCbSelected.getTag())) {
          selectedMap.remove((Integer) viewHolder.mCbSelected.getTag());
        }
      }
    }
  }

  class ViewHolder {
    View layoutItem;
    TextView mTvIndex;
    TextView mTvMallId;
    TextView mTvName;
    CheckBox mCbSelected;

    ViewHolder(View itemView) {
      layoutItem = itemView.findViewById(R.id.layout_item);
      mTvIndex = (TextView) itemView.findViewById(R.id.tv_index);
      mTvMallId = (TextView) itemView.findViewById(R.id.tv_mall_id);
      mTvName = (TextView) itemView.findViewById(R.id.tv_name);
      mCbSelected = (CheckBox) itemView.findViewById(R.id.cb_status);
    }
  }

  public interface Callback {
    void onItemClick(int position);
    void onItemLongClick(int position);
  }
}
