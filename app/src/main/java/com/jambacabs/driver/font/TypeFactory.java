package com.jambacabs.driver.font;

import android.content.Context;
import android.graphics.Typeface;

class TypeFactory
{

    Typeface bold;
    Typeface regular;

     TypeFactory(Context context) {
        bold = Typeface.createFromAsset(context.getAssets(), "nexabold.otf");
        regular = Typeface.createFromAsset(context.getAssets(), "nexalight.otf");
    }
}
