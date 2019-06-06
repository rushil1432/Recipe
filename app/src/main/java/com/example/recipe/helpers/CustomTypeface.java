package com.example.recipe.helpers;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;

public class CustomTypeface {

    public SpannableString setTypeFace(Context context,String value) {
        Typeface font = Typeface.createFromAsset(context.getAssets(), "fonts/raleway_semibold.ttf");
        SpannableString str = new SpannableString(value);
        str.setSpan(new CustomTypefaceSpan("", font), 0, str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return str;
    }

}
