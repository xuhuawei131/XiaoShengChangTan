package com.lingdian.xiaoshengchangtan.cache;

import com.lzy.okgo.callback.FileCallback;

/**
 * Created by lingdian on 17/9/15.
 */

public abstract class MyFileCallback extends FileCallback {


    public MyFileCallback(String fileName){
        super(FileCache.getInstance().getDownloadPath(),fileName);
    }

}
