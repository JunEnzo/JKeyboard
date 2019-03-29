package com.luna.keyboardlibrary.annotation;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @description: 注解的方式规定键盘弹出的context
 * @author: chenjun
 * @project: ticket_20180731
 * @date: 2019/3/8
 * @time: 12:50
 */
public class KeyBoartType {
    public static final String KEY_ACTIVITY = "key_activity";   //
    public static final String KEY_DIALOG = "key_dialog";

    @StringDef({KEY_ACTIVITY, KEY_DIALOG})
    @Retention(RetentionPolicy.SOURCE)
    public @interface TypeMode{
    }
}
