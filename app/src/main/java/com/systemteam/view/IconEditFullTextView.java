package com.systemteam.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import com.systemteam.R;

/**
 */
public class IconEditFullTextView extends FrameLayout {

    private ImageView mIcon;
    private EditText mInput;
    private ImageButton mDelete;

    public IconEditFullTextView(Context context) {
        super(context);
    }

    public IconEditFullTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.widget_icon_edittext_full, this, true);

        mIcon = (ImageView) findViewById(R.id.custom_widget_icon);
        mInput = (EditText) findViewById(R.id.custom_widget_input);
        mDelete = (ImageButton) findViewById(R.id.custom_widget_delete);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.IconEditTextView);
        Drawable drawable = a.getDrawable(R.styleable.IconEditTextView_edtIcon);
        CharSequence hint = a.getText(R.styleable.IconEditTextView_edtHint);
        boolean isHideIcon = a.getBoolean(R.styleable.IconEditTextView_isHideIcon, false);
        a.recycle();

        if (drawable != null) {
            mIcon.setImageDrawable(drawable);
        }
        if (!TextUtils.isEmpty(hint)) {
            mInput.setHint(hint);
        }
        if (isHideIcon) {
            mIcon.setVisibility(INVISIBLE);
//            mInput.setPadding(0, 0, 0, 0);
        }
    }

    public IconEditFullTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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
        mInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    mDelete.setVisibility(VISIBLE);
                } else {
                    mDelete.setVisibility(GONE);
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

    public void clearPadding() {
        mInput.setPadding(0, 0, 0, 0);
    }

    public void setMaxLength(int maxLength) {
        mInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
    }
}
