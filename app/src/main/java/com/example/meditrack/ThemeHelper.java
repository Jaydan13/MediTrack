package com.example.meditrack;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.core.content.ContextCompat;

public class ThemeHelper {

    public static void applyTheme(Activity activity) {

        int colour = activity.getSharedPreferences("SettingsPrefs", Context.MODE_PRIVATE)
                .getInt("appColourValue", ContextCompat.getColor(activity, R.color.blue));

        View root = activity.findViewById(android.R.id.content);
        applyToViews(root, colour);
    }

    private static void applyToViews(View view, int colour) {

        Object tag = view.getTag();

        // Only apply if tag = "appTheme"
        if ("appTheme".equals(tag)) {

            // BUTTONS → change tint
            if (view instanceof Button) {
                Button btn = (Button) view;

                int id = btn.getId();

                // Skip delete buttons
                if (id != R.id.deleteRemindBtn && id != R.id.deleteStockBtn) {
                    btn.setBackgroundTintList(ColorStateList.valueOf(colour));
                }
            }

            // LAYOUTS → change background
            else if (view instanceof LinearLayout) {
                view.setBackgroundColor(colour);
            }
        }

        // Loop through children
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                applyToViews(group.getChildAt(i), colour);
            }
        }
    }
}