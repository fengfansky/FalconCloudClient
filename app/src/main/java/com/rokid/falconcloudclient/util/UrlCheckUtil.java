package com.rokid.falconcloudclient.util;

import android.webkit.URLUtil;

import java.net.URI;

/**
 * Created by fanfeng on 2017/7/7.
 */

public class UrlCheckUtil {

    public static final String HTTP_SCHEME = "http";

    public static URI packageUrl(URI uri){
        if (URLUtil.isHttpsUrl(uri.toString()) && URLUtil.isHttpUrl(uri.toString())){
            return uri;
        }else {
            return URI.create(HTTP_SCHEME + "://" + uri.toString());
        }
    }

}
