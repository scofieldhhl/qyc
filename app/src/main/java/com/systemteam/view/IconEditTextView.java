package com.systemteam.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.systemteam.R;
import com.systemteam.util.LogTool;

/**
 * 类描述：
 * 创建人：Administrator
 * 创建时间：2016/6/6 15:30
 */
public class IconEditTextView extends FrameLayout implements View.OnFocusChangeListener {

    private ImageView mIcon;
    private EditText mInput;
    private ImageButton mDelete;
    private boolean enable = true;
    private boolean focusable = true;
    private int mMaxLength;
    private Context mContext;
    private boolean isHideIcon;
    private boolean isPhoneNum, isNumeric = false;

    public IconEditTextView(Context context) {
        super(context);
        mContext = context;
    }

    public IconEditTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.widget_icon_edittext, this, true);

        mIcon = (ImageView) findViewById(R.id.custom_widget_icon);
        mInput = (EditText) findViewById(R.id.custom_widget_input);
        mDelete = (ImageButton) findViewById(R.id.custom_widget_delete);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.IconEditTextView);
        Drawable drawable = a.getDrawable(R.styleable.IconEditTextView_edtIcon);
        CharSequence hint = a.getText(R.styleable.IconEditTextView_edtHint);
        CharSequence text = a.getText(R.styleable.IconEditTextView_text);
        isHideIcon = a.getBoolean(R.styleable.IconEditTextView_isHideIcon, false);
        boolean isIconGone = a.getBoolean(R.styleable.IconEditTextView_isIconGone, false);
        enable = a.getBoolean(R.styleable.IconEditTextView_enable, true);
        focusable = a.getBoolean(R.styleable.IconEditTextView_focusable, true);
        mMaxLength = a.getInt(R.styleable.IconEditTextView_maxLength, 150);
        isPhoneNum = a.getBoolean(R.styleable.IconEditTextView_isPhoneNum, false);
        isNumeric = a.getBoolean(R.styleable.IconEditTextView_isNumeric, false);
        a.recycle();

        if (drawable != null) {
            mIcon.setImageDrawable(drawable);
        }
        if (!TextUtils.isEmpty(hint)) {
            mInput.setHint(hint);
        }
        if (!TextUtils.isEmpty(text)) {
            mInput.setText(text);
        }
        if (isHideIcon) {
            mIcon.setVisibility(INVISIBLE);
        }
        if (isIconGone) {
            mIcon.setVisibility(GONE);
        }
        if (enable) {
            mInput.setEnabled(true);
            mInput.setFocusable(true);
        } else {
            mInput.setEnabled(false);
            mDelete.setVisibility(GONE);
            mInput.setBackgroundResource(R.drawable.bg_edittext_uneditable);
        }
        if (focusable) {
            mInput.setFocusable(true);
        } else {
            mInput.setFocusable(false);
            mDelete.setVisibility(GONE);
            mInput.setBackgroundResource(R.drawable.bg_edittext_uneditable);
        }
        mInput.setSingleLine();
        mInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(mMaxLength)});
        if (isPhoneNum) {
            mInput.setInputType(InputType.TYPE_CLASS_PHONE);//电话
        }
        else if(isNumeric){
            mInput.setInputType(InputType.TYPE_CLASS_NUMBER);//数字
        }
    }

    public IconEditTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mDelete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mInput.setText("");
            }
        });
        mInput.setOnFocusChangeListener(this);
        mInput.addTextChangedListener(new TextWatcher() {
            InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                imm.showSoftInput(mInput, InputMethodManager.SHOW_FORCED);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (enable || focusable) {
                    if (enable) {
                        if (s.length() > 0) {
                            mDelete.setVisibility(VISIBLE);
                        } else {
                            mDelete.setVisibility(GONE);
                        }
                    }
                } else {
                    mInput.setBackgroundResource(R.drawable.bg_edittext_uneditable);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    public String getInputText() {
        return mInput.getText().toString().trim();
    }

    public void setInputType(int type) {
        mInput.setInputType(type);
    }

    public void setText(String text) {
        mInput.setText(text);
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
        if (enable) {
            mInput.setFocusable(true);
            mInput.setEnabled(true);
        } else {
            mInput.setEnabled(false);
            mInput.setFocusable(false);
            mDelete.setVisibility(GONE);
            mInput.setBackgroundResource(R.drawable.bg_edittext_uneditable);
        }
        invalidate();
    }

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        LogTool.d("onFocusChange: " + hasFocus);
        if (hasFocus) {
            imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
        } else {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0); //强制隐藏键盘
        }
    }

    public void setIsHideIcon(boolean isHideIcon) {
        this.isHideIcon = isHideIcon;
        if (isHideIcon) {
            mIcon.setVisibility(INVISIBLE);
        } else {
            mIcon.setVisibility(VISIBLE);
        }
        invalidate();
    }

    public void setMaxLength(int maxLength) {
        if (mInput != null) {
            mInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
        }
    }
}
