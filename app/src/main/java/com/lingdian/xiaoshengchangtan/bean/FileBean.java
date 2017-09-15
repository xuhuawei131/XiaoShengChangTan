package com.lingdian.xiaoshengchangtan.bean;

import com.lingdian.xiaoshengchangtan.cache.FileCache;

import java.io.File;

/**
 * Created by lingdian on 17/9/12.
 */

public class FileBean {
    private static final String URL="http://live.jlradio.net:80/VodFiles//am738/%s/2000007200_mp4/200000_7200_543210K.mp4";

    public String fileUrl;

    public String fileName;
    public String filePath;

    public String fileDownName;
    public String fileDownPath;

    public static FileBean checkData(String fileTitle){
        String array[]=fileTitle.split("_");
        if(array.length>1){
            FileBean bean=new FileBean();
            bean.fileUrl=String.format(URL,array[1]);

            bean.fileName=fileTitle+".mp4";;
            bean.filePath=getFilePath(bean.fileName);

            bean.fileDownName=fileTitle+"_.mp4";
            bean.fileDownPath=getFilePath(bean.fileDownName);
            return bean;
        }
        return null;
    }

    private static String getFilePath(String fileName){
        String fileDir= FileCache.getInstance().getDownloadFile().getAbsolutePath();
        return new File(fileDir,fileName).getAbsolutePath();
    }
}
