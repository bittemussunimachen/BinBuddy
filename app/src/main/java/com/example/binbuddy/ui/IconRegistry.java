package com.example.binbuddy.ui;

import androidx.annotation.DrawableRes;

import com.example.binbuddy.R;

/**
 * Central registry for mapping semantic icon keys to drawable resources.
 */
public final class IconRegistry {

    public static final String ICON_ECO_SCORE = "eco_score";
    public static final String ICON_PACKAGING = "packaging";
    public static final String ICON_PALM_OIL = "palm_oil";
    public static final String ICON_CARBON = "carbon";
    public static final String ICON_WARNING = "warning";

    private IconRegistry() {
        // Utility class
    }

    @DrawableRes
    public static int getIconRes(String key) {
        if (key == null) {
            return R.drawable.ic_warning;
        }

        switch (key) {
            case ICON_ECO_SCORE:
                return R.drawable.ic_environment;
            case ICON_PACKAGING:
                return R.drawable.ic_recycle;
            case ICON_PALM_OIL:
                return R.drawable.ic_palm;
            case ICON_CARBON:
                return R.drawable.ic_environment;
            default:
                return R.drawable.ic_warning;
        }
    }
}
