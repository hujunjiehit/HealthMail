package com.coinbene.common.widget;

import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
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

public class EditTextTwoIcon extends LinearLayout implements View.OnClickListener {
    @BindView(R2.id.name_input)
    EditText inputText;
    @BindView(R2.id.close_view)
    ImageView closeView;
    @BindView(R2.id.down_view)
    ImageView rightView;

//    @BindView(R.id.test)
//    Button textBtn;

    public EditTextTwoIcon(Context context) {
        super(context);
        initLayout();
    }

    public EditTextTwoIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
        initLayout();
    }

    public EditTextTwoIcon(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initLayout();
    }

    private void initLayout() {
        View view = LayoutInflater.from(this.getContext()).inflate(R.layout.common_edittext_twoicon_layout, null);
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_VERTICAL;
        EditTextTwoIcon.this.addView(view, params);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
        initListener();
    }

    private void initListener() {
        closeView.setImageResource(R.drawable.icon_close);
        rightView.setImageResource(R.drawable.close_eye);
        closeView.setVisibility(View.GONE);
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
                if (editable != null && editable.length() > 0) {
                    closeView.setVisibility(View.VISIBLE);
//                    textBtn.setVisibility(VISIBLE);
                } else {
                    closeView.setVisibility(View.GONE);
//                    textBtn.setVisibility(GONE);
                }
            }
        });
        closeView.setOnClickListener(this);
        rightView.setOnClickListener(this);
    }

    public void setFirstRightIcon(int resId) {
        closeView.setImageResource(resId);
    }

    public void setSecondRightIcon(int resId) {
        rightView.setImageResource(resId);
    }

    public void setSecondRightIconVisible(int visible) {
        rightView.setVisibility(visible);
    }

    public void setFirstRightIconVisible(int visible) {
        closeView.setVisibility(visible);
    }

    public boolean isIconEye;
    public boolean isOpenEye;

    public void setSecondRightPwdEye() {
        isIconEye = true;
        isOpenEye = false;
        rightView.setVisibility(VISIBLE);
        rightView.setImageResource(R.drawable.close_eye);
        inputText.setSingleLine();
        inputText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
    }

    public void setSecondRightPwdEyeHint() {
        isIconEye = true;
        isOpenEye = false;
        rightView.setVisibility(VISIBLE);
        rightView.setImageResource(R.drawable.close_eye);
        inputText.setSingleLine();
        inputText.setHint(R.string.cap_pwd_hint);
        inputText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
    }

    public boolean isInputNumType;

    public void setSecondRightEye_Num() {
        isIconEye = true;
        isOpenEye = false;
        rightView.setVisibility(VISIBLE);
        rightView.setImageResource(R.drawable.close_eye);

        inputText.setSingleLine();
        isInputNumType = true;
        inputText.setInputType(EditorInfo.TYPE_CLASS_NUMBER | EditorInfo.TYPE_NUMBER_VARIATION_PASSWORD);
    }

    public void setSecondRightDown() {
        isIconEye = false;
        rightView.setImageResource(R.drawable.down_arrow);
    }

    public EditText getInputText() {
        return inputText;
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
            inputText.setText("");
        } else if (v.getId() == R.id.down_view) {
            if (isIconEye) {
                isOpenEye = !isOpenEye;
                rightView.setImageResource(isOpenEye ? R.drawable.open_eye : R.drawable.close_eye);
                String inputStr = inputText.getText().toString();
                if (isOpenEye) {
                    if (isInputNumType) {
                        inputText.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
                    } else {
                        inputText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    }
                } else {
                    if (isInputNumType) {
                        inputText.setInputType(InputType.TYPE_CLASS_NUMBER | EditorInfo.TYPE_NUMBER_VARIATION_PASSWORD);
                    } else {
                        inputText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    }
                }
                inputText.setSelection(inputStr.length());
            } else {

            }
        }
    }
}
