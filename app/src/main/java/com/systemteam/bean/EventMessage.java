package com.systemteam.bean;

/**
 * Created by adminws on 2016/9/12.
 */
public class EventMessage {
    public static final int ACTION_GAMEOVER = 1;
    private boolean isFree = false;    //是否免费使用
    private int action;//1表示刷新

    public EventMessage(boolean isFree, int action) {
        this.isFree = isFree;
        this.action = action;
    }

    public boolean getIsFree() {
        return isFree;
    }

    public int getAction() {
        return action;
    }
}
