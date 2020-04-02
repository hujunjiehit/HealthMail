package com.coinbene.manbiwang.market.widget;

import android.content.Context;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coinbene.common.Constants;
import com.coinbene.manbiwang.market.R;
import com.coinbene.manbiwang.market.R2;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Create by huyong
 * on 2018/7/30
 */
public class AscDescTextView extends LinearLayout implements View.OnClickListener {
    @BindView(R2.id.tv_sort_name)
    TextView tvSortName;
    @BindView(R2.id.iv_sort)
    ImageView ivSort;
    @BindView(R2.id.sort_root_layout)
    RelativeLayout sortLayout;
    private boolean isFirstDown = true;
    private int stutes = 0;//0 默认状态   1 向下  2 向上
    private String type;//排序方式

    private int sortType = 0;// 0是币种   1是24小时   2 是最新价   3是涨跌幅
    private OnClickSortType onClickSortType;


    public int getSortType() {
        return sortType;
    }

    public void setSortType(int sortType) {
        this.sortType = sortType;
    }

    public AscDescTextView(Context context) {
        super(context);
        initView();
    }

    public AscDescTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public AscDescTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void initView() {
        View view = LayoutInflater.from(this.getContext()).inflate(R.layout.textview_asc_desc, null);
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_VERTICAL;
        AscDescTextView.this.addView(view, params);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
//        tvSortName.setOnClickListener(this);
//        ivSort.setOnClickListener(this);
        sortLayout.setOnClickListener(this);
    }

    public boolean isFirstDown() {
        return isFirstDown;
    }

    public void setFirstDown(boolean firstDown) {
        isFirstDown = firstDown;
    }


    private void setStutesView() {
        if (stutes == 0) {
            ivSort.setImageResource(R.drawable.icon_sort_default);
        } else if (stutes == 1) {
            ivSort.setImageResource(R.drawable.icon_sort_down);
        } else if (stutes == 2) {
            ivSort.setImageResource(R.drawable.icon_sort_up);
        }
    }

    public TextView getTvSortName() {
        return tvSortName;
    }

    public int getStutes() {
        return stutes;
    }

    public void setStutes(int stutes) {
        this.stutes = stutes;
    }

    public void clearStutes() {
        stutes = 0;
        setStutesView();
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sort_root_layout) {
            if (stutes == 0) {//币种信息第一次点击是向上排序  其他第一次是向下
                if (isFirstDown) {
                    stutes = 1;
                } else {
                    stutes = 2;
                }
            } else if (stutes == 1) {
                if (isFirstDown) {
                    stutes = 2;
                } else {
                    stutes = 0;
                }
            } else if (stutes == 2) {
                if (isFirstDown) {
                    stutes = 0;
                } else {
                    stutes = 1;
                }
            }
            setStutesView();
            onClickSortType.OnSortType(getSortType());
        }
    }

    public void setOnClickSortType(OnClickSortType onClickSortType) {
        this.onClickSortType = onClickSortType;
    }

    public interface OnClickSortType {
        void OnSortType(int sortType);

    }

    public String getType() {
        if (stutes == 0) {
            type = Constants.SORT_BY_SORT;
        } else if (stutes == 1) {
            type = Constants.SORT_DESC;
        } else if (stutes == 2) {
            type = Constants.SORT_ASC;
        }
        return type;
    }

}
