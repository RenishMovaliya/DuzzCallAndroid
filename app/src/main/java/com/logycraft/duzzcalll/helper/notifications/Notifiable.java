package com.logycraft.duzzcalll.helper.notifications;

import java.util.ArrayList;
import java.util.List;

/* loaded from: classes2.dex */
public class Notifiable {
    private String mGroupTitle;
    private String mLocalIdentity;
    private String mMyself;
    private final int mNotificationId;
    private List<NotifiableMessage> mMessages = new ArrayList();
    private boolean mIsGroup = false;
    private int mIconId = 0;
    private int mTextId = 0;

    public Notifiable(int id) {
        this.mNotificationId = id;
    }

    public int getNotificationId() {
        return this.mNotificationId;
    }

    public void resetMessages() {
        this.mMessages = new ArrayList();
    }

    public void addMessage(NotifiableMessage notifMessage) {
        this.mMessages.add(notifMessage);
    }

    public List<NotifiableMessage> getMessages() {
        return this.mMessages;
    }

    public boolean isGroup() {
        return this.mIsGroup;
    }

    public void setIsGroup(boolean isGroup) {
        this.mIsGroup = isGroup;
    }

    public String getGroupTitle() {
        return this.mGroupTitle;
    }

    public void setGroupTitle(String title) {
        this.mGroupTitle = title;
    }

    public String getMyself() {
        return this.mMyself;
    }

    public void setMyself(String myself) {
        this.mMyself = myself;
    }

    public String getLocalIdentity() {
        return this.mLocalIdentity;
    }

    public void setLocalIdentity(String localIdentity) {
        this.mLocalIdentity = localIdentity;
    }

    public int getIconResourceId() {
        return this.mIconId;
    }

    public void setIconResourceId(int id) {
        this.mIconId = id;
    }

    public int getTextResourceId() {
        return this.mTextId;
    }

    public void setTextResourceId(int id) {
        this.mTextId = id;
    }

    public String toString() {
        return "Id: " + this.mNotificationId + ", local identity: " + this.mLocalIdentity + ", myself: " + this.mMyself + ", isGrouped: " + this.mIsGroup;
    }
}
