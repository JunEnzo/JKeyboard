package com.luna.keyboard;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.luna.keyboardlibrary.KeyboardManager;
import com.luna.keyboardlibrary.annotation.DialogType;
import com.luna.keyboardlibrary.annotation.KeyBoartType;

public class MainActivity extends AppCompatActivity {

    private EditText mSysKey;
    private EditText mCusKey;
    private Button mDialogKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        KeyboardManager.getInstance().initUI(this, KeyBoartType.KEY_ACTIVITY);

        initUI();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initUI() {
        mSysKey = findViewById(R.id.edt_sys_key);
        mCusKey = findViewById(R.id.edt_cus_key);
        mDialogKey = findViewById(R.id.btn_cus_key);

        mSysKey.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                KeyboardManager.getInstance().hideKeyboard(KeyBoartType.KEY_ACTIVITY);
                return false;
            }
        });
        mCusKey.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mCusKey.requestFocus();
                KeyboardManager.getInstance().abcNumberKeyboard(mCusKey, KeyBoartType.KEY_ACTIVITY);
                return false;
            }
        });

        mDialogKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AppCustomDialog.Builder(MainActivity.this)
                        .type(DialogType.TYPE_EDIT)
                        .title("提示")
                        .message("Dialog绑定自定义输入法")
                        .cancelTouchout(true)
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .build()
                        .show();
            }
        });
    }
}
