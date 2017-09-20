package com.lingdian.xiaoshengchangtan.customview;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.lingdian.xiaoshengchangtan.R;

/**
 * Created by lingdian on 17/9/20.
 */

public class MyMenuDialog  extends Dialog {
    public MyMenuDialog(@NonNull Context context) {
        super(context,R.style.my_dialog);
    }

    public MyMenuDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    protected MyMenuDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.setCanceledOnTouchOutside(true);
    }

    public void showDialog(){
        LinearLayout root = (LinearLayout) LayoutInflater.from(getContext()).inflate(
                R.layout.layout_menu_item, null);
        root.findViewById(R.id.btn_open_camera).setOnClickListener(btnlistener);
        root.findViewById(R.id.btn_choose_img).setOnClickListener(btnlistener);
        root.findViewById(R.id.btn_cancel).setOnClickListener(btnlistener);
        this.setContentView(root);
        Window dialogWindow = getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        dialogWindow.setWindowAnimations(R.style.dialogstyle); // 添加动画
        WindowManager.LayoutParams lp = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        lp.x = 0; // 新位置X坐标
        lp.y = -20; // 新位置Y坐标
        lp.width = (int) getContext().getResources().getDisplayMetrics().widthPixels; // 宽度
//      lp.height = WindowManager.LayoutParams.WRAP_CONTENT; // 高度
//      lp.alpha = 9f; // 透明度
        root.measure(0, 0);
        lp.height = root.getMeasuredHeight();
        lp.alpha = 9f; // 透明度
        dialogWindow.setAttributes(lp);
        show();
    }
private View.OnClickListener btnlistener=new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        if (isShowing()) {
            dismiss();
        }
    }
};
}
