package com.ymnet.onekeyclean.cleanmore.wechat;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.ymnet.onekeyclean.R;
import com.ymnet.onekeyclean.cleanmore.wechat.view.AddTrustDialog;
import com.ymnet.onekeyclean.cleanmore.wechat.view.ExportDialog;
import com.ymnet.onekeyclean.cleanmore.wechat.view.ListViewDialog;



/**
 * Created by wangduheng26 on 15/6/8.
 */
public class DialogFactory {
    /**
     *
     * @param context
     * @param id layout
     * @return
     */
    public static MyDialog createDialog(Context context, int id) {
        MyDialog dialog = new MyDialog(context, R.style.dialog);
        dialog.setContentView(id);
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation_style;
        return dialog;
    }

    /**
     *
     * @param context
     * @param id layout
     * @param style style
     * @return
     */
    public static MyDialog createDialog(Context context, int id, int style) {
        MyDialog dialog = new MyDialog(context, style);
        dialog.setContentView(id);
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation_style;
        return dialog;
    }

    public static class MyDialog extends Dialog{

        public MyDialog(Context context) {
            super(context);
        }

        public MyDialog(Context context, int theme) {
            super(context, theme);
        }

        protected MyDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
            super(context, cancelable, cancelListener);
        }
        public MyDialog setText(int id,CharSequence str){
            TextView view = (TextView) findViewById(id);
            if(view!=null&& !TextUtils.isEmpty(str)){
                view.setText(str);
            }
            return this;
        }
        public MyDialog setBtnOnclickListener(int id,View.OnClickListener listener){
            Button view = (Button) findViewById(id);
            if(view!=null&& listener!=null){
                view.setOnClickListener(listener);
            }
            return this;
        }
        public MyDialog setText(int id,int strId){
            String str=getContext().getString(strId);
            return setText(id,str);
        }

        public MyDialog setText(int id,int strId,Object... formatArgs){
            String str=getContext().getString(strId,formatArgs);
            return setText(id,str);
        }

        public MyDialog setCheckBoxText(int id,boolean visibility,CharSequence str){

            return setCheckBoxTextDefalult(id,visibility,str,false);
        }

        public boolean getCheckBoxSelect(int id){
            CheckBox view = (CheckBox) findViewById(id);
            if(view!=null){
                return view.isChecked();
            }
            return false;
        }

        public MyDialog setCheckBoxTextDefalult(int id,boolean visibility,CharSequence str, boolean b1) {
            CheckBox view = (CheckBox) findViewById(id);
            if(view!=null){
                if(visibility){
                    view.setVisibility(View.VISIBLE);
                }else{
                    view.setVisibility(View.GONE);
                }
                view.setChecked(b1);
                view.setText(str);
            }
            return this;


        }
    }
    public static Dialog createDialog(Context context, int id, String title, String message, String btn1, String btn0, View.OnClickListener list1, View.OnClickListener list0) {
        if (R.layout.dialog_filedelete == id) {
            Dialog dialog = new Dialog(context, R.style.dialog);
            dialog.setContentView(id);
            TextView dialogTitle = (TextView) dialog.findViewById(R.id.dialog_title);
            TextView dialogMessage = (TextView) dialog.findViewById(R.id.dialog_message);
            Button positiveBtn = (Button) dialog.findViewById(R.id.dialog_btn0);
            Button negativeBtn = (Button) dialog.findViewById(R.id.dialog_btn1);
            dialogTitle.setText(title);
            dialogMessage.setText(message);
            positiveBtn.setText(btn1);
            negativeBtn.setText(btn0);
            if (list1 != null) {
                positiveBtn.setOnClickListener(list1);
            }
            if (list0 != null) {
                negativeBtn.setOnClickListener(list0);
            }
            dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation_style;
            return dialog;
        } else {
            Dialog dialog = new Dialog(context, R.style.dialog);
            dialog.setContentView(id);
            dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation_style;
            return dialog;
        }

    }

    public static AddTrustDialog createTrustDialog(Context context) {
        AddTrustDialog dialog = new AddTrustDialog(context);
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation_style;
        return dialog;

    }

    public static ListViewDialog createListViewDialog(Context context, BaseAdapter adapter, AdapterView.OnItemClickListener listener) {
        ListViewDialog dialog = new ListViewDialog(context, adapter, listener);
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation_style;
        return dialog;
    }


    public static ExportDialog createExportDialog(Context context, CharSequence title){
        return ExportDialog.create(context,title);
    }
}
