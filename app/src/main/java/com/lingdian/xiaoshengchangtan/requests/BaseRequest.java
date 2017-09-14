package com.lingdian.xiaoshengchangtan.requests;

import com.lingdian.xiaoshengchangtan.requests.callback.OnBaseCallback;
import com.lzy.okgo.model.Response;

/**
 * Created by lingdian on 17/9/14.
 */

public abstract class BaseRequest<T> {
    protected OnBaseCallback<T> callback;

    public  void  setDataCallback(OnBaseCallback<T> callback){
        this.callback=callback;
    }

    protected abstract void onParserSuccess(BaseRequest request,Response<String> response);

}
