package com.ymnet.onekeyclean.cleanmore.wechat.component;

import android.content.Context;
import android.util.Log;

import com.google.gson.JsonSerializer;
import com.ymnet.onekeyclean.cleanmore.wechat.Navigator;
import com.ymnet.onekeyclean.cleanmore.wechat.activity.BaseFragmentActivity_MembersInjector;
import com.ymnet.onekeyclean.cleanmore.wechat.activity.Navigator_Factory;
import com.ymnet.onekeyclean.cleanmore.wechat.modules.ApplicationModule;
import com.ymnet.onekeyclean.cleanmore.wechat.modules.ThreadExecutor;
import com.ymnet.onekeyclean.cleanmore.wechat.modules.UserRepository;
import com.ymnet.onekeyclean.cleanmore.wechat.view.BaseFragmentActivity;

import javax.annotation.Generated;
import javax.inject.Provider;

import dagger.MembersInjector;
import dagger.internal.MembersInjectors;
import dagger.internal.ScopedProvider;

import static android.content.ContentValues.TAG;

@Generated("dagger.internal.codegen.ComponentProcessor")
public final class DaggerApplicationComponent implements ApplicationComponent {
  private Provider<Navigator>                   navigatorProvider;
  private MembersInjector<BaseFragmentActivity> baseFragmentActivityMembersInjector;
  private Provider<Context>                     provideApplicationContextProvider;
//  private Provider<JobExecutor>                 jobExecutorProvider;
  private Provider<ThreadExecutor>              provideThreadExecutorProvider;
//  private Provider<UIThread>                    uIThreadProvider;
//  private Provider<PostExecutionThread>         providePostExecutionThreadProvider;
//  private Provider<com.market2345.usercenter.cache.JsonSerializer> jsonSerializerProvider;
//  private Provider<FileManager> fileManagerProvider;
//  private Provider<UserCacheImpl>               userCacheImplProvider;
//  private Provider<UserCache>                   provideUserCacheProvider;
//  private Provider<UserDataStoreFactory>        userDataStoreFactoryProvider;
//  private Provider<UserEntityDataMapper>        userEntityDataMapperProvider;
//  private Provider<UserDataRepository>          userDataRepositoryProvider;
  private Provider<UserRepository>              provideUserRepositoryProvider;
//  private Provider<TaskManagerImpl>             taskManagerImplProvider;
//  private Provider<TaskManager>                 provideTaskManagerProvider;
  private Provider<JsonSerializer>              jsonSerializerProvider1;
//  private Provider<SearchHotRecommendCacheImpl> searchHotRecommendCacheImplProvider;
//  private Provider<SearchHotRecommendCache>     provideSearchRecommendCacheProvider;
//  private Provider<SearchDataStoreFactory>      searchDataStoreFactoryProvider;
//  private Provider<SearchDataRepository>        searchDataRepositoryProvider;
//  private Provider<SearchRepository>            provideSearchRepositoryProvider;
//  private Provider<SplashPageDataStoreFactory>  splashPageDataStoreFactoryProvider;
//  private Provider<SplashPageDataMapper>        splashPageDataMapperProvider;
//  private Provider<SplashPageDataRepository> splashPageDataRepositoryProvider;
//  private Provider<SplashPageRepository> provideSplashPageRepositoryProvider;

  private DaggerApplicationComponent(Builder builder) {  
    assert builder != null;
    initialize(builder);
  }

  public static Builder builder() {  
    return new Builder();
  }

  private void initialize(final Builder builder) {  
    this.navigatorProvider = ScopedProvider.create(Navigator_Factory.create());
    this.baseFragmentActivityMembersInjector = BaseFragmentActivity_MembersInjector.create((MembersInjector) MembersInjectors.noOp(), navigatorProvider);
//    this.provideApplicationContextProvider = ScopedProvider.create(ApplicationModule_ProvideApplicationContextFactory.create(builder.applicationModule));
//    this.jobExecutorProvider = ScopedProvider.create(JobExecutor_Factory.create());
//    this.provideThreadExecutorProvider = ScopedProvider.create(ApplicationModule_ProvideThreadExecutorFactory.create(builder.applicationModule, jobExecutorProvider));
//    this.uIThreadProvider = ScopedProvider.create(UIThread_Factory.create());
//    this.providePostExecutionThreadProvider = ScopedProvider.create(ApplicationModule_ProvidePostExecutionThreadFactory.create(builder.applicationModule, uIThreadProvider));
//    this.jsonSerializerProvider = ScopedProvider.create(com.market2345.usercenter.cache.JsonSerializer_Factory.create());
//    this.fileManagerProvider = ScopedProvider.create(FileManager_Factory.create());
//    this.userCacheImplProvider = ScopedProvider.create(UserCacheImpl_Factory.create(provideApplicationContextProvider, jsonSerializerProvider, fileManagerProvider, provideThreadExecutorProvider));
//    this.provideUserCacheProvider = ScopedProvider.create(ApplicationModule_ProvideUserCacheFactory.create(builder.applicationModule, userCacheImplProvider));
//    this.userDataStoreFactoryProvider = ScopedProvider.create(UserDataStoreFactory_Factory.create(provideApplicationContextProvider, provideUserCacheProvider));
//    this.userEntityDataMapperProvider = ScopedProvider.create(UserEntityDataMapper_Factory.create());
//    this.userDataRepositoryProvider = ScopedProvider.create(UserDataRepository_Factory.create(userDataStoreFactoryProvider, userEntityDataMapperProvider));
//    this.provideUserRepositoryProvider = ScopedProvider.create(ApplicationModule_ProvideUserRepositoryFactory.create(builder.applicationModule, userDataRepositoryProvider));
//    this.taskManagerImplProvider = ScopedProvider.create(TaskManagerImpl_Factory.create(provideThreadExecutorProvider, providePostExecutionThreadProvider));
//    this.provideTaskManagerProvider = ScopedProvider.create(ApplicationModule_ProvideTaskManagerFactory.create(builder.applicationModule, taskManagerImplProvider));
//    this.jsonSerializerProvider1 = ScopedProvider.create(JsonSerializer_Factory.create());
//    this.searchHotRecommendCacheImplProvider = ScopedProvider.create(SearchHotRecommendCacheImpl_Factory.create(provideApplicationContextProvider, jsonSerializerProvider1, fileManagerProvider, provideThreadExecutorProvider));
//    this.provideSearchRecommendCacheProvider = ScopedProvider.create(ApplicationModule_ProvideSearchRecommendCacheFactory.create(builder.applicationModule, searchHotRecommendCacheImplProvider));
//    this.searchDataStoreFactoryProvider = ScopedProvider.create(SearchDataStoreFactory_Factory.create(provideSearchRecommendCacheProvider));
//    this.searchDataRepositoryProvider = ScopedProvider.create(SearchDataRepository_Factory.create(searchDataStoreFactoryProvider));
//    this.provideSearchRepositoryProvider = ScopedProvider.create(ApplicationModule_ProvideSearchRepositoryFactory.create(builder.applicationModule, searchDataRepositoryProvider));
//    this.splashPageDataStoreFactoryProvider = ScopedProvider.create(SplashPageDataStoreFactory_Factory.create(provideApplicationContextProvider));
//    this.splashPageDataMapperProvider = ScopedProvider.create(SplashPageDataMapper_Factory.create());
//    this.splashPageDataRepositoryProvider = ScopedProvider.create(SplashPageDataRepository_Factory.create(splashPageDataStoreFactoryProvider, splashPageDataMapperProvider));
//    this.provideSplashPageRepositoryProvider = ScopedProvider.create(ApplicationModule_ProvideSplashPageRepositoryFactory.create(builder.applicationModule, splashPageDataRepositoryProvider));
  }

  @Override
  public void inject(BaseFragmentActivity base) {
    Log.d(TAG, "inject: "+baseFragmentActivityMembersInjector);
    baseFragmentActivityMembersInjector.injectMembers(base);
  }

  @Override
  public Context context() {  
    return provideApplicationContextProvider.get();
  }

  /*@Override
  public ThreadExecutor threadExecutor() {
    return provideThreadExecutorProvider.get();
  }

  @Override
  public PostExecutionThread postExecutionThread() {  
    return providePostExecutionThreadProvider.get();
  }

  @Override
  public UserRepository userRepository() {
    return provideUserRepositoryProvider.get();
  }

  @Override
  public TaskManager taskManager() {  
    return provideTaskManagerProvider.get();
  }

  @Override
  public SearchRepository searchRepository() {  
    return provideSearchRepositoryProvider.get();
  }

  @Override
  public SplashPageRepository splashPageRepository() {  
    return provideSplashPageRepositoryProvider.get();
  }*/

  public static final class Builder {
    private ApplicationModule applicationModule;
  
    private Builder() {  
    }
  
    public ApplicationComponent build() {  
      if (applicationModule == null) {
        throw new IllegalStateException("applicationModule must be set");
      }
      return new DaggerApplicationComponent(this);
    }
  
    public Builder applicationModule(ApplicationModule applicationModule) {  
      if (applicationModule == null) {
        throw new NullPointerException("applicationModule");
      }
      this.applicationModule = applicationModule;
      return this;
    }
  }
}

