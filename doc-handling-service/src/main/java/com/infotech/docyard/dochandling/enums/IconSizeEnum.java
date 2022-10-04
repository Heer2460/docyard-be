package com.infotech.docyard.dochandling.enums;

import java.util.HashMap;

public enum IconSizeEnum {
    SMALL("small"),
    MEDIUM("medium"),
    LARGE("large"),
    EXTRA_LARGE("extra-large");

    // static Map
    private static HashMap<String, IconSizeEnum> iconSizeMap = new HashMap<String, IconSizeEnum>();

    static {
        for (IconSizeEnum e : values())
            iconSizeMap.put(e.getIconSize(), e);
    }


    public final static IconSizeEnum DEFAULT = MEDIUM;

    private IconSizeEnum(String iconSize) {
        setIconSize(iconSize);
    }

    private String iconSize;

    private void setIconSize(String iconSize) {
        this.iconSize = iconSize;
    }

    // public methods

    public String getIconSize() {
        return iconSize;
    }

    public static final IconSizeEnum getByValue(String iconSize) {
        return iconSizeMap.containsKey(iconSize) ? iconSizeMap.get(iconSize) : DEFAULT;
    }
}
