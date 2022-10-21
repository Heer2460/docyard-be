package com.infotech.docyard.dochandling.util;

import org.apache.commons.lang.StringUtils;

public class TestMain {

    public static void main(String args[]) {

        if(StringUtils.containsAny("jan:","/\\:*?\"<>|")){
            System.out.println(true);
        } else {
            System.out.println(false);
        }

    }
}
