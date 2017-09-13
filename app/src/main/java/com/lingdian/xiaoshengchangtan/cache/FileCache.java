package com.lingdian.xiaoshengchangtan.cache;

import android.os.Environment;

import com.lingdian.xiaoshengchangtan.config.SwitchConfig;

import java.io.File;

/**
 * Created by lingdian on 17/9/11.
 * 文件缓存路径
 */

public class FileCache {

    private static FileCache cache = null;
    private String filePath= Environment.getExternalStorageDirectory()+ SwitchConfig.PATH_DIR;
    private FileCache() {

    }
    public static FileCache getInstance() {
        if (cache == null) {
            cache = new FileCache();
        }
        return cache;
    }

    public File getFilePath(){
        File file=new File(filePath);
        if(!file.exists()){
            file.mkdirs();
        }

        return file;
    }

}
