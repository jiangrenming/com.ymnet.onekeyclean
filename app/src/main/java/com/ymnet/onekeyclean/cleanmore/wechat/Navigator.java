package com.ymnet.onekeyclean.cleanmore.wechat;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class Navigator {

    @Inject
    public Navigator() {}

    /*public void navigateToUpgradePage(@NonNull Activity activity) {
        Intent intent = new Intent(activity, LoginActivity.class);
        if (Account.getExistedInstance().isLocalAccountSignin(C.get())) {
            intent = new Intent(activity, UpdateAppActivity.class);
        }

        activity.startActivity(intent);
    }

    public void navigateToRecommendRankPage(@NonNull Activity activity) {
        Intent intent = new Intent(activity, LoginActivity.class);
        if (Account.getExistedInstance().isLocalAccountSignin(C.get())) {
            intent = new Intent(activity, HomeTabActivity.class);
            intent.putExtra(HomeTabActivity.NOTIFI, HomeUtils.OPEN_RECOMMEND_RANK);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }

        activity.startActivity(intent);
    }

    public void navigateToSearchPage(@NonNull Activity activity) {
        Intent intent = new Intent(activity, LoginActivity.class);
        if (Account.getExistedInstance().isLocalAccountSignin(C.get())) {
            intent = new Intent(activity, HomeTabActivity.class);
            intent.putExtra(HomeTabActivity.NOTIFI, HomeUtils.NOTIFY_VALUE_OPENHOME_TO_SEARCH);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }

        activity.startActivity(intent);
    }

    public void navigateToBindingMobilePage(@NonNull Activity activity) {
        Intent intent = new Intent(activity, LoginActivity.class);
        if (Account.getExistedInstance().isLocalAccountSignin(C.get())) {
            intent = new Intent(activity, BindPhoneActivity.class);
        }

        activity.startActivity(intent);
    }

    public void navigateToCleanMobilePage(@NonNull Activity activity) {
        Intent intent = new Intent(activity, LoginActivity.class);
        if (Account.getExistedInstance().isLocalAccountSignin(C.get())) {
            intent = new Intent(activity, SilverActivity.class);
        }

        activity.startActivity(intent);
    }

    public void navigateToLoginPage(@NonNull Activity activity) {
        Intent intent = new Intent(activity, LoginActivity.class);
        activity.startActivity(intent);
    }

    public void navigateToAppDetailsPage(@NonNull Activity activity, @NonNull App app) {
        Intent intent = new Intent(activity, DetailActivity.class);
        if(app!=null && !TextUtils.isEmpty(app.detailDownloadPushEvent)){
            intent.putExtra("clicktoevent",app.detailDownloadPushEvent);
        }
        intent.putExtra(App.class.getSimpleName(), app);
        activity.startActivity(intent);
    }

    public void navigateToTagList(@NonNull Activity activity, @NonNull String tagName) {
        if (!TextUtils.isEmpty(tagName)) {
//            StatisticSpec.sendEvent(StatisticEventContants.tag);

            Intent intent = new Intent(activity, AppListActivity.class)
                    .putExtra("from",AppListActivity.FROM_SEARCH_TAG);
            intent.putExtra(AppListActivity.NAME, tagName);
            activity.startActivity(intent);
        }
    }

    public void navigateToDownloadPage(@NonNull Activity activity) {
        Intent intentTo = new Intent(activity, DownloadActivity.class);
        intentTo.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intentTo);
    }*/
}
