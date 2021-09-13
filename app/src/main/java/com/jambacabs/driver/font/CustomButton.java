package com.jambacabs.driver.font;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatButton;

import com.jambacabs.driver.R;

public class CustomButton extends AppCompatButton
{
    private TypeFactory mFontFactory;
    public CustomButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyCustomFont(context, attrs);
    }

    private void applyCustomFont(Context context, AttributeSet attrs) {

        int typefaceType;
        TypedArray array = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.custom_text,
                0, 0);
        try {
            typefaceType = array.getInteger(R.styleable.custom_text_name, 0);
        } finally {
            array.recycle();
        }
        if (!isInEditMode()) {
            setTypeface(getTypeFace(typefaceType));
        }

    }

    public Typeface getTypeFace(int type) {
        if (mFontFactory == null)
            mFontFactory = new TypeFactory(getContext());

        if(type == 1)
        {
            return mFontFactory.bold;
        }else
        {
            return mFontFactory.regular;
        }
    }
}
