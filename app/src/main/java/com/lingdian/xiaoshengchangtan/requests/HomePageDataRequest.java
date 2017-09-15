package com.lingdian.xiaoshengchangtan.requests;

import android.util.Log;

import com.lingdian.xiaoshengchangtan.utils.HtmlPageUrlUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

/**
 * Created by lingdian on 17/9/12.
 * 解析home网页中 的数据 找到我们的数据列表
 */

public class HomePageDataRequest extends BaseRequest<String>{

    public void startRequest(int pageInex){

        String homeUrl= HtmlPageUrlUtils.getPageUrlByIndex(pageInex);

        OkGo.<String>post(homeUrl).tag(this).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                onParserSuccess(HomePageDataRequest.this,response);
            }
            @Override
            public void onCacheSuccess(Response<String> response) {
                super.onCacheSuccess(response);
                onParserSuccess(HomePageDataRequest.this,response);
//                arrayList.clear();
//                arrayList.addAll(HtmlParer.dealFileListResult(response));
            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
                int code = response.code();
                Log.v("xhw", "onError");

                if (callback!=null){
                    callback.onDataError(HomePageDataRequest.this,response.code(),response.message());
                }

            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (callback!=null){
                    callback.onDataFinish(HomePageDataRequest.this);
                }
            }
        });
    }

    @Override
    public void onParserSuccess(BaseRequest request,Response<String> response) {
        if (callback!=null){
            callback.onDataSuccess(request,response.body());
        }
    }
}
