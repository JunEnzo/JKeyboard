package com.luna.keyboardlibrary.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.luna.keyboardlibrary.KeyboardManager;
import com.luna.keyboardlibrary.annotation.KeyBoartType;

/**
 * @description: 自定义软键盘绑定dialog的基类
 * @author: chenjun
 * @project: ticket_20180731
 * @date: 2019/2/26
 * @time: 9:16
 */
public abstract class KeyBoardDialog extends Dialog {

    public KeyBoardDialog(@NonNull Context context) {
        super(context);
    }

    public KeyBoardDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected KeyBoardDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(bindLayoutView());
        initKeyboard(this.getContext(), bindLayoutView());
        initView();
        bindKeyboard(bindEditView());
        bindLayoutParams();
    }

    /**
     * 初始化布局
     * @return
     */
    protected abstract void initView();

    /**
     * 布局View
     * @return
     */
    protected abstract View bindLayoutView();

    /**
     * 绑定输入框
     * @return
     */
    protected abstract EditText bindEditView();

    /**
     * 设置输入框宽高
     */
    protected abstract void bindLayoutParams();

    /**
     * 初始化dialog绑定自定义软键盘
     * @param context
     * @param view      布局view
     */
    private void initKeyboard(Context context, View view) {
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        float density = dm.density;
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        KeyboardManager.getInstance().initDialog(this, view, context, width, KeyboardManager.KEY_DIALOG);
    }

    /**
     * 初始化dialog绑定自定义软键盘
     * @param context
     * @param resLayoutID   布局layoutID
     */
    private void initKeyboard(Context context, int resLayoutID) {
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        float density = dm.density;
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        KeyboardManager.getInstance().initDialog(this, resLayoutID, context, width, KeyBoartType.KEY_DIALOG);
    }

    /**
     * 输入框绑定软键盘
     * @param editText
     */
    @SuppressLint("ClickableViewAccessibility")
    protected void bindKeyboard(final EditText editText){
        if (editText != null) {
            editText.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    KeyboardManager.getInstance().abcNumberKeyboard(editText, KeyBoartType.KEY_DIALOG);
                    return false;
                }
            });
        }
    }

}
