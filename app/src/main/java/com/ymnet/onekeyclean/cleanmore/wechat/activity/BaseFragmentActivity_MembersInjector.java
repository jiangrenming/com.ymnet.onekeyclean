package com.ymnet.onekeyclean.cleanmore.wechat.activity;

import android.support.v4.app.FragmentActivity;

import com.ymnet.onekeyclean.cleanmore.wechat.Navigator;
import com.ymnet.onekeyclean.cleanmore.wechat.view.BaseFragmentActivity;

import javax.annotation.Generated;
import javax.inject.Provider;

import dagger.MembersInjector;

@Generated("dagger.internal.codegen.ComponentProcessor")
public final class BaseFragmentActivity_MembersInjector implements MembersInjector<BaseFragmentActivity> {
  private final MembersInjector<FragmentActivity> supertypeInjector;
  private final Provider<Navigator>               navigatorProvider;

  public BaseFragmentActivity_MembersInjector(MembersInjector<FragmentActivity> supertypeInjector, Provider<Navigator> navigatorProvider) {
    assert supertypeInjector != null;
    this.supertypeInjector = supertypeInjector;
    assert navigatorProvider != null;
    this.navigatorProvider = navigatorProvider;
  }

  @Override
  public void injectMembers(BaseFragmentActivity instance) {  
    if (instance == null) {
      throw new NullPointerException("Cannot inject members into a null reference");
    }
    supertypeInjector.injectMembers(instance);
    instance.navigator = navigatorProvider.get();
  }

  public static MembersInjector<BaseFragmentActivity> create(MembersInjector<FragmentActivity> supertypeInjector, Provider<Navigator> navigatorProvider) {
      return new BaseFragmentActivity_MembersInjector(supertypeInjector, navigatorProvider);
  }
}

