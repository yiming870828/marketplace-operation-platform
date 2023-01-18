package com.sothebys.payment.utile;


import org.apache.commons.lang3.StringUtils;

public class PaymentUtil {

    public static String getPBLId(String url) {
        return StringUtils.substringAfterLast(url, "/");
    }


}
