package com.sagar.jewellery.model.enums;

public enum GoldPurity {
    K14(0.585, "14 Karat"),
    K18(0.750, "18 Karat"),
    K22(0.916, "22 Karat"),
    K24(0.999, "24 Karat");

    private final double fineness;
    private final String displayName;

    GoldPurity(double fineness, String displayName) {
        this.fineness = fineness;
        this.displayName = displayName;
    }

    public double getFineness() {
        return fineness;
    }

    public String getDisplayName() {
        return displayName;
    }
}
