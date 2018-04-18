package com.lingdian.xiaoshengchangtan.bean;

import com.lingdian.xiaoshengchangtan.cache.FileCache;

import java.io.File;

/**
 * Created by lingdian on 17/9/12.
 */

public class FileBean {
    //    private static final String URL="http://live.jlradio.net:80/VodFiles//am738/%s/2000007200_mp4/200000_7200_543210K.mp4";
    private static final String URL = "http://live.jlradio.net:80/VodFiles//am738/%s/1200005400_mp4/120000_5400_543210K.mp4";

    public String fileUrl;

    public String fileName;
    public String filePath;

    public String fileDownName;
    public String fileDownPath;

    public static FileBean newInstance(String fileTitle) {
        String array[] = fileTitle.split("_");
        if (array.length > 1) {
            FileBean bean = new FileBean();
//            bean.fileUrl = String.format(URL, array[1]);//不使用本地拼接的方式了 而使用网络获取下载地址方式

            bean.fileName = fileTitle + ".mp4";
            ;
            bean.filePath = getFilePath(bean.fileName);

            bean.fileDownName = fileTitle + "_.mp4";
            bean.fileDownPath = getFilePath(bean.fileDownName);
            return bean;
        }
        return null;
    }

    private static String getFilePath(String fileName) {
        String fileDir = FileCache.getInstance().getDownloadFile().getAbsolutePath();
        return new File(fileDir, fileName).getAbsolutePath();
    }

    /**
     * 文件是否存在
     *
     * @return
     */
    public boolean isExistFile() {
        if (new File(this.filePath).exists()) {
            return true;
        } else {
            return false;
        }
    }

}
