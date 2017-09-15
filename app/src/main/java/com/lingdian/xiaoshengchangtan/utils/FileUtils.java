package com.lingdian.xiaoshengchangtan.utils;

import com.lingdian.xiaoshengchangtan.cache.FileCache;

import java.io.File;

/**
 * Created by lingdian on 17/9/12.
 */

public class FileUtils {

    public static String getFilePath(String fileName){
        String fileDir= FileCache.getInstance().getDownloadFile().getAbsolutePath();
        return new File(fileDir,fileName).getAbsolutePath();
    }
}
