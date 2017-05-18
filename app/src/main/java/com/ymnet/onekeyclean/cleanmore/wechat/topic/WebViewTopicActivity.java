package com.ymnet.onekeyclean.cleanmore.wechat.topic;/*
package com.example.baidumapsevice.wechat.topic;

import android.accounts.Account;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.baidumapsevice.wechat.MarketApplication;
import com.example.baidumapsevice.wechat.R;
import com.example.baidumapsevice.wechat.activity.TitleBarActivity;
import com.example.baidumapsevice.constants.TaskContants;
import com.example.baidumapsevice.customview.CircularProgress;
import com.example.baidumapsevice.wechat.detail.DetailActivity;
import com.example.baidumapsevice.model.App;
import com.example.baidumapsevice.model.TopicInfo;
import com.example.baidumapsevice.model.TopicUtils;
import com.example.baidumapsevice.utils.C;
import com.facebook.common.util.UriUtil;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;

public class WebViewTopicActivity extends TitleBarActivity implements View.OnClickListener, MarketObserver {
    private static final String TAG = WebViewTopicActivity.class.getSimpleName();

    public static final int DO_TASK_STATUS_CODE_DONE = 0;
    public static final int DO_TASK_STATUS_CODE_EMPTY_SOFT_ID = 10000;
    public static final int DO_TASK_STATUS_CODE_APP_NOT_INSTALL = 10002;

    private static final int TOPIC_SUB_TYPE_NORMAL = 0;
    private static final int TOPIC_SUB_TYPE_SINGLE_APP = 1;
    private static final String PREFIX_SHARE_KEY = "js_sharepref_";
    private int from = -1;

    private String[] CODE2STATUS = {
            // status_code
            "下载",      //   0
            "等待中",    //   1
            "暂停",      //   2
            "直接下载",  //    3
            "继续",     //    4
            "重试",     //    5
            "检测中",   //    6
            "安装中",   //     7
            "升级",     // 8
            "安装",     // 9
            "打开"      // 10
    };


    private HashMap<String, Integer> STATUS2CODE;

    private boolean isPointWallTask;
    private int mTopicId;
    private TextView mTopTitle;
    private WebView mWebView;

    private View mAppStatusView;
    private View mPlaceHolder;
    private ImageButton mShareBtn;

    private TopicInfo mTopicInfo;

    private SparseArray<App> mApps;

    */
/**
     * K: linkedDownloadUrl
     * V: buttonId
     *//*

    private HashMap<String, String> mPlaceMap;
    */
/**
     * K: softId
     * V: button status
     *//*

    private HashMap<String, Integer> mStatusMap;

    */
/**
     * K: btnId
     * V: linked app download url
     *//*

    private HashMap<String, String> mDownloadURLMap;

    */
/**
     * K: btnId
     * V: linked app package name
     *//*

    private HashMap<String, String> mPackageNameMap;

    private HashMap<String, ProgressCallback> mCallbacks;

    private int mSubType = -1;

    private boolean disableLinkInWebView;
    private boolean showCustomizeLabel;
    private boolean showGiftLabel;


    private void setDisableLinkInWebView(boolean disable) {
        this.disableLinkInWebView = disable;
    }

    private DownloadManager    mDownloadManager;
    private DataCenterObserver session;
    private CustomShareBoard   shareBoard;

    */
/**
     * Share content
     *//*

    private String mTitle;
    private String mContent;
    private String mIconUrl;
    private String mTargetUrl;

    private boolean isFirst = true;


    public interface DownloadClickListener {
        void click();
    }

    private TaskEventStatusObserver pointWallTaskEventStatusObserver = new TaskEventStatusObserver(WebViewTopicActivity.this);

    public final static class TaskEventStatusObserver implements PointWallTaskEventStatusObserver {

        private WeakReference<WebViewTopicActivity> mRef;


        public TaskEventStatusObserver(WebViewTopicActivity activity) {
            mRef = new WeakReference<WebViewTopicActivity>(activity);
        }


        @Override
        public void onTaskEventDone(int type, String softid) {
            Activity activity = mRef.get();
            if (activity != null) {
                ((WebViewTopicActivity) activity).callbackPointWallTaskEventStatus(softid, String.valueOf(WebViewTopicActivity.DO_TASK_STATUS_CODE_DONE));
            }
        }

        @Override
        public void onTaskEventError(int type, int errorCode, String softid) {
            Activity activity = mRef.get();
            if (activity != null) {
                ((WebViewTopicActivity) activity).callbackPointWallTaskEventStatus(softid, String.valueOf(errorCode));
                if (errorCode == TaskContants.DOTASK_ERROR_CODE_NET_ERROR) {
                    ((WebViewTopicActivity) activity).callbackPointWallTaskEventStatus(softid, String.valueOf(errorCode));
                    Toast.makeText(C.get(), activity.getString(R.string.point_wall_network_issue), Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        public void onTaskEventDone(int type) {

        }

        @Override
        public void onTaskEventError(int type, int errorCode) {

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (shareBoard != null) {
            shareBoard.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview_topic);
        from = getIntent().getIntExtra("from",-1);
        EventBus.getDefault().register(this);
        disableLinkInWebView = false; // default value
        initView();
        initJsBridge();
        initListener();
        loadData();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
        updateWebViewButtons();

        if (shareBoard != null && shareBoard.isShowing()) {
            shareBoard.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (shareBoard != null) {
            shareBoard.cleanListeners();
        }

        EventBus.getDefault().unregister(this);
        MarketApplication.getInstance().getApplicationComponent().taskManager().unregisterTaskEventObserver(pointWallTaskEventStatusObserver);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (mWebView != null && mWebView.canGoBack()) {
                        mWebView.goBack();
                    } else {
                        finish();
                    }
                    return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }


    private Callback<Response<DetailAppEntity>> appInfoCallback = new Callback<Response<DetailAppEntity>>() {
        @Override
        public void onResponse(Call<Response<DetailAppEntity>> call, Response<DetailAppEntity> response) {
            if (response.getData() != null) {
                SingleAppResp appDetail = new SingleAppRespMapper().transform(response);
                if (appDetail.list != null) {
                    App app = appDetail.list;
                    AppsUtils.notifyDisplayEvent(app.packageName);
                    mApps.put(app.sid, app);
                    switch (mSubType) {
                        case WebViewTopicActivity.TOPIC_SUB_TYPE_NORMAL:
                            int intStatus = mStatusMap.get(String.valueOf(app.sid));
                            String status = CODE2STATUS[intStatus];
                            Log.d(TAG, "status " + status);
                            if (!TextUtils.isEmpty(status)) {
                                Log.d(TAG, "onSucess: performClick---" + status + "/" + app.sid);
                                mDownloadManager.performClick(WebViewTopicActivity.this, status, app, isPointWallTask);
                            }
                            break;
                        case WebViewTopicActivity.TOPIC_SUB_TYPE_SINGLE_APP:
                            initAppStatus(app);
                            break;
                    }
                }
            }

        }

        @Override
        public void onFailure(Call<Response<DetailAppEntity>> call, Throwable t) {
            showError();
        }
    };

    class JSInterface {

        @JavascriptInterface
        public void login() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(WebViewTopicActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            });
        }


        @JavascriptInterface
        public void toast(final String msg) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(C.get(), msg, Toast.LENGTH_SHORT).show();
                }
            });
        }

        @JavascriptInterface
        public void toTaskCenter() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Intent intent = TaskCenterActivity.getCallingIntent(C.get());
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    C.get().startActivity(intent);
                }
            });
        }

        @JavascriptInterface
        public void openAppDetail(final String softId) {
            Log.d(TAG, "openAppDetail---" + softId);
            //In case of NumberFormatException
            if (null == softId) {
                return;
            }

            int sid = Integer.valueOf(softId.trim());
            if (sid > 0) {
                TopicUtils.startDetailTopicActivity(C.get(), sid);
            }
        }

        @JavascriptInterface
        public void requestPointOnWall(final String softId, final String pkgName) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    boolean isSignIn = Account.getExistedInstance().isLocalAccountSignin(C.get());
                    if (isSignIn) {
                        if (!TextUtils.isEmpty(softId) && !TextUtils.isEmpty(pkgName)) {
                            DataCenterObserver observer = DataCenterObserver.get(C.get());
                            if (observer.isInstalled(pkgName)) {
                                if (Utils.isNetworkAvailable(C.get())) {
                                    MarketApplication.getInstance().getApplicationComponent().taskManager().putTaskEvent(TaskType.TYPE_POINT_WALL, softId, pointWallTaskEventStatusObserver);
                                } else {
                                    callbackPointWallTaskEventStatus(softId, String.valueOf(TaskContants.DOTASK_ERROR_CODE_NET_ERROR));
                                    Toast.makeText(C.get(), getString(R.string.point_wall_network_issue), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                callbackPointWallTaskEventStatus(softId, String.valueOf(DO_TASK_STATUS_CODE_APP_NOT_INSTALL));
                            }
                        } else {
                            callbackPointWallTaskEventStatus(softId, String.valueOf(DO_TASK_STATUS_CODE_EMPTY_SOFT_ID));
                        }
                    } else {
                        callbackPointWallTaskEventStatus(softId, String.valueOf(TaskContants.DOTASK_ERROR_CODE_UNLOGIN));
                    }
                }
            });
        }

        @JavascriptInterface
        public void onDownloadButtonClick(final String btnId, final String softId, final int intStatus) {
            onDownloadButtonClick(btnId, softId, intStatus, 0);
        }

        @JavascriptInterface
        public void onDownloadButtonClick(final String btnId, final String softId, final int intStatus, final int ignoreTaskEvent) {
            Log.d(TAG, "onDownloadButtonClick---btnId: " + btnId);
            Log.d(TAG, "onDownloadButtonClick---softId: " + softId);
            Log.d(TAG, "onDownloadButtonClick---intStatus: " + intStatus);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (btnId != null && softId != null && intStatus >= 0) {
                        Log.d(TAG, btnId);
                        Log.d(TAG, softId);
                        Log.d(TAG, intStatus+"");
                        if(from == 6){
//                            StatisticSpec.sendEvent(StatisticEventContants.push_zhuanti_click_download+mTopicId);
                        }
                        if (!TextUtils.isEmpty(btnId)) {
                            Log.d(TAG, "button clicked " + btnId);
                            Log.d(TAG, "button clicked " + softId);
                            mStatusMap.put(softId.trim(), intStatus);

                            int sid = -1;
                            try {
                                sid = Integer.valueOf(softId.trim());
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }

                            if (ignoreTaskEvent == 1) {
                                isPointWallTask = true;
                            } else {
                                isPointWallTask = false;
                            }

                            if (mApps != null && mApps.size() > 0) {
                                App app = mApps.get(sid);
                                if (app != null) {
                                    String status = CODE2STATUS[intStatus];
                                    Log.d(TAG, "onDownloadButtonClick---" + intStatus + "/" + app.sid);
                                    mDownloadManager.performClick(WebViewTopicActivity.this, status, app, isPointWallTask);
                                } else {
                                    TApier.get().getAppByID(sid).enqueue(appInfoCallback);
                                }
                            } else {
                                TApier.get().getAppByID(sid).enqueue(appInfoCallback);
                            }
                        }
                    }
                }
            });
        }


        @JavascriptInterface
        public void getButtonInitStatus(final String btnId, final String linkedPackageName, final String linkedDownloadURL) {
            Log.d(TAG, "getButtonInitStatus");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!TextUtils.isEmpty(linkedPackageName) && !TextUtils.isEmpty(btnId)) {
                        mDownloadURLMap.put(btnId, linkedDownloadURL);
                        mPackageNameMap.put(btnId, linkedPackageName);
                        mPlaceMap.put(linkedDownloadURL, btnId);
                        JSDownloadButtonStatus button = new JSDownloadButtonStatus();
                        button.btnId = btnId;
                        button.intStatus = session.isInstalled(linkedPackageName) ? 10 : 0;
                        button.isEnabled = 1;
                        button.percents = -1;
                        callbackButtonInitStatus(button.btnId, button.intStatus, button.isEnabled, button.percents);
                        showWebViewTopicHistory();
                        initWebViewButtons();
                    }
                }
            });
        }

        @JavascriptInterface
        public void changeTopicTitle(final String title) {
            Log.d(TAG, "changeTopicTitle");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!TextUtils.isEmpty(title)) {
                        mTopTitle.setText(title);
                    }
                }
            });
        }

        @JavascriptInterface
        public void openPSirTopicList() {
            Log.d(TAG, "openPSirList");
            openPSirList();
        }

        @JavascriptInterface
        public void copyToClipboard(String content) {
            Log.d(TAG, "copyToClipboard: " + content);
            if (!TextUtils.isEmpty(content)) {
                ApplicationUtils.copy(content);
                Toast.makeText(C.get(), R.string.dialog_gift_get_copy_success, Toast.LENGTH_SHORT).show();
            }
        }

        @JavascriptInterface
        public void setPreference(String name, String value) {
            Log.d(TAG, "setPreference: name=" + name);
            Log.d(TAG, "setPreference: value=" + value);
            if (!TextUtils.isEmpty(name)) {
                String internalKey = PREFIX_SHARE_KEY + name;
                C.setPreference(internalKey, value);
            }
        }

        @JavascriptInterface
        public String getPreference(String name, String defValue) {
            Log.d(TAG, "getPreference: name=" + name);
            Log.d(TAG, "getPreference: defValue=" + defValue);
            String res = defValue;
            if (!TextUtils.isEmpty(name)) {
                String internalKey = PREFIX_SHARE_KEY + name;
                res = C.getPreference(internalKey, defValue);
            }
            Log.d(TAG, "getPreference: res=" + res);
            return res;
        }

        @JavascriptInterface
        public void removePreference(String name) {
            Log.d(TAG, "removePreference: name=" + name);
            if (!TextUtils.isEmpty(name)) {
                String internalKey = PREFIX_SHARE_KEY + name;
                C.removePreference(internalKey);
            }
        }

        @JavascriptInterface
        public boolean showShareButton(final String title, final String content, final String iconUrl, final String targetUrl) {
            final AtomicBoolean showSuccess = new AtomicBoolean(true);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (TextUtils.isEmpty(targetUrl)) {
                        showSuccess.set(false);
                        return;
                    }

                    //Init share content
                    mTitle = title;
                    mContent = content;
                    mIconUrl = iconUrl;
                    mTargetUrl = targetUrl;

                    //show share button
                    mShareBtn.setVisibility(View.VISIBLE);
                    showSuccess.set(true);
                }
            });
            return showSuccess.get();
        }

        @JavascriptInterface
        public String getPhoneData() {
            Log.d(TAG, "getPhoneData");

            String res = "";
            PhoneData data = PhoneData.getInstance(C.get());
            Gson gson = new Gson();
            String jsonString = gson.toJson(data);
            if (!TextUtils.isEmpty(jsonString)) {
                res = jsonString;
            }

            Log.d(TAG, "PhoneData=" + res);
            return res;
        }

        @JavascriptInterface
        public boolean share(final String title, final String content, final String iconUrl, final String targetUrl) {
            WebViewTopicActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (TextUtils.isEmpty(targetUrl)) {
                        return;
                    }

                    UMImage shareImage = null;
                    if (!TextUtils.isEmpty(iconUrl)) {
                        shareImage = new UMImage(WebViewTopicActivity.this, iconUrl);
                    }

                    if (shareBoard == null) {
                        shareBoard = new CustomShareBoard(WebViewTopicActivity.this);
                        shareBoard.configPlatforms();
                    }

                    shareBoard.setAllShareContent(title, content, targetUrl, shareImage);
                    if (!shareBoard.isShowing()) {
                        shareBoard.showAtLocation(WebViewTopicActivity.this.getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
                    }
                }
            });
            // 分享成功返回true,失败返回false，目前暂时返回true
            return true;
        }

        @JavascriptInterface
        public void getLogonUserInfo() {
            boolean isLogin = Account.getExistedInstance().isLocalExisted(WebViewTopicActivity.this);
            if (isLogin) {
                setUserInfo(getJSONString());
            } else {
                TopicUtils.startLoginActivity(C.get());
            }
        }

        @JavascriptInterface
        public String getUserInfo() {
            Account account = Account.getExistedInstance();
            boolean isLogin = account.isLocalExisted(C.get());

            if (isLogin) {
                return getJSONString();
            } else {
                return new JSONObject().toString();
            }
        }

        @JavascriptInterface
        public void bindPhone() {
            boolean isLogin = Account.getExistedInstance().isLocalExisted(WebViewTopicActivity.this);
            if (isLogin) {
                TopicUtils.startBindPhoneActivity(C.get());
            } else {
                TopicUtils.startLoginActivity(C.get());
            }
        }

        @JavascriptInterface
        public String getZSAppVersionCode() {
            String code = "";
            int versionCode = Utils.getVersionCode(C.get());
            if (versionCode > 0) {
                code = String.valueOf(versionCode);
            }
            return code;
        }

        @JavascriptInterface
        public String getZSAppVersionName() {
            return Utils.getVersonName(C.get());
        }
    }

    private String getJSONString() {
        Account account = Account.getExistedInstance();
        JSONObject obj = new JSONObject();
        try {
            obj.put("uid", account.getUserInfo(Account.INFOKEY_USERID, C.get()))
                    .put("uname", account.getUserInfo(Account.INFOKEY_USERNAME, C.get()))
                    .put("passid", account.getUserInfo(Account.INFOKEY_PASSID, C.get()))
                    .put("lastToken", account.getUserInfo(Account.INFOKEY_ACCESSTOKEN, C.get()))
                    .put("avatar", account.getUserInfo(Account.INFOKEY_AVARTARURL, C.get()))
                    .put("regType", account.getUserInfo(Account.INFOKEY_REGTYPE, C.get()))
                    .put("phone", account.getUserInfo(Account.INFOKEY_PHONENUMBER, C.get()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        L.i(TAG, obj.toString());
        return obj.toString();
    }

    private void showShareBoard() {
        UMImage shareImage = null;
        if (!TextUtils.isEmpty(mIconUrl)) {
            shareImage = new UMImage(WebViewTopicActivity.this, mIconUrl);
        }

        if (shareBoard == null) {
            shareBoard = new CustomShareBoard(WebViewTopicActivity.this);
            shareBoard.configPlatforms();
        }

        shareBoard.setAllShareContent(mTitle, mContent, mTargetUrl, shareImage);
        if (!shareBoard.isShowing()) {
            shareBoard.showAtLocation(WebViewTopicActivity.this.getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
        }
    }

    private void openPSirList() {
        Log.d(TAG, "openPSirList");
        Intent intent = new Intent(this, PSirTopicListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void callbackPointWallTaskEventStatus(String softId, String statusCode) {
        if (mWebView != null) {
            mWebView.loadUrl("javascript:callbackPointWallTaskEventStatus('" + softId + "', '" + statusCode + "')");
        }
    }

    private void callbackButtonInitStatus(String btnId, int intStatus, int isEnabled, float percents) {
        Log.d(TAG, "callbackButtonInitStatus---" + btnId + "/" + intStatus + "/" + isEnabled + "/" + percents);
        if (mWebView != null) {
            mWebView.loadUrl("javascript:setBtnStatus('" + btnId + "', '" + intStatus + "', '" + isEnabled + "', '" + percents + "')");
        }
    }

    private void showWebViewTopicHistory() {
        if (mWebView != null) {
            mWebView.loadUrl("javascript:showTopicHistory()");
        }
    }

    private void setUserInfo(String jsonStr) {
        if (mWebView != null) {
            mWebView.loadUrl("javascript:receiveUserInfo('" + jsonStr + "')");
        }
    }

    private void initJsBridge() {
        if (mWebView == null) return;
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(new JSInterface(), "zhushou");
    }

    private View fl_loading, pb_loading, ll_loaded_fail, btn_retry;

    private void initLoadingView() {
        fl_loading = findViewById(R.id.fl_loading);
        pb_loading = findViewById(R.id.pb_loading);
        if (pb_loading != null && pb_loading instanceof CircularProgress) {
            ((CircularProgress) pb_loading).setName(TAG);
        }
        ll_loaded_fail = findViewById(R.id.ll_loaded_fail);
        btn_retry = findViewById(R.id.btn_retry);
    }

    private void checkLoadingView() throws Exception {
        if (fl_loading == null || pb_loading == null || ll_loaded_fail == null) {
            throw new IllegalArgumentException("loading view has null");
        }
    }

    private void showLoading() {
        try {
            checkLoadingView();
        } catch (Exception e) {
//            e.printStackTrace();
            return;
        }
        fl_loading.setVisibility(View.VISIBLE);
        pb_loading.setVisibility(View.VISIBLE);
        ll_loaded_fail.setVisibility(View.GONE);
    }

    private void hideLoading() {
        try {
            checkLoadingView();
        } catch (Exception e) {
//            e.printStackTrace();
            return;
        }
        fl_loading.setVisibility(View.GONE);
        pb_loading.setVisibility(View.GONE);
        ll_loaded_fail.setVisibility(View.GONE);
    }

    private void showLoadFail() {
        try {
            checkLoadingView();
        } catch (Exception e) {
//            e.printStackTrace();
            return;
        }
        fl_loading.setVisibility(View.VISIBLE);
        pb_loading.setVisibility(View.GONE);
        ll_loaded_fail.setVisibility(View.VISIBLE);
    }

    private void initView() {
        initLoadingView();
        showCustomizeLabel = false;
        showGiftLabel = false;
        mTopicId = getIntent().getIntExtra(TopicInfo.TOPIC_ID, -1);
        if (mTopicId == -1) {
            Toast.makeText(getApplicationContext(), "专题不存在!", Toast.LENGTH_SHORT).show();
            finish();
        }

        mTopTitle = (TextView) findViewById(R.id.tv_top_title);
        mWebView = (WebView) findViewById(R.id.webView);
        mWebView.setWebViewClient(new MyWebViewClient());
        mWebView.setWebChromeClient(new MyWebChromeClient());
        mWebView.getSettings().setDomStorageEnabled(true);
        File dir = C.get().getExternalCacheDir();
        if (dir != null) {
            if (!dir.exists()) {
                dir.mkdirs();
            }

            Log.d(TAG, "app cache dir=" + dir.getPath());
            mWebView.getSettings().setAppCachePath(dir.getPath());
        }

        mWebView.getSettings().setAllowFileAccess(true);
        mWebView.getSettings().setAppCacheEnabled(true);
        handleWebViewDownloadStatus();


        mAppStatusView = findViewById(R.id.app_status_item);
        mPlaceHolder = findViewById(R.id.v_ph);
        mShareBtn = (ImageButton) findViewById(R.id.web_share);
    }

    private void handleWebViewDownloadStatus() {
        if (!disableLinkInWebView) {
            Log.d(TAG, "set DownloadListener");
            mWebView.setDownloadListener(new DownloadListener() {
                @Override
                public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                    Log.d(TAG, "onDownloadStart---" + url);
                    Uri uri = Uri.parse(url);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            });
        } else {
            Log.d(TAG, "remove DownloadListener");
            mWebView.setDownloadListener(null);
        }
    }

    private void initListener() {
        findViewById(R.id.ib_top_back).setOnClickListener(this);
        ll_loaded_fail.setOnClickListener(this);
        btn_retry.setOnClickListener(this);
        mShareBtn.setOnClickListener(this);
    }

    private void loadData() {
        DataCenterObserver.get(C.get()).addObserver(this);
        mDownloadManager = DownloadManager.getInstance(C.get());
        session = DataCenterObserver.get(C.get());

        mWebView.setVisibility(View.INVISIBLE);
        showLoading();

        Call<Response<TplTopicEntity>> call = TApier.get().getTopicInfo(mTopicId);
        call.enqueue(new Callback<Response<TplTopicEntity>>() {
            @Override
            public void onResponse(Call<Response<TplTopicEntity>> call, Response<TplTopicEntity> response) {
                if (WebViewTopicActivity.this.isFinishing()) {
                    return;
                }
                TopicResponseInfo info = new TopicResponseInfoMapper().transform(response);
                if (MHttp.responseOK(response.getCode()) && info.list != null) {
                    initTopic(info.list);
                } else {
                    showError();
                }
            }

            @Override
            public void onFailure(Call<Response<TplTopicEntity>> call, Throwable t) {
                if (WebViewTopicActivity.super.isFinishing()) {
                    return;
                }
                showError();
            }
        });
    }

    @Override
    public void update(MarketObservable observable, Object data) {
        Log.d(TAG, "update");
        if (isFinishing() || null == mApps) {
            return;
        }

        if (mApps.size() > 0) {
            if (data instanceof String) {
                if (SessionManager.ADD_OR_REMOVE_DOWNLOAD.equals(data)) {
                    switch (mSubType) {
                        case WebViewTopicActivity.TOPIC_SUB_TYPE_NORMAL:
                            initWebViewButtons();
                            break;
                        case WebViewTopicActivity.TOPIC_SUB_TYPE_SINGLE_APP:
                            App app = mApps.get(mTopicInfo.softId);
                            AppsUtils.notifyDisplayEvent(app.packageName);
                            initAppStatus(app);
                            break;
                    }
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_top_back:
                finish();
                break;
            case R.id.ll_loaded_fail:
            case R.id.btn_retry:
                if (!Utils.isNetworkAvailable(this)) {
                    Toast.makeText(C.get(), "网络异常,请稍后再试", Toast.LENGTH_SHORT).show();
                    return;
                }
                showLoading();
                loadData();
                break;
            case R.id.web_share:
                if (TextUtils.isEmpty(mTargetUrl)) {
                    return;
                }
                Statistics.onEvent(C.get(), StatisticEventContants.Zhuanti_Detail_Share_ + mTopicId);
                showShareBoard();
                break;
            default:
                break;
        }

    }

    private void initAppStatus(final App app) {
        if (app == null) return;

        mAppStatusView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Statistics.onEvent(C.get(), StatisticEventContants.zhuanti_singleapp_click_id + mTopicId);
                startActivity(new Intent(WebViewTopicActivity.this, DetailActivity.class).putExtra(App.class.getSimpleName(), app)
                        .putExtra("zhuantiSingleappTopicId", mTopicId)
                        .putExtra("zhuantiSingleappDetailType", "zhuantiSingleappDetailType")
                );
            }
        });

        ImageView iv_app_icon = (ImageView) mAppStatusView.findViewById(R.id.iv_app_icon);
        TextView tv_recommend_icon = (TextView) mAppStatusView.findViewById(R.id.tv_recommend_icon);
        TextView tv_title = (TextView) mAppStatusView.findViewById(R.id.tv_title);
        TextView tv_label = (TextView) mAppStatusView.findViewById(R.id.tv_label);
        TextView tv_gift_label = (TextView) mAppStatusView.findViewById(R.id.tv_gift_label);
        TextView tv_size = (TextView) mAppStatusView.findViewById(R.id.tv_size);
        TextView tv_download_count = (TextView) mAppStatusView.findViewById(R.id.tv_download_count);
        IntroduceView tv_introduce = (IntroduceView) mAppStatusView.findViewById(R.id.tv_introduce);
        DownloadProgressView pb_progress = (DownloadProgressView) mAppStatusView.findViewById(R.id.pb_progress);
        DownloadStatusView tv_download = (DownloadStatusView) mAppStatusView.findViewById(R.id.tv_download);
        RateView tv_rate = (RateView) mAppStatusView.findViewById(R.id.tv_rate);
        DownloadCountLayout rl_size_download_count = (DownloadCountLayout) mAppStatusView.findViewById(R.id.rl_size_download_count);
        DownloadSpeedLayout ll_download_size_speed = (DownloadSpeedLayout) mAppStatusView.findViewById(R.id.ll_download_size_speed);
        SizeView tv_download_size = (SizeView) mAppStatusView.findViewById(R.id.tv_download_size);
        SpeedView tv_speed = (SpeedView) mAppStatusView.findViewById(R.id.tv_speed);
        TextView tv_signature = (TextView) mAppStatusView.findViewById(R.id.tv_signature);

        mDownloadManager.setOnClickListener(tv_download);
        ViewTagger.setTag(tv_download, R.id.hold_activty, WebViewTopicActivity.this);
//        ImageLoader.getInstance().displayImage(app.icon, iv_app_icon);
        iv_app_icon.setImageURI(UriUtil.parse(app.icon));
        setTitleIcon(app.recomIco, tv_recommend_icon);
        tv_title.setText(app.title);
        if (showCustomizeLabel && !TextUtils.isEmpty(app.sLabel)) {
            tv_label.setText(app.sLabel);
            tv_label.setVisibility(View.VISIBLE);
        } else {
            tv_label.setVisibility(View.GONE);
        }

        if (showGiftLabel && app.giftTotal > 0) {
            tv_gift_label.setVisibility(View.VISIBLE);
        } else {
            tv_gift_label.setVisibility(View.GONE);
        }

        tv_title.requestLayout();
        tv_size.setText(app.fileLength);
        tv_download_count.setText(ApplicationUtils.getFormatDownloads(app.totalDowns));

        try {
            double m = Double.parseDouble(app.mark);
            if (m > 10) {
                app.mark = "10.0";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        tv_introduce.setText(app.oneword);


        tv_download.setTag(R.id.download_result_click, new DownloadClickListener() {

            @Override
            public void click() {
                Statistics.onEvent(C.get(), StatisticEventContants.zhuanti_singleapp_download_id + mTopicId);
            }
        });
        tv_download.setTag(R.id.download_item, app);
        tv_download.setTag(R.id.download_url, app.url);
        pb_progress.setTag(R.id.download_url, app.url);
        tv_rate.setTag(R.id.download_url, app.url);
        tv_introduce.setTag(R.id.download_url, app.url);
        rl_size_download_count.setTag(R.id.download_url, app.url);
        ll_download_size_speed.setTag(R.id.download_url, app.url);
        tv_download_size.setTag(R.id.download_url, app.url);
        tv_speed.setTag(R.id.download_url, app.url);

        DownloadInfo downloadInfo = mDownloadManager.getDownloadInfo(app.url);
        if (downloadInfo != null) {
            downloadInfo.addProgressViews(
                    pb_progress,
                    tv_download,
                    tv_rate,
                    tv_introduce,
                    rl_size_download_count,
                    ll_download_size_speed,
                    tv_download_size,
                    tv_speed);
            downloadInfo.notifyProgress(this);
        } else {
            // 升级 or 打开 or 下载
            if (session.getAppsInfoHandler().checkInupdatelist(app.packageName)) {
                // 升级
                tv_download.setText("升级");
                tv_download.setTextColor(getResources().getColor(R.color.item_update_color));
                tv_download.setBackgroundResource(R.drawable.install_bg);

                //签名冲突
                if (!TextUtils.isEmpty(app.certMd5) && !session.getInstalledApp(app.packageName).signatures.contains(app.certMd5)) {
                    tv_signature.setVisibility(View.VISIBLE);
                } else {
                    tv_signature.setVisibility(View.GONE);
                }
            } else if (session.getAppsInfoHandler().checkIsHasInatall(app.packageName)) {
                // 打开
                tv_download.setText("打开");
                tv_download.setTextColor(getResources().getColor(R.color.item_update_color));
                tv_download.setBackgroundResource(R.drawable.install_bg);
            } else {
                tv_download.setText("下载");
                tv_download.setTextColor(getResources().getColor(R.color.item_down_color));
                tv_download.setBackgroundResource(R.drawable.item_down);
                tv_signature.setVisibility(View.GONE);

            }

            tv_download.setEnabled(true);
            tv_download.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            pb_progress.setVisibility(View.GONE);
            tv_rate.setVisibility(View.INVISIBLE);
            tv_introduce.setVisibility(View.VISIBLE);
            rl_size_download_count.setVisibility(View.VISIBLE);
            ll_download_size_speed.setVisibility(View.GONE);
        }
        if (!mAppStatusView.isShown()) {
            mAppStatusView.setVisibility(View.VISIBLE);
            mPlaceHolder.setVisibility(View.VISIBLE);
        }

        Log.d(TAG, "" + app.title);
    }


    private void initTopic(TopicInfo info) {
        mTopicInfo = info;
        mTopTitle.setText(info.title);
        Log.d(TAG, "type " + info.type);
        Log.d(TAG, "title " + info.title);
        Log.d(TAG, "url " + info.third_url);
        Log.d(TAG, "softId " + info.softId);
        Log.d(TAG, "disable_link " + info.disable_link);

        if (!TextUtils.isEmpty(info.third_url)) {
            mWebView.loadUrl(info.third_url);
        }

        // mApps used by both subtype
        if (mApps == null) {
            mApps = new SparseArray<>();
        }

        setDisableLinkInWebView(info.disable_link > 0);
        Log.d(TAG, "disableLinkInWebView " + disableLinkInWebView);

        handleWebViewDownloadStatus();

        if (!(info.softId > 0)) {
            // WebView 专题
            mSubType = WebViewTopicActivity.TOPIC_SUB_TYPE_NORMAL;

            if (mPlaceMap == null) {
                mPlaceMap = new HashMap<String, String>();
            }

            if (mStatusMap == null) {
                mStatusMap = new HashMap<String, Integer>();
            }

            if (mDownloadURLMap == null) {
                mDownloadURLMap = new HashMap<String, String>();
            }

            if (mPackageNameMap == null) {
                mPackageNameMap = new HashMap<String, String>();
            }

            if (mCallbacks == null) {
                mCallbacks = new HashMap<String, ProgressCallback>();
            }

            if (STATUS2CODE == null) {
                STATUS2CODE = new HashMap<String, Integer>();
                int len = CODE2STATUS.length;
                for (int i = 0; i < len; i++) {
                    STATUS2CODE.put(CODE2STATUS[i], i);
                }
            }
            initWebViewButtons();
        } else {
            // 基于 WebView 专题的单应用专题
            mSubType = WebViewTopicActivity.TOPIC_SUB_TYPE_SINGLE_APP;
            Log.d(TAG, "softId > 0");
            TApier.get().getAppByID(info.softId).enqueue(appInfoCallback);

        }
    }

    private void updateWebViewButtons() {
        Log.d(TAG, "updateWebViewButtons");
        if (mPlaceMap != null && mPlaceMap.size() > 0) {
            for (String url : mPlaceMap.keySet()) {
                String btnId = mPlaceMap.get(url);
                DownloadInfo info = mDownloadManager.getDownloadInfo(url);
                if (info != null) {
                    Log.d(TAG, "btnId---" + btnId);
                    Log.d(TAG, "mStatus---" + info.mStatus);
                    int status = DownloadInfo2IntStatus(info);
                    callbackButtonInitStatus(btnId, status, 1, -1);
                } else {
                    if (mPackageNameMap != null) {
                        String linkedPackageName = mPackageNameMap.get(btnId);
                        if (!TextUtils.isEmpty(linkedPackageName)) {
                            int status = session.isInstalled(linkedPackageName) ? 10 : 0;
                            callbackButtonInitStatus(btnId, status, 1, -1);
                        }
                    }
                }
            }
        }
    }

    private int DownloadInfo2IntStatus(DownloadInfo info) {
        int result = -1;
        String text = "";
        switch (info.mStatus) {
            case 0:
            case Downloads.Impl.STATUS_PENDING:
                text = "等待中";
                break;
            case Downloads.Impl.STATUS_RUNNING:
            case Downloads.Impl.STATUS_RETRYING:
                text = "暂停";
                break;
            case Downloads.Impl.STATUS_QUEUED_FOR_WIFI:
                text = "直接下载";
                break;
            case Downloads.Impl.STATUS_PAUSED_BY_APP:
                text = "继续";
                break;
            case Downloads.Impl.STATUS_WAITING_TO_RETRY:
                text = "重试";
                break;
            case Downloads.Impl.STATUS_CHECKING:
                text = "检测中";
                break;
            case Downloads.Impl.STATUS_INSTALLING:
                text = "安装中";
                break;
            case Downloads.Impl.STATUS_DOWNLOAD_SUCCESS:
                App updateApp = DataCenterObserver.get(C.get()).getAppsInfoHandler().getUpdateApp(info.mPackageName);

                if (updateApp != null && updateApp.versionCode > info.mVersionCode) {
                    text = "升级";
                } else {
                    InstalledApp installedApp = DataCenterObserver.get(C.get()).getInstalledApp(info.mPackageName);
                    if (installedApp == null) {
                        if (!TextUtils.isEmpty(info.mFileName) && new File(info.mFileName).exists()) {
                            text = "安装";
                        } else {
                            text = "下载";
                        }
                    }
                    //升级的应用
                    else {
                        if (info.mVersionCode > installedApp.versionCode) {
                            if (!TextUtils.isEmpty(info.mFileName) && new File(info.mFileName).exists()) {
                                text = "安装";
                            } else {
                                // 打开
                                text = "升级";
                            }
                        }
                        //有的机器安装完成的广播接收到的特别慢
                        else {
                            text = "打开";
                        }
                    }
                }
                break;
            case Downloads.Impl.STATUS_INSTALLED:
                // 升级
                if (DataCenterObserver.get(C.get()).getAppsInfoHandler().checkInupdatelist(info.mPackageName)) {
                    text = "升级";
                } else {
                    InstalledApp installedApp = DataCenterObserver.get(C.get()).getInstalledApp(info.mPackageName);
                    if (installedApp == null) {
                        text = "下载";
                    } else {
                        text = "打开";
                    }
                }
                break;
            case Downloads.Impl.STATUS_CANCELED:
                break;

        }
        if (STATUS2CODE != null) {
            Integer c = STATUS2CODE.get(text);
            result = c == null ? -1 : c;
        }
        return result;
    }

    private void initWebViewButtons() {
        Log.d(TAG, "initWebViewButtons");
        if (mDownloadURLMap != null && mDownloadURLMap.size() > 0 && mCallbacks != null) {
            mCallbacks.clear();
            for (String url : mDownloadURLMap.values()) {
                Log.d(TAG, "download url " + url);
                DownloadInfo info = mDownloadManager.getDownloadInfo(url);
                if (info != null) {
                    ProgressCallback callback = new ProgressCallback();
                    callback.setTag(R.id.download_item, info);
                    callback.setTag(R.id.download_url, url);
                    callback.setTag(R.id.hold_activty, WebViewTopicActivity.this);
                    info.addProgressViews(callback);
                    info.notifyProgress(WebViewTopicActivity.this);
                    mCallbacks.put(url, callback); // add and hold to avoid be GC
                }
            }
        }
    }


    private void showError() {
        mWebView.setVisibility(View.INVISIBLE);
        showLoadFail();
    }

    class ProgressCallback extends IProgressCallback.ProgressCallbackImpl {

        private float progress;

        @Override
        public void showProgress(float progress) {
            this.progress = (float) (Math.floor(progress * 1000)) / 10;
            L.i(TAG, "Progress:" + this.progress);
        }

        @Override
        public void showCurrentSize(String size) {

        }

        @Override
        public void showStatus(DownloadInfo info) {
            Log.d(TAG, "showStatus");
            String text = "";
            boolean tvEnable = true;
            if (info == null) {
                return;
            }

            switch (info.mStatus) {
                case 0:
                case Downloads.Impl.STATUS_PENDING:
                    text = "等待中";
                    break;
                case Downloads.Impl.STATUS_RUNNING:
                case Downloads.Impl.STATUS_RETRYING:
                    text = "暂停";
                    break;
                case Downloads.Impl.STATUS_QUEUED_FOR_WIFI:
                    text = "直接下载";
                    break;
                case Downloads.Impl.STATUS_PAUSED_BY_APP:
                    text = "继续";
                    break;
                case Downloads.Impl.STATUS_WAITING_TO_RETRY:
                    text = "重试";
                    break;
                case Downloads.Impl.STATUS_CHECKING:
                    text = "检测中";
                    tvEnable = false;
                    break;
                case Downloads.Impl.STATUS_INSTALLING:
                    text = "安装中";
                    tvEnable = false;
                    break;
                case Downloads.Impl.STATUS_DOWNLOAD_SUCCESS:
                    App updateApp = DataCenterObserver.get(C.get()).getAppsInfoHandler().getUpdateApp(info.mPackageName);

                    if (updateApp != null && updateApp.versionCode > info.mVersionCode) {
                        text = "升级";
                    } else {
                        InstalledApp installedApp = DataCenterObserver.get(C.get()).getInstalledApp(info.mPackageName);
                        if (installedApp == null) {
                            if (!TextUtils.isEmpty(info.mFileName) && new File(info.mFileName).exists()) {
                                text = "安装";
                            } else {
                                text = "下载";
                            }
                        }
                        //升级的应用
                        else {
                            if (info.mVersionCode > installedApp.versionCode) {
                                if (!TextUtils.isEmpty(info.mFileName) && new File(info.mFileName).exists()) {
                                    text = "安装";
                                } else {
                                    // 打开
                                    text = "升级";
                                }
                            }
                            //有的机器安装完成的广播接收到的特别慢
                            else {
                                text = "打开";
                            }
                        }
                    }
                    break;
                case Downloads.Impl.STATUS_INSTALLED:
                    // 升级
                    if (DataCenterObserver.get(C.get()).getAppsInfoHandler().checkInupdatelist(info.mPackageName)) {
                        text = "升级";
                    } else {
                        InstalledApp installedApp = DataCenterObserver.get(C.get()).getInstalledApp(info.mPackageName);
                        if (installedApp == null) {
                            text = "下载";
                        } else {
                            text = "打开";
                        }
                    }
                    break;
                case Downloads.Impl.STATUS_CANCELED:
                    return;

            }

            String url = info.mUrl;
            if (!TextUtils.isEmpty(url) && mPlaceMap != null && mPlaceMap.size() > 0 && !TextUtils.isEmpty(text)) {
                String bid = mPlaceMap.get(url);
                JSDownloadButtonStatus status = new JSDownloadButtonStatus();
                status.btnId = bid;
                status.intStatus = STATUS2CODE.get(text);
                status.isEnabled = tvEnable ? 1 : 0;
                status.percents = this.progress;
                callbackButtonInitStatus(status.btnId, status.intStatus, status.isEnabled, status.percents);
            }
        }

        @Override
        public void showSpeed(String speedStatus) {

        }

        @Override
        public void setVisible(boolean downloading) {

        }
    }

    private void setTitleIcon(int recomIco, TextView tv_recommend_icon) {
        switch (recomIco) {
            case 1:
                tv_recommend_icon.setText("新品");
                tv_recommend_icon.setBackgroundResource(R.drawable.new_title);
                tv_recommend_icon.setVisibility(View.VISIBLE);
                break;
            case 2:
                tv_recommend_icon.setText("首发");
                tv_recommend_icon.setBackgroundResource(R.drawable.first_title);
                tv_recommend_icon.setVisibility(View.VISIBLE);
                break;
            case 4:
                tv_recommend_icon.setText("精品");
                tv_recommend_icon.setBackgroundResource(R.drawable.special_title);
                tv_recommend_icon.setVisibility(View.VISIBLE);
                break;
            case 5:
                tv_recommend_icon.setText("热门");
                tv_recommend_icon.setBackgroundResource(R.drawable.hot_title);
                tv_recommend_icon.setVisibility(View.VISIBLE);
                break;
            case 6:
                tv_recommend_icon.setText("热搜");
                tv_recommend_icon.setBackgroundResource(R.drawable.trend_title);
                tv_recommend_icon.setVisibility(View.VISIBLE);
                break;
            case 0:
            case 3:
            default:
                tv_recommend_icon.setVisibility(View.GONE);
                break;
        }
    }

    private class MyWebChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100) {
                hideLoading();
                mWebView.setVisibility(View.VISIBLE);
            }
            super.onProgressChanged(view, newProgress);
        }
    }

    private class MyWebViewClient extends WebViewClient {

        @Override
        public void onPageFinished(WebView view, String url) {
            Log.d(TAG, "onPageFinished");
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            Log.d(TAG, "onReceivedError---" + errorCode);
            showLoadFail();
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.d(TAG, "url---" + url);
            if (url.startsWith("http://") || url.startsWith("https://")) {
                return false;
            } else {
                // bypass
                return true;
            }
        }
    }

    class JSDownloadButtonStatus {
        String btnId;
        int intStatus;
        int isEnabled;
        float percents;
    }

    public void onEventMainThread(BindPhoneSuccessEvent event) {
        setUserInfo(getJSONString());
    }

    public void onEventMainThread(SignInResultEvent event) {
        if (event.success) {
            setUserInfo(getJSONString());
        }
    }

    public void onEventMainThread(DownStartEvent event) {
        String sid = event.mSid;
        if (!TextUtils.isEmpty(sid) && mStatusMap.containsKey(sid)) {
            Toast.makeText(C.get(), "开始下载...", Toast.LENGTH_SHORT).show();
        }
    }
}
*/
