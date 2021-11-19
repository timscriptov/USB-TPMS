package com.tpms.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SupportMultipleScreensUtil {
    public static final int BASE_SCREEN_HEIGHT = 600;
    public static final float BASE_SCREEN_HEIGHT_FLOAT = 600.0f;
    public static final int BASE_SCREEN_WIDTH = 1024;
    public static final float BASE_SCREEN_WIDTH_FLOAT = 1024.0f;
    public static float scale = 1.0f;

    public static void init(Context context) {
        scale = ((float) context.getResources().getDisplayMetrics().widthPixels) / 1024.0f;
    }

    public static int getScaleValue(int i) {
        return (int) Math.ceil((double) (scale * ((float) i)));
    }

    public static void scale(View view) {
        if (view == null) {
            return;
        }
        if (view instanceof ViewGroup) {
            scaleViewGroup((ViewGroup) view);
        } else {
            //scaleView(view);
        }
    }

    private static void scaleViewGroup(ViewGroup viewGroup) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View childAt = viewGroup.getChildAt(i);
            if (childAt instanceof ViewGroup) {
                scaleViewGroup((ViewGroup) childAt);
            }
            //scaleView(childAt);
        }
    }

    /*private static void scaleView(View view) {
        Object tag = view.getTag(R.id.is_scale_size_tag);
        if (!(tag instanceof Boolean) || !((Boolean) tag).booleanValue()) {
            if (view instanceof TextView) {
                scaleTextView((TextView) view);
            } else {
                scaleViewSize(view);
            }
            view.setTag(R.id.is_scale_size_tag, true);
        }
    }*/

    public static void scaleViewSize(View view) {
        if (view != null) {
            view.setPadding(getScaleValue(view.getPaddingLeft()), getScaleValue(view.getPaddingTop()), getScaleValue(view.getPaddingRight()), getScaleValue(view.getPaddingBottom()));
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            if (layoutParams != null) {
                if (layoutParams.width > 0) {
                    layoutParams.width = getScaleValue(layoutParams.width);
                }
                if (layoutParams.height > 0) {
                    layoutParams.height = getScaleValue(layoutParams.height);
                }
                if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
                    ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) layoutParams;
                    int scaleValue = getScaleValue(marginLayoutParams.topMargin);
                    int scaleValue2 = getScaleValue(marginLayoutParams.leftMargin);
                    int scaleValue3 = getScaleValue(marginLayoutParams.bottomMargin);
                    int scaleValue4 = getScaleValue(marginLayoutParams.rightMargin);
                    marginLayoutParams.topMargin = scaleValue;
                    marginLayoutParams.leftMargin = scaleValue2;
                    marginLayoutParams.bottomMargin = scaleValue3;
                    marginLayoutParams.rightMargin = scaleValue4;
                }
            }
            view.setLayoutParams(layoutParams);
        }
    }

    private static void setTextViewCompoundDrawables(TextView textView, Drawable drawable, Drawable drawable2, Drawable drawable3, Drawable drawable4) {
        if (drawable != null) {
            scaleDrawableBounds(drawable);
        }
        if (drawable3 != null) {
            scaleDrawableBounds(drawable3);
        }
        if (drawable2 != null) {
            scaleDrawableBounds(drawable2);
        }
        if (drawable4 != null) {
            scaleDrawableBounds(drawable4);
        }
        textView.setCompoundDrawables(drawable, drawable2, drawable3, drawable4);
    }

    public static Drawable scaleDrawableBounds(Drawable drawable) {
        drawable.setBounds(0, 0, getScaleValue(drawable.getIntrinsicWidth()), getScaleValue(drawable.getIntrinsicHeight()));
        return drawable;
    }

    /*public static void scaleTextView(TextView textView) {
        if (textView != null) {
            scaleViewSize(textView);
            Object tag = textView.getTag(R.id.is_scale_font_tag);
            if (!(tag instanceof Boolean) || !((Boolean) tag).booleanValue()) {
                textView.setTextSize(0, textView.getTextSize() * scale);
            }
            Drawable[] compoundDrawables = textView.getCompoundDrawables();
            setTextViewCompoundDrawables(textView, compoundDrawables[0], compoundDrawables[1], compoundDrawables[2], compoundDrawables[3]);
            textView.setCompoundDrawablePadding(getScaleValue(textView.getCompoundDrawablePadding()));
        }
    }*/
}
