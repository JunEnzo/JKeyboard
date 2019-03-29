package com.luna.keyboard;

import android.content.Context;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.luna.keyboardlibrary.annotation.DialogType;
import com.luna.keyboardlibrary.dialog.KeyBoardDialog;

/**
 * @description: 可绑定自定义软键盘dialog
 * @author: chenjun
 * @date: 2019/2/26
 * @time: 14:29
 */
public class AppCustomDialog extends KeyBoardDialog {

    private static final int TYPE_NORMAL = 0;  //普通弹框，默认
    private static final int TYPE_EDIT = 1;    //编辑框
    private static final int TYPE_PROGRESSBAR = 3;    //加载框

    private Builder mBuilder;

    private AppCustomDialog(Builder builder) {
        super(builder.context, R.style.CustomDialog);
        this.mBuilder = builder;
    }

    private AppCustomDialog(Builder builder, int resStyle) {
        super(builder.context, resStyle);
        this.mBuilder = builder;
    }

    @Override
    protected View bindLayoutView() {
        return mBuilder.view;
    }

    @Override
    protected EditText bindEditView() {
        return mBuilder.edtMessage;
    }

    @Override
    protected void initView() {
        if (mBuilder != null && mBuilder.btnPosition.getVisibility() == View.VISIBLE) {
            this.mBuilder.btnPosition.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBuilder.posListener.onClick(AppCustomDialog.this, -1);
                }
            });
        }

        if (mBuilder != null && mBuilder.btnCancle.getVisibility() == View.VISIBLE) {
            this.mBuilder.btnCancle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBuilder.cancleListener.onClick(AppCustomDialog.this, -1);
                }
            });
        }
    }

    @Override
    protected void bindLayoutParams() {
        ViewGroup.LayoutParams lp = mBuilder.view.getLayoutParams();
        if (mBuilder.height > 0) {
            lp.height = mBuilder.height;
        }
        lp.width = mBuilder.width;
        mBuilder.view.setLayoutParams(lp);
    }

    public static class Builder {
        private Context context;
        private int height, width; //弹框宽高
        private boolean cancelTouchout; //点击弹框外，弹框是否消失
        private View view;  //弹框view
        private int resStyle = -1;  //弹框style

        private TextView tvTitle;  //标题
        private TextView tvMessage; //内容
        private EditText edtMessage;    //输入框
        private ProgressBar pbLoading;  //正在加载提示
        private TextView tvProgressBarTips; //加载提示
        private TextView btnPosition;   //确认
        private TextView btnCancle;     //取消
        private OnClickListener posListener;
        private OnClickListener cancleListener;

        public Builder(Context context) {
            this.context = context;
            this.view = LayoutInflater.from(context).inflate(R.layout.custom_dialog_layout, null);

            this.tvTitle = (TextView) view.findViewById(R.id.tv_title);
            this.tvMessage = (TextView) view.findViewById(R.id.tv_message);
            this.edtMessage = (EditText) view.findViewById(R.id.edt_message);
            this.pbLoading = (ProgressBar) view.findViewById(R.id.pb_loading);
            this.tvProgressBarTips = (TextView) view.findViewById(R.id.tv_progressbar_tips);
            this.btnPosition = (TextView) this.view.findViewById(R.id.tv_position);
            this.btnCancle = (TextView) this.view.findViewById(R.id.tv_cancle);

            this.width = (int) (getScreenWidth(context) * 0.7);
        }

        public Builder title(String title) {
            if (!TextUtils.isEmpty(title)) {
                this.tvTitle.setVisibility(View.VISIBLE);
                this.tvTitle.setText(title);
            } else {
                this.tvTitle.setVisibility(View.GONE);
            }
            return this;
        }

        public Builder progressbarTitle(String title) {
            if (!TextUtils.isEmpty(title)) {
                this.tvProgressBarTips.setVisibility(View.VISIBLE);
                this.tvProgressBarTips.setText(title);
            } else {
                this.tvProgressBarTips.setVisibility(View.GONE);
            }
            return this;
        }

        public Builder message(String message) {
            if (!TextUtils.isEmpty(message)) {
                this.tvMessage.setVisibility(View.VISIBLE);
                this.tvMessage.setText(message);
            } else {
                this.tvMessage.setVisibility(View.GONE);
            }
            return this;
        }

        public Builder height(int heightPx) {
            this.height = heightPx;
            return this;
        }

        public Builder width(int widthPx) {
            this.width = widthPx;
            return this;
        }

        public Builder cancelTouchout(boolean cancelTouchout) {
            this.cancelTouchout = cancelTouchout;
            return this;
        }

        public Builder style(int resStyle) {
            this.resStyle = resStyle;
            return this;
        }

        /**
         * 以注解的方式规定传入参数
         */
        public Builder type(@DialogType.TypeMode int dialotType) {
            if (dialotType == TYPE_EDIT) {
                this.edtMessage.setVisibility(View.VISIBLE);
            } else if (dialotType == TYPE_PROGRESSBAR) {
                this.pbLoading.setVisibility(View.VISIBLE);
            } else {
                this.tvTitle.setVisibility(View.VISIBLE);
            }
            return this;
        }

        public String getEditData() {
            return edtMessage.getText().toString().trim();
        }

        public Builder setEditData(String str) {
            edtMessage.setText(str);
            return this;
        }

        public Builder setPositiveButton(String buttonName, OnClickListener listener) {
            this.posListener = listener;
            this.btnPosition.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(buttonName)) {
                btnPosition.setText(buttonName);
            }
            return this;
        }

        public Builder setCancleButton(String buttonName, OnClickListener listener) {
            this.cancleListener = listener;
            this.btnCancle.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(buttonName)) {
                btnCancle.setText(buttonName);
            }
            return this;
        }

        public AppCustomDialog build() {
            if (resStyle != -1) {
                return new AppCustomDialog(this, resStyle);
            } else {
                return new AppCustomDialog(this);
            }
        }
    }

    /**
     * 获取屏幕像素宽
     *
     * @return 屏幕宽
     */
    private static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

}
