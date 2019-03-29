package com.luna.keyboardlibrary;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.luna.keyboardlibrary.annotation.KeyBoartType;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @description: ${DESP}
 * @author: chenjun
 * @project: SafeKeyboard-master
 * @date: 2018/11/23
 * @time: 10:58
 */
public class KeyboardManager {

    private static final String TAG = "KeyboardManager";

    private KeyboardManager() {
        if (SingletonHolder.instance != null) {
            throw new IllegalStateException();
        }
    }

    private static class SingletonHolder {
        private static KeyboardManager instance = new KeyboardManager();
    }

    public static KeyboardManager getInstance() {
        return SingletonHolder.instance;
    }

    private Map<String, SafeKeyboard> keyMap = new HashMap();
    //软键盘类型
    public static final String TYPE_NUMBER = "1";               //数字键盘，只能输入数字
    public static final String TYPE_TEXT_CAP_CHARACTERS = "2";
    public static final String TYPE_CUSTOM_NUMBER_TEXT = "4";   //数字字母键盘，数字字母
    public static final String TYPE_CUSTOM_NUMBER = "5";        //数字键盘，只能输入数字和小数点
    public static final String TYPE_NUMBER_PASSWORD = "6";      //密码框
    public static final String TYPE_TEXT_OR_NUMBER = "7";       //数字切换与字母键盘可切换，数字字母

    /*
     *键盘弹出context
     *  KEY_ACTIVITY 用于activity
     *  key_dialog 用于dialog
     */
    public static final String KEY_ACTIVITY = "key_activity";   //
    public static final String KEY_DIALOG = "key_dialog";

    public void initUI(Activity mContext, @KeyBoartType.TypeMode String flag) {
        ViewGroup layout = (ViewGroup) mContext.getWindow().getDecorView().findViewById(android.R.id.content);
        SafeKeyboard safeKeyboard = new SafeKeyboard(mContext, layout);
        safeKeyboard.initConfing(TYPE_CUSTOM_NUMBER_TEXT);
        initConfig(mContext, safeKeyboard);
        keyMap.put(flag, safeKeyboard);
    }

    public void initDialog(Dialog dialog, int resLayoutID, Context mContext, int width, @KeyBoartType.TypeMode String flag) {
        View view = LayoutInflater.from(mContext).inflate(resLayoutID, null);
        initDialog(dialog, view, mContext, width, flag);
    }

    public void initDialog(Dialog dialog, View v, Context mContext, int width, String flag) {
        LinearLayout.LayoutParams mKeyboardContainerLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        mKeyboardContainerLayoutParams.gravity = Gravity.BOTTOM;
        LinearLayout linearLayout = new LinearLayout(mContext);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setGravity(Gravity.CENTER);

        dialog.getWindow().setContentView(linearLayout, mKeyboardContainerLayoutParams);
        linearLayout.addView(v);
        try {
            ViewGroup.LayoutParams lp = linearLayout.getLayoutParams();
            lp.width = width;
//            lp.height = height;
            linearLayout.setLayoutParams(lp);
        } catch (Exception e) {
            e.printStackTrace();
        }

        SafeKeyboard safeKeyboard = new SafeKeyboard(mContext, linearLayout);
        safeKeyboard.initConfing(TYPE_CUSTOM_NUMBER_TEXT);
        initConfig(mContext, safeKeyboard);
        keyMap.put(flag, safeKeyboard);
    }

    /**
     * 数字键盘
     */
    public void numberKeyboard(EditText safeEdit, @KeyBoartType.TypeMode String flag) {
        if (keyMap.get(flag) != null) {
            keyMap.get(flag).setKeyboardType(TYPE_CUSTOM_NUMBER, safeEdit);
        }
    }

    /**
     * 字母数字键盘
     */
    public void abcNumberKeyboard(EditText safeEdit, @KeyBoartType.TypeMode String flag) {
        if (keyMap.get(flag) != null) {
            keyMap.get(flag).setKeyboardType(TYPE_CUSTOM_NUMBER_TEXT, safeEdit);
        }
    }

    /**
     * 字母数字切换键盘
     */
    public void abcOrNumberKeyboard(EditText safeEdit, @KeyBoartType.TypeMode String flag) {
        if (keyMap.get(flag) != null) {
            keyMap.get(flag).setKeyboardType(TYPE_TEXT_OR_NUMBER, safeEdit);
        }
    }

    /**
     * 字符键盘
     */
    public void symbolKeyboard(EditText safeEdit, @KeyBoartType.TypeMode String flag) {
        if (keyMap.get(flag) != null) {
            keyMap.get(flag).setKeyboardType(TYPE_TEXT_CAP_CHARACTERS, safeEdit);
        }
    }

    /**
     * 显示键盘
     *
     * @return
     */
    public void showKeyboard(@KeyBoartType.TypeMode String flag) {
        if (keyMap.get(flag) != null && !keyMap.get(flag).isShow()) {
            keyMap.get(flag).showKeyboard();
        }
    }

    /**
     * 隐藏键盘
     *
     * @return
     */
    public void hideKeyboard(@KeyBoartType.TypeMode String flag) {
        if (keyMap.get(flag) != null && keyMap.get(flag).isShow()) {
            keyMap.get(flag).hideKeyboard();
        }
    }

    /**
     * 键盘是否显示
     * @return true 输入法未显示
     */
    public boolean isShowKeyboard(@KeyBoartType.TypeMode String flag) {
        if (keyMap.get(flag) != null && !keyMap.get(flag).isShow()) {
            return true;
        }
        return false;
    }

    /**
     * 输入法当前要输入的输入框提示
     * @param tips
     */
    public void setKeyboardTips(String tips, @KeyBoartType.TypeMode String flag) {
        if (keyMap.get(flag) != null) {
            keyMap.get(flag).setTvTips(tips);
        }
    }
    /**
     * 输入法配置
     *
     * @param context
     * @param safeKeyboard
     */
    private void initConfig(Context context, SafeKeyboard safeKeyboard) {
        safeKeyboard.setDelDrawable(context.getResources().getDrawable(R.drawable.icon_del));
        safeKeyboard.setLowDrawable(context.getResources().getDrawable(R.drawable.icon_capital_default));
        safeKeyboard.setUpDrawable(context.getResources().getDrawable(R.drawable.icon_capital_selected));
    }

    public void cleanKeyBoard() {
        keyMap.clear();
    }

    public static void hideSystemSoftKeyboard(EditText editText) {
        int sdkInt = Build.VERSION.SDK_INT;
        if (sdkInt >= 11) {
            try {
                Class<EditText> cls = EditText.class;
                Method setShowSoftInputOnFocus;
                setShowSoftInputOnFocus = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
                setShowSoftInputOnFocus.setAccessible(true);
                setShowSoftInputOnFocus.invoke(editText, false);
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            editText.setInputType(InputType.TYPE_NULL);
        }
    }

}
