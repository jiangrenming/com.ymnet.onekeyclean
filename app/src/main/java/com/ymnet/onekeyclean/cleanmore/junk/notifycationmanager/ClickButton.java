package com.ymnet.onekeyclean.cleanmore.junk.notifycationmanager;

import android.content.Intent;

public class ClickButton
{
    public int viewId;
    
    public Intent actionIntent;
    
    public String titleString;
    
    public int iconId;
    
    public ClickButton()
    {
        
    }
    
    public ClickButton(int viewId, Intent intent)
    {
        
        this.viewId = viewId;
        this.actionIntent = intent;
    }
    
    public ClickButton(int iconId, Intent intent, String title)
    {
        this.iconId = iconId;
        this.actionIntent = intent;
        this.titleString = title;
        
    }
    
}
