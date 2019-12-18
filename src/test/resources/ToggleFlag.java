package com.android.launcher3.config;

public class ToggleFlag {

    public String key;
    public String defaultValue;
    public String description;

    @Override
    public String toString() {
        return "TogglableFlag{"
                + "key=" + key + ", "
                + "defaultValue=" + defaultValue + ", "
                + "description=" + description
                + "}";
    }

}