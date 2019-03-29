package com.luna.keyboardlibrary;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Build;
import android.text.Editable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.luna.keyboardlibrary.util.Utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by Administrator on 2018/3/7 0007.
 */

public class SafeKeyboard {

    private static final String TAG = "SafeKeyboard";

    private Context mContext;               //上下文

    private ViewGroup layout;
    private View keyContainer;              //自定义键盘的容器View
    private SafeKeyboardView keyboardView;  //键盘的View
    private Keyboard keyboardABC;        //数字键盘
    private Keyboard keyboardNumber;        //数字键盘
    private Keyboard keyboardLetter;        //字母数字键盘或字母键盘
    private Keyboard keyboardSymbol;        //符号键盘
    private static boolean isCapes = true;
    private String keyboardType = KeyboardManager.TYPE_CUSTOM_NUMBER_TEXT;
    private String keyboardTypeSwitch = KeyboardManager.TYPE_CUSTOM_NUMBER_TEXT;
    private TextView done;
    private TextView numOrAbc;
    private TextView tvTips;

    SafeKeyboard(Context mContext, ViewGroup layout){
        this.mContext = mContext;
        this.layout = layout;
        FrameLayout.LayoutParams mKeyboardContainerLayoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup
                .LayoutParams.WRAP_CONTENT);
        mKeyboardContainerLayoutParams.gravity = Gravity.BOTTOM;

        keyContainer = LayoutInflater.from(mContext).inflate(R.layout.layout_keyboard_containor, null);
        keyContainer.setVisibility(View.GONE);

        keyboardView = (SafeKeyboardView) keyContainer.findViewById(R.id.safeKeyboardLetter);
        done = (TextView) keyContainer.findViewById(R.id.tv_complete);
        numOrAbc = (TextView) keyContainer.findViewById(R.id.tv_abc);
        tvTips = (TextView) keyContainer.findViewById(R.id.tv_tips);

        keyContainer.setPadding(Utils.dipToPx(mContext, 0),
                Utils.dipToPx(mContext, 0),
                Utils.dipToPx(mContext, 0),
                Utils.dipToPx(mContext, 0));
        if (layout.indexOfChild(keyContainer) == -1) {
            layout.addView(keyContainer, mKeyboardContainerLayoutParams);
        } else {
            keyContainer.setVisibility(View.VISIBLE);
        }
    }

    public void initConfing(String type) {
        initKeyboard(type);
    }

    /**
     *
     * @param type
     */
    @SuppressLint("ClickableViewAccessibility")
    private void initKeyboard(String type) {
        keyboardABC = new Keyboard(mContext, R.xml.keyboard_abc);         //实例化字母键盘
        keyboardSymbol = new Keyboard(mContext, R.xml.keyboard_symbol);         //实例化符号键盘
        keyboardNumber = new Keyboard(mContext, R.xml.keyboard_number);            //实例化数字键盘
        keyboardLetter = new Keyboard(mContext, R.xml.keyboard_letter);         //实例化字母数字键盘

        if (TextUtils.equals(KeyboardManager.TYPE_TEXT_CAP_CHARACTERS, type)) {
            keyboardView.setKeyboard(keyboardSymbol);
        } else if (TextUtils.equals(KeyboardManager.TYPE_CUSTOM_NUMBER_TEXT, type)) {
            keyboardView.setKeyboard(keyboardABC);
        } else if (TextUtils.equals(KeyboardManager.TYPE_CUSTOM_NUMBER, type)) {
            keyboardView.setKeyboard(keyboardNumber);
        } else if (TextUtils.equals(KeyboardManager.TYPE_TEXT_OR_NUMBER, type)) {
            keyboardView.setKeyboard(keyboardLetter);
        } else {
            keyboardView.setKeyboard(keyboardABC);
        }
        keyboardView.setEnabled(true);
        keyboardView.setPreviewEnabled(false);


        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isKeyboardShown()) {
                    hideKeyboard();
                }
            }
        });
        numOrAbc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 数字与字母键盘互换
                if (keyboardType == KeyboardManager.TYPE_CUSTOM_NUMBER) {
                    //当前为字母键盘
                    keyboardType = KeyboardManager.TYPE_TEXT_OR_NUMBER;
                } else {
                    //当前为数字键盘
                    keyboardType = KeyboardManager.TYPE_CUSTOM_NUMBER;
                }
                isCapes = true;
                changeKeyboardLetterCase();
                switchKeyboard();
            }
        });
        keyboardView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return event.getAction() == MotionEvent.ACTION_MOVE;
            }
        });
    }

    /**
     * 键盘状态监听
     * @param mEditText
     */
    private void initCallback(final EditText mEditText) {
        keyboardView.setOnKeyboardActionListener(new KeyboardView.OnKeyboardActionListener() {
            @Override
            public void onPress(int primaryCode) {
                if (keyboardType == KeyboardManager.TYPE_CUSTOM_NUMBER) {
                    keyboardView.setPreviewEnabled(false);
                } else {
                    keyboardView.setPreviewEnabled(true);
                    if (primaryCode == -1 || primaryCode == -5 || primaryCode == 32 || primaryCode == -2
                            || primaryCode == 100860 || primaryCode == -35) {
                        keyboardView.setPreviewEnabled(false);
                    } else if (primaryCode == -3) {
                        keyboardView.setPreviewEnabled(false);
                        if (isKeyboardShown()) {
                            hideKeyboard();
                        }
                    } else {
                        keyboardView.setPreviewEnabled(true);
                    }
                }
            }

            @Override
            public void onRelease(int primaryCode) {

            }

            @Override
            public void onKey(int primaryCode, int[] keyCodes) {
                try {
                    Editable editable = mEditText.getText();
                    int start = mEditText.getSelectionStart();
                    int end = mEditText.getSelectionEnd();
                    if (primaryCode == Keyboard.KEYCODE_CANCEL) {
                        // 隐藏键盘
                        hideKeyboard();
                    } else if (primaryCode == Keyboard.KEYCODE_DELETE || primaryCode == -35) {

                        // 回退键,删除字符
                        if (editable != null && editable.length() > 0) {
                            if (start == end) { //光标开始和结束位置相同, 即没有选中内容
                                editable.delete(start - 1, start);
                            } else { //光标开始和结束位置不同, 即选中EditText中的内容
                                editable.delete(start, end);
                            }
                        }
                    } else if (primaryCode == Keyboard.KEYCODE_SHIFT) {
                        // 大小写切换
                        changeKeyboardLetterCase();
                        // 重新setKeyboard, 进而系统重新加载, 键盘内容才会变化(切换大小写)
                        keyboardType = keyboardTypeSwitch;
                        switchKeyboard();
                    } else if (primaryCode == Keyboard.KEYCODE_MODE_CHANGE) {
                        // 数字与字母键盘互换
                        if (keyboardType == KeyboardManager.TYPE_CUSTOM_NUMBER) { //当前为数字键盘
                            keyboardType = KeyboardManager.TYPE_CUSTOM_NUMBER_TEXT;
                        } else {        //当前不是数字键盘
                            keyboardType = KeyboardManager.TYPE_CUSTOM_NUMBER;
                        }
                        switchKeyboard();
                    } else if (primaryCode == 100860) {
                        // 字母与符号切换
                        if (keyboardType == KeyboardManager.TYPE_TEXT_CAP_CHARACTERS) { //当前是符号键盘
                            keyboardType = KeyboardManager.TYPE_CUSTOM_NUMBER_TEXT;
                        } else {        //当前不是符号键盘, 那么切换到符号键盘
                            keyboardType = KeyboardManager.TYPE_TEXT_CAP_CHARACTERS;
                        }
                        switchKeyboard();
                    } else {
                        // 输入键盘值
                        // editable.insert(start, Character.toString((char) primaryCode));
                        editable.replace(start, end, Character.toString((char) primaryCode));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onText(CharSequence text) {

            }

            @Override
            public void swipeLeft() {

            }

            @Override
            public void swipeRight() {

            }

            @Override
            public void swipeDown() {

            }

            @Override
            public void swipeUp() {

            }
        });

        layout.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                int hasMoved = 0;
                Object heightTag = layout.getTag(R.id.scroll_height_by_keyboard);
                if (heightTag != null) {
                    hasMoved = (int) heightTag;
                }
                if (keyContainer.getVisibility() == View.GONE) {
                    layout.removeOnLayoutChangeListener(this);
                    if (hasMoved > 0) {
                        layout.getChildAt(0).scrollBy(0, -1 * hasMoved);
                        layout.setTag(R.id.scroll_height_by_keyboard, 0);
                    }
                } else {
                    Rect rect = new Rect();
                    layout.getWindowVisibleDisplayFrame(rect);

                    int[] etLocation = new int[2];
                    mEditText.getLocationOnScreen(etLocation);
                    int keyboardTop = etLocation[1] + mEditText.getHeight() + mEditText.getPaddingTop() + mEditText.getPaddingBottom() + 1;   //1px is a divider
                    Object anchor = mEditText.getTag(R.id.anchor_view);
                    View mShowAnchorView = null;
                    if (anchor != null && anchor instanceof View) {
                        mShowAnchorView = (View) anchor;
                    }
                    if (mShowAnchorView != null) {
                        int[] saLocation = new int[2];
                        mShowAnchorView.getLocationOnScreen(saLocation);
                        keyboardTop = saLocation[1] + mShowAnchorView.getHeight() + mShowAnchorView.getPaddingTop() + mShowAnchorView   //1px is a divider
                                .getPaddingBottom() + 1;
                    }
                    int moveHeight = keyboardTop + keyContainer.getHeight() - rect.bottom;
                    //height > 0 rootview 需要继续上滑
                    if (moveHeight > 0) {
                        layout.getChildAt(0).scrollBy(0, moveHeight);
                        layout.setTag(R.id.scroll_height_by_keyboard, hasMoved + moveHeight);
                    } else {
                        int moveBackHeight = Math.min(hasMoved, Math.abs(moveHeight));
                        if (moveBackHeight > 0) {
                            layout.getChildAt(0).scrollBy(0, -1 * moveBackHeight);
                            layout.setTag(R.id.scroll_height_by_keyboard, hasMoved - moveBackHeight);
                        }
                    }

                }
            }
        });
    }

    /**
     * 键盘切换
     */
    private void switchKeyboard() {
        switch (keyboardType) {
            case KeyboardManager.TYPE_NUMBER:
                done.setVisibility(View.GONE);
                numOrAbc.setVisibility(View.GONE);
                keyboardView.setKeyboard(keyboardABC);
                break;
            case KeyboardManager.TYPE_TEXT_CAP_CHARACTERS:
                done.setVisibility(View.GONE);
                numOrAbc.setVisibility(View.GONE);
                keyboardView.setKeyboard(keyboardSymbol);
                break;
            case KeyboardManager.TYPE_CUSTOM_NUMBER_TEXT:
                done.setVisibility(View.GONE);
                numOrAbc.setVisibility(View.GONE);
                keyboardView.setKeyboard(keyboardABC);
                break;
            case KeyboardManager.TYPE_CUSTOM_NUMBER:
                done.setVisibility(View.VISIBLE);
                if (keyboardTypeSwitch == KeyboardManager.TYPE_CUSTOM_NUMBER) {
                    numOrAbc.setVisibility(View.GONE);
                } else {
                    numOrAbc.setVisibility(View.VISIBLE);
                }
                keyboardView.setKeyboard(keyboardNumber);
                break;
            case KeyboardManager.TYPE_TEXT_OR_NUMBER:
                done.setVisibility(View.GONE);
                numOrAbc.setVisibility(View.GONE);
                keyboardView.setKeyboard(keyboardLetter);
                break;
            default:
                done.setVisibility(View.GONE);
                numOrAbc.setVisibility(View.GONE);
                keyboardView.setKeyboard(keyboardABC);
                break;
        }
    }

    /**
     * 大小写切换
     */
    private void changeKeyboardLetterCase() {
        List<Keyboard.Key> keyList = null;
        switch (keyboardType) {
            case KeyboardManager.TYPE_CUSTOM_NUMBER_TEXT:
                keyList = keyboardABC.getKeys();
                break;
            case KeyboardManager.TYPE_TEXT_OR_NUMBER:
                keyList = keyboardLetter.getKeys();
                break;
            default:
                break;
        }
        if (keyList != null && keyList.size() > 0) {
            if (isCapes) {
                for (Keyboard.Key key : keyList) {
                    if (key.label != null && isUpCaseLetter(key.label.toString())) {
                        key.label = key.label.toString().toLowerCase();
                        key.codes[0] += 32;
                    }
                }
            } else {
                for (Keyboard.Key key : keyList) {
                    if (key.label != null && isLowCaseLetter(key.label.toString())) {
                        key.label = key.label.toString().toUpperCase();
                        key.codes[0] -= 32;
                    }
                }
            }
            isCapes = !isCapes;
            keyboardView.setCap(isCapes);
        }
    }

    /**
     * 隐藏键盘
     */
    public void hideKeyboard() {
        keyContainer.setVisibility(View.GONE);
        keyContainer.clearAnimation();
    }

    /**
     * 显示键盘
     */
    public void showKeyboard() {
//        switchKeyboard();
        keyContainer.setVisibility(View.VISIBLE);
        keyContainer.clearAnimation();
    }

    /**
     * 判断小写
     * @param str
     * @return
     */
    private boolean isLowCaseLetter(String str) {
        String letters = "abcdefghijklmnopqrstuvwxyz";
        return letters.contains(str);
    }

    /**
     * 判断大写
     * @param str
     * @return
     */
    private boolean isUpCaseLetter(String str) {
        String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        return letters.contains(str);
    }

    /**
     * 监听editview状态 获得焦点打开软键盘，否则隐藏
     * @param mEditText
     */
    @SuppressLint("ClickableViewAccessibility")
    private void addListeners(EditText mEditText) {
/*        mEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    hideSystemKeyBoard((EditText) v);
                    if (!isKeyboardShown()) {
                        showKeyboard();
                    } else {
                        hideKeyboard();
                    }
                }
                return false;
            }
        });*/
        /*mEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (v instanceof EditText) {
                    if (!hasFocus) {
                        if (isKeyboardShown()) {
                            hideKeyboard();
                        }
                    } else {
                        hideSystemKeyBoard((EditText) v);
                        showKeyboard();
                    }
                }
            }
        });*/
        mEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v instanceof EditText) {
                    if (isKeyboardShown()) {
                        hideKeyboard();
                    } else {
                        showKeyboard();
                    }
                }
            }
        });
    }

    /**
     * 软键盘显示状态
     * @return true 显示  false 隐藏
     */
    public boolean isShow() {
        return isKeyboardShown();
    }

    /**
     * 隐藏系统键盘关键代码
     * @param edit
     */
    private void hideSystemKeyBoard(EditText edit) {
//        this.mEditText = edit;
        InputMethodManager imm = (InputMethodManager) this.mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm == null)
            return;
        boolean isOpen = imm.isActive();
        if (isOpen) {
            imm.hideSoftInputFromWindow(edit.getWindowToken(), 0);
        }

        int currentVersion = Build.VERSION.SDK_INT;
        String methodName = null;
        if (currentVersion >= 16) {
            methodName = "setShowSoftInputOnFocus";
        } else if (currentVersion >= 14) {
            methodName = "setSoftInputShownOnFocus";
        }

        if (methodName == null) {
            edit.setInputType(0);
        } else {
            try {
                Method setShowSoftInputOnFocus = EditText.class.getMethod(methodName, Boolean.TYPE);
                setShowSoftInputOnFocus.setAccessible(true);
                setShowSoftInputOnFocus.invoke(edit, Boolean.FALSE);
            } catch (NoSuchMethodException e) {
                edit.setInputType(0);
                e.printStackTrace();
            } catch (IllegalAccessException | InvocationTargetException | IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 设置输入法提示
     * @param tips
     */
    public void setTvTips(String tips) {
        if (!TextUtils.isEmpty(tips)) {
            tvTips.setText(tips);
            tvTips.setVisibility(View.VISIBLE);
        } else {
            tvTips.setVisibility(View.GONE);
        }
    }
    /**
     * 软键盘显示状态
     * @return
     */
    private boolean isKeyboardShown() {
        return keyContainer.getVisibility() == View.VISIBLE;
    }

    /**
     * 设置删除按钮图标
     * @param delDrawable
     */
    public void setDelDrawable(Drawable delDrawable) {
        keyboardView.setDelDrawable(delDrawable);
    }

    /**
     * 设置小写按钮图标
     * @param lowDrawable
     */
    public void setLowDrawable(Drawable lowDrawable) {
        keyboardView.setLowDrawable(lowDrawable);
    }

    /**
     * 获取大写按钮图标
     * @param upDrawable
     */
    public void setUpDrawable(Drawable upDrawable) {
        keyboardView.setUpDrawable(upDrawable);
    }

    private Drawable keyDrawable;
    /**
     * 获取大写按钮图标
     * @param keyDrawable
     */
    public void setKeyDrawable(Drawable keyDrawable) {
        this.keyDrawable = keyDrawable;
        keyboardView.setKeyDrawable(keyDrawable);
    }

    /**
     * 绑定软键盘
     * @param keyboardType
     * @param editText
     */
    public void setKeyboardType(String keyboardType, EditText editText) {
        hideSystemKeyBoard(editText);
        //字母数字键盘，默认显示数字键盘
        if (TextUtils.equals(KeyboardManager.TYPE_TEXT_OR_NUMBER, keyboardType)) {
            this.keyboardType = KeyboardManager.TYPE_CUSTOM_NUMBER;
            this.keyboardTypeSwitch = keyboardType;
        } else {
            this.keyboardType = keyboardType;
            this.keyboardTypeSwitch = keyboardType;
        }
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);

        initCallback(editText);
        addListeners(editText);

        //默认大写键盘
        isCapes = false;
        changeKeyboardLetterCase();

        switchKeyboard();
    }

}
