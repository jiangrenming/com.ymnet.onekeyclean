package com.ymnet.onekeyclean.cleanmore.wechat.modules;


import android.database.Observable;

import java.util.List;

import bolts.Task;

public interface UserRepository {
    Observable<List<Task>> tasks(final String userId, final String passId, final String token);
}
