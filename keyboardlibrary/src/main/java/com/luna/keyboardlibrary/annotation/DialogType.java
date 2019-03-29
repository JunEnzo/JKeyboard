package com.luna.keyboardlibrary.annotation;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @description: 一注解的方式规定Dialog种类
 * @author: chenjun
 * @project: ticket_20180731
 * @date: 2019/2/21
 * @time: 14:53
 */
public final class DialogType {
    public static final int TYPE_NORMAL         = 0;  //普通弹框
    public static final int TYPE_EDIT           = 1;    //编辑框
    public static final int TYPE_PROGRESSBAR    = 3;    //加载框

    @IntDef({TYPE_NORMAL, TYPE_EDIT, TYPE_PROGRESSBAR})
    @Retention(RetentionPolicy.SOURCE)
    public @interface TypeMode{
    }
}
