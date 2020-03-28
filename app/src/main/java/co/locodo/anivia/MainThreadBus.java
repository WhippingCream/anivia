package co.locodo.anivia;

import android.os.Handler;
import android.os.Looper;

import com.squareup.otto.Bus;

public class MainThreadBus extends Bus {
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    public void register(final Object event) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            super.register(event);
        } else {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    MainThreadBus.super.register(event);
                }
            });
        }
    }

    @Override
    public void unregister(final Object event) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            super.unregister(event);
        } else {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    MainThreadBus.super.unregister(event);
                }
            });
        }
    }

    @Override
    public void post(final Object event) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            super.post(event);
        } else {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    MainThreadBus.super.post(event);
                }
            });
        }
    }
}
