package com.ymnet.onekeyclean.cleanmore.notification;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;

public class AssistantServiceHelper extends AccessibilityService {


    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // TODO: 2017/5/17 0017  
        int i = event.describeContents();
    }

    @Override
    public void onInterrupt() {

    }
}
