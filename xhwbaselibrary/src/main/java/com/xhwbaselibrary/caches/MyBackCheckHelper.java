package com.xhwbaselibrary.caches;

import com.xhwbaselibrary.backgound.MyLifecycleHandler;

/**
 * Created by lingdian on 17/9/25.
 * 检测前台助手
 */

public class MyBackCheckHelper {
    private static MyBackCheckHelper instance=null;
    private MyLifecycleHandler handler;

    private MyBackCheckHelper(){
        handler=new MyLifecycleHandler();
        MyAppContext.getInstance().getApplication().registerActivityLifecycleCallbacks(handler);
    }

    public static MyBackCheckHelper getInstance(){
        if(instance==null){
            instance=new MyBackCheckHelper();
        }
        return instance;
    }

    /**
     * 应用是否可见
     * @return
     */
    public boolean isApplicationVisible(){
        if(handler!=null){
            return handler.isApplicationVisible();
        }else{
            return false;
        }
    }

    /**
     * 应用是否在前台
     * @return
     */
    public boolean isApplicationInForeground(){
        if(handler!=null){
            return handler.isApplicationInForeground();
        }else{
            return false;
        }
    }

}
