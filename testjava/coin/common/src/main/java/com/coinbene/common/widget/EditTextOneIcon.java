package com.coinbene.common.widget;

import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.coinbene.common.R;
import com.coinbene.common.R2;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mengxiangdong on 2017/12/6.
 */

public class EditTextOneIcon extends LinearLayout implements View.OnClickListener {
    @BindView(R2.id.name_input)
    EditText inputText;
    @BindView(R2.id.close_view)
    ImageView rightView;

    public EditTextOneIcon(Context context) {
        super(context);
        initLayout();
    }

    public EditTextOneIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
        initLayout();
    }

    public EditTextOneIcon(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initLayout();
    }

    private void initLayout() {
        View view = LayoutInflater.from(this.getContext()).inflate(R.layout.common_edittext_oneicon_layout, null);
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_VERTICAL;
        EditTextOneIcon.this.addView(view, params);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
        initListener();
    }

    public void setHintText(String hintText) {
        inputText.setHint(hintText);
    }

    private void initListener() {
        if (isIconEye) {
            rightView.setBackgroundResource(R.drawable.close_eye);
        } else {
            rightView.setBackgroundResource(R.drawable.icon_close);
        }

        rightView.setVisibility(View.GONE);
        inputText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (isIconEye) {
                    return;
                }
                if (editable != null && editable.length() > 0) {
                    rightView.setVisibility(View.VISIBLE);
                } else {
                    rightView.setVisibility(View.GONE);
                }
            }
        });
        inputText.setInputType(InputType.TYPE_CLASS_TEXT);
        rightView.setOnClickListener(this);
    }

    public void setFirstRightIcon(int resId) {
        rightView.setImageResource(resId);
    }

    public void setFirstRightIconVisible(int visible) {
        rightView.setVisibility(visible);
    }

    public void setRightViewPwdEye() {
        isIconEye = true;
        inputText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        rightView.setBackgroundResource(R.drawable.close_eye);
        rightView.setVisibility(View.VISIBLE);
    }

    public void setInputTypeNumer() {
        inputText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);
    }

    public boolean isIconEye;
    public boolean isOpenEye;

    public EditText getInputText() {
        return inputText;
    }

    public void setCloseState(int i) {
         rightView.setVisibility(i);
    }

    public String getInputStr() {
        return inputText.getText().toString();
    }
    public void setInputText(String text) {
        inputText.setText(text);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.close_view) {
            if (isIconEye) {
                isOpenEye = !isOpenEye;
                rightView.setImageResource(isOpenEye ? R.drawable.open_eye : R.drawable.close_eye);
                String inputStr = inputText.getText().toString();
                if (isOpenEye) {
                    inputText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    inputText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                inputText.setSelection(inputStr.length());
            } else {
                inputText.setText("");
            }
        }
    }
}
