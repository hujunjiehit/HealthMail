package com.coinbene.common.widget.app;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coinbene.common.R;
import com.coinbene.common.activities.adapter.TextSelectAdapter;
import com.coinbene.common.widget.ItemDivider;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class ListWithCancelSheetDialog extends BottomSheetDialog implements View.OnClickListener, TextSelectAdapter.SelectedListener {
    public ListWithCancelSheetDialog(@NonNull Context context) {
        this(context, R.style.CoinBene_BottomSheet);
        this.context = context;
    }

    public ListWithCancelSheetDialog(@NonNull Context context, int theme) {
        super(context, theme);
        this.context = context;
        initLayout();
    }

    private Context context;
    private Object object;
    private TextView cancelTv;
    private RecyclerView recyclerView;
    private TextSelectAdapter selectAdapter;
    private ListSheetDialogListener dialogListener;

    public void setTag(Object object) {
        this.object = object;
        if (selectAdapter != null) {
            selectAdapter.setTag(object);
        }
    }

    private void initLayout() {
        setCancelable(true);
        setContentView(R.layout.common_dialog_text_select);
        cancelTv = this.findViewById(R.id.text_select_cancel);
        recyclerView = this.findViewById(R.id.text_select_RecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        cancelTv.setOnClickListener(this);

        selectAdapter = new TextSelectAdapter(object);
        selectAdapter.setListener(this);
        recyclerView.setAdapter(selectAdapter);
        //设置分割线
        ItemDivider itemDivider = new ItemDivider(context);
        recyclerView.addItemDecoration(itemDivider);
    }

    public void setListSheetDialogListener(ListSheetDialogListener dialogListener) {
        this.dialogListener = dialogListener;
    }

    public void setDataAndNotifyData(String[] array) {
        selectAdapter.setData(array);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.text_select_cancel) {
            dismiss();
        } else {
        }
    }

    @Override
    public void show() {
        if (isShowing()) {
            dismiss();
        }
        super.show();
    }

    @Override
    public void selected(Object tag, String content, int position) {
        if (dialogListener != null) {
            dialogListener.onSelectedItem(object, position);
        }
        dismiss();
    }

    public void setSelectedPosition(int position) {
        selectAdapter.setSelectByPosition(position);
    }

    public interface ListSheetDialogListener {
        void onSelectedItem(Object tag, int position);
    }
}
