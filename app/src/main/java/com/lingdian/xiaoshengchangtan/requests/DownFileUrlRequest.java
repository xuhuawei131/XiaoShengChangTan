package com.lingdian.xiaoshengchangtan.requests;

import android.util.Log;

import com.lingdian.xiaoshengchangtan.utils.HtmlParer;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

/**
 * Created by lingdian on 17/9/12.
 * 获取网页中  要下载文件的地址
 */

public class DownFileUrlRequest {
    public void startRequest(String url){

            OkGo.<String>post(url).tag(this).execute(new StringCallback() {
                @Override
                public void onSuccess(Response<String> response) {
                    dealPageResult(response);
                }

                @Override
                public void onCacheSuccess(Response<String> response) {
                    super.onCacheSuccess(response);
                    dealPageResult(response);
                }

                @Override
                public void onError(Response<String> response) {
                    super.onError(response);
                    int code = response.code();
                    Log.v("xhw", "onError");
                }

                @Override
                public void onFinish() {
                    super.onFinish();
                    Log.v("xhw", "onFinish");
                }
            });

}
    private void dealPageResult(Response<String> response) {
        Log.v("xhw", "onSuccess");
        String html = response.body();
//        InputStream inputStream=response.getRawResponse().body().byteStream();
        String fileUrl = HtmlParer.getPageDownFile(html);
    }
}
