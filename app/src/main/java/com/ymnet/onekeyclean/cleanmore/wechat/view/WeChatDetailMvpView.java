package com.ymnet.onekeyclean.cleanmore.wechat.view;


import com.ymnet.onekeyclean.cleanmore.wechat.mode.ChangeMode;
import com.ymnet.onekeyclean.cleanmore.wechat.mode.ExportMode;

/**
 * Created by wangdh on 5/3/16.
 * gmail:wangduheng26@gamil.com
 */
public interface WeChatDetailMvpView extends MVPView {
     void setText(String str);

     void hideLoading();

     void showLoading();

    void showExportDialog(boolean show);

    void changeExportProgress(ChangeMode mode);

    void showExportComplete(ExportMode mode);

    void changeGroupCount();
}
