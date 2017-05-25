package com.ymnet.onekeyclean.cleanmore.wechat.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ymnet.onekeyclean.R;


/**
 * Created by Administrator on 2014/12/24.
 */
public class AddTrustDialog extends Dialog {

    private Context context;
    private ImageView iv_icon;
    private TextView tv_name;
    private TextView btn_add_trust;

    public AddTrustDialog(Context context) {
        super(context, R.style.DialogTheme);
        this.context = context;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dia_add_trust);
        iv_icon = (ImageView) findViewById(R.id.iv_icon);
        tv_name = (TextView) findViewById(R.id.tv_name);
        btn_add_trust = (TextView) findViewById(R.id.btn_add_trust);
    }

    public void setIcon(Drawable d) {
        if (iv_icon != null && d != null) {
            iv_icon.setImageDrawable(d);
        }
    }

    public void setIcon(int id) {
        if (iv_icon != null) {
            iv_icon.setImageResource(id);
        }
    }

    public ImageView getIcon() {
        if(iv_icon==null) iv_icon= (ImageView) findViewById(R.id.iv_icon);
        return iv_icon;
    }

    public void setName(String name) {
        if (!TextUtils.isEmpty(name) && tv_name != null) {
            tv_name.setText(name);
        }
    }

    public void setBtnText(String text){
        if(!TextUtils.isEmpty(text)){
            btn_add_trust.setText(text);
        }
    }
    public void setBtnOnClickListener(View.OnClickListener listener) {
        if (btn_add_trust != null) {
            btn_add_trust.setOnClickListener(listener);
        }
    }

}
