package com.logycraft.duzzcalll.helper.notifications;

import android.graphics.Bitmap;
import android.net.Uri;

/* loaded from: classes2.dex */
public class NotifiableMessage {
    private final String mFileMime;
    private final Uri mFilePath;
    private final String mMessage;
    private final String mSender;
    private Bitmap mSenderBitmap;
    private final long mTime;

    public NotifiableMessage(String message, String sender, long time, Uri filePath, String fileMime) {
        this.mMessage = message;
        this.mSender = sender;
        this.mTime = time;
        this.mFilePath = filePath;
        this.mFileMime = fileMime;
    }

    public String getMessage() {
        return this.mMessage;
    }

    public String getSender() {
        return this.mSender;
    }

    public long getTime() {
        return this.mTime;
    }

    public Bitmap getSenderBitmap() {
        return this.mSenderBitmap;
    }

    public void setSenderBitmap(Bitmap bm) {
        this.mSenderBitmap = bm;
    }

    public Uri getFilePath() {
        return this.mFilePath;
    }

    public String getFileMime() {
        return this.mFileMime;
    }
}
