package com.ymnet.onekeyclean.cleanmore.notification;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;

public class AssistantServiceHelper extends AccessibilityService {


    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        int i = event.describeContents();
    }

    @Override
    public void onInterrupt() {

    }
}
