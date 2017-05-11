package com.ymnet.onekeyclean.cleanmore.junk.mode;


import android.graphics.drawable.Drawable;

public class JunkChild {
    public long     size;
    public Drawable icon;
    public int      select;//0->false 1->true
    public String   name;
    private boolean defaultSelect=true;//没有暴露外部改变方法 ，外部无法改变 自身控制
    public int getSelect() {
        return select;
    }

    public void setSelect(int select) {
        if(this.select!=select){
            defaultSelect=!defaultSelect;
        }
        this.select=select;

    }

    public boolean getDefaultSelect() {
        return defaultSelect;
    }

    @Override
    public String toString() {
        return "JunkChild{" +
                "size=" + size +
                ", icon=" + icon +
                ", select=" + select +
                ", name='" + name + '\'' +
                ", defaultSelect=" + defaultSelect +
                '}';
    }
}

