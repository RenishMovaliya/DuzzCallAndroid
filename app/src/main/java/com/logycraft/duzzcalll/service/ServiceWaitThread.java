package com.logycraft.duzzcalll.service;


import com.logycraft.duzzcalll.Util.LinphoneUtils;

/* loaded from: classes2.dex */
public class ServiceWaitThread extends Thread {
    private ServiceWaitThreadListener mListener;

    public ServiceWaitThread(ServiceWaitThreadListener listener) {
        this.mListener = listener;
    }

    @Override // java.lang.Thread, java.lang.Runnable
    public void run() {
        while (!LinphoneService.isReady()) {
            try {
                sleep(30L);
            } catch (InterruptedException e) {
                throw new RuntimeException("waiting thread sleep() has been interrupted");
            }
        }
        if (this.mListener != null) {
            LinphoneUtils.dispatchOnUIThread(new Runnable() { // from class: com.duzzelcall.managinig.service.ServiceWaitThread.1
                @Override // java.lang.Runnable
                public void run() {
                    ServiceWaitThread.this.mListener.onServiceReady();
                }
            });
        }
    }
}
