package com.lingdian.xiaoshengchangtan.utils;

import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

/**
 * Created by lingdian on 17/9/10.
 */

public class HtmlParer {

    /**
     * 获取某个详情页面的下载地址
     * @param html 网页html的所有数据元素
     * @return 获取这个页面 音频的下载地址
     */
    public static String getPageDownFile(String html){
        String fileUrl=null;
        StringReader readerStr=new StringReader(html);
        BufferedReader reader=new BufferedReader(readerStr);
        String strLine;
        try {
            while ((strLine=reader.readLine())!=null){
                if(strLine.lastIndexOf(".mp4")!=-1){
                    fileUrl =getDownLoadUrl(strLine);
                    break;
                }
            }
            reader.close();
            return fileUrl;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    private static String getDownLoadUrl(String srcStr){
        if(TextUtils.isEmpty(srcStr)){
            return null;
        }

        int startIndex=srcStr.indexOf("\'");
        int lastIndex=srcStr.lastIndexOf("\'");

        if(lastIndex>startIndex){
            return srcStr.substring(startIndex+1,lastIndex);
        }
        return null;
    }

}
