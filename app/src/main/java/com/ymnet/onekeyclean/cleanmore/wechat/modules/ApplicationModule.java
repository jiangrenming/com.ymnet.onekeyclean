package com.ymnet.onekeyclean.cleanmore.wechat.modules;


import android.content.Context;

import com.ymnet.onekeyclean.MarketApplication;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Dagger module that provides objects which will live during the application lifecycle.
 */
@Module
public class ApplicationModule {
    private final MarketApplication marketApplication;

    public ApplicationModule(MarketApplication marketApplication) {
        this.marketApplication = marketApplication;
    }

    @Provides
    @Singleton
    Context provideApplicationContext() {
        return this.marketApplication;
    }

   /* @Provides
    @Singleton
    ThreadExecutor provideThreadExecutor(JobExecutor jobExecutor) {
        return jobExecutor;
    }

    @Provides
    @Singleton
    PostExecutionThread providePostExecutionThread(UIThread uiThread) {
        return uiThread;
    }

    @Provides
    @Singleton
    TaskManager provideTaskManager(TaskManagerImpl taskManager) {
        return taskManager;
    }

    @Provides
    @Singleton
    UserCache provideUserCache(UserCacheImpl userCache) {
        return userCache;
    }

    @Provides
    @Singleton
    UserRepository provideUserRepository(UserDataRepository userDataRepository) {
        return userDataRepository;
    }

    @Provides
    @Singleton
    SearchRepository provideSearchRepository(SearchDataRepository searchDataRepository) {
        return searchDataRepository;
    }

    @Provides
    @Singleton
    SearchHotRecommendCache provideSearchRecommendCache(SearchHotRecommendCacheImpl searchHotRecommendCache) {
        return searchHotRecommendCache;
    }

    @Provides
    @Singleton
    SplashPageRepository provideSplashPageRepository(SplashPageDataRepository splashPageDataRepository) {
        return splashPageDataRepository;
    }*/

}