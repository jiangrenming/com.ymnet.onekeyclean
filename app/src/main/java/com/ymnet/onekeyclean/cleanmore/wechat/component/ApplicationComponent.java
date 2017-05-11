package com.ymnet.onekeyclean.cleanmore.wechat.component;

import android.content.Context;

import com.ymnet.onekeyclean.cleanmore.wechat.modules.ApplicationModule;
import com.ymnet.onekeyclean.cleanmore.wechat.view.BaseFragmentActivity;

import javax.inject.Singleton;

import dagger.Component;

/**
 * A component whose lifetime is the life of the application.
 */
@Singleton // Constraints this component to one-per-application or unscoped bindings.
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {
    void inject(BaseFragmentActivity base);

    //Exposed to sub-graphs.
    Context context();

   /* ThreadExecutor threadExecutor();

    PostExecutionThread postExecutionThread();

    UserRepository userRepository();
    TaskManager taskManager();
    SearchRepository searchRepository();
    SplashPageRepository splashPageRepository();*/
}
