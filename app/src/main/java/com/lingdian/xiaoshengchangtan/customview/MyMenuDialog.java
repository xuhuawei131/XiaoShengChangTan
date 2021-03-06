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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lingdian.xiaoshengchangtan.R;

/**
 * Created by lingdian on 17/9/20.
 * http://blog.csdn.net/zouzhigang96/article/details/50454111
 */

public class MyMenuDialog extends Dialog {
    private TextView image_down;
    public MyMenuDialog(@NonNull Context context) {
        super(context, R.style.my_dialog);
    }

    public MyMenuDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    protected MyMenuDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.setCanceledOnTouchOutside(true);
    }


    public void showDialog(boolean isDowned) {
        LinearLayout root = (LinearLayout) LayoutInflater.from(getContext()).inflate(
                R.layout.layout_menu_item, null);
        root.findViewById(R.id.btn_close).setOnClickListener(btnlistener);
        root.findViewById(R.id.image_collect).setOnClickListener(btnlistener);
        image_down= (TextView) root.findViewById(R.id.image_down);
        image_down.setOnClickListener(btnlistener);
        root.findViewById(R.id.image_timer).setOnClickListener(btnlistener);
        root.findViewById(R.id.image_feedback).setOnClickListener(btnlistener);

        if (isDowned){
            image_down.setCompoundDrawablesWithIntrinsicBounds(0,
                    R.drawable.ic_play_pop_downloaded,0,0);
        }else{
            image_down.setCompoundDrawablesWithIntrinsicBounds(0,
                    R.drawable.ic_play_pop_download,0,0);
        }


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

    private View.OnClickListener btnlistener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (isShowing()) {
                dismiss();
            }
            if(onDialogItemClick==null){
                return;
            }
            if (v.getId() == R.id.image_down) {
                onDialogItemClick.onDialogItem(1);
            } else if (v.getId() == R.id.image_timer) {
                onDialogItemClick.onDialogItem(2);
            } else if (v.getId() == R.id.image_collect) {
                onDialogItemClick.onDialogItem(0);
            } else if (v.getId() == R.id.image_feedback) {
                onDialogItemClick.onDialogItem(3);
            }
        }
    };
    private OnDialogItemClick onDialogItemClick;

    public void setOnDialogItemClick(OnDialogItemClick click) {
        this.onDialogItemClick = click;
    }

    public interface OnDialogItemClick {
        public void onDialogItem(int index);
    }

}
