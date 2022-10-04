package com.infotech.docyard.gateway.util;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public class AppUtility {

    public static boolean isEmpty(Object object) {
        if (object == null) {
            return true;
        }
        if (object instanceof Long) {
            if ((Long) object <= 0) {
                return true;
            }
        }
        if (object instanceof Double) {
            if ((Double) object <= 0) {
                return true;
            }
        }
        if (object instanceof Optional<?> && !((Optional<?>) object).isPresent()) {
            return true;
        }
        if (object instanceof String) {
            String objString = object.toString();
            return objString.trim().length() <= 0 || objString.trim().equalsIgnoreCase("null");
        } else if (object instanceof StringBuilder) {
            StringBuilder stringBuilder = (StringBuilder) object;
            return stringBuilder.toString().trim().length() <= 0;
            // Check for List and Sets
        } else if (object instanceof Collection<?>) {
            return ((Collection) object).isEmpty();
        } else if (object instanceof Map<?, ?>) {
            return ((Map) object).isEmpty();
        } else if (object instanceof Boolean[]) {
            return ((Boolean[]) object).length <= 0;
        }
        return false;
    }

}
