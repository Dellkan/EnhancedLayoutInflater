package com.dellkan.enhanced_layout_inflater;

public class Utils {
    public static boolean equals(Object first, Object second) {
        if (first == null && second == null) {
            return true;
        }
        if (first != null) {
            return first.equals(second);
        }
        return false;
    }
}
