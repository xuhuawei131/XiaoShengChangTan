package com.lingdian.xiaoshengchangtan.utils;

/**
 * Created by lingdian on 17/9/14.
 */

public class HtmlPageUrlUtils
{
    private static final String  homeUrl1="http://gb.jlradio.net/misc/node_153_%d.shtml";
    private static final String  homeUrl="http://gb.jlradio.net/misc/node_153.shtml";

    public static String getPageUrlByIndex(int pageIndex){
        if(pageIndex==0){
            return homeUrl;
        }else{
            pageIndex++;
            return String.format(homeUrl1,pageIndex);
        }
    }

}
