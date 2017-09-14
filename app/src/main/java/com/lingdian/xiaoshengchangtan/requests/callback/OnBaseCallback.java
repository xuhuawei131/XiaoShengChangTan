package com.lingdian.xiaoshengchangtan.requests.callback;

import com.lingdian.xiaoshengchangtan.requests.BaseRequest;

/**
 * Created by lingdian on 17/9/14.
 */

public interface OnBaseCallback<T> {
    public void onDataSuccess(BaseRequest request,T result);
    public void onDataError(BaseRequest request,int code,String msg);
    public void onDataFinish(BaseRequest request);
}
