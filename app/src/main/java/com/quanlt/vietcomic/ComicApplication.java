package com.quanlt.vietcomic;

import android.app.Application;

import com.quanlt.vietcomic.di.component.DaggerNetworkComponent;
import com.quanlt.vietcomic.di.component.NetworkComponent;
import com.quanlt.vietcomic.di.module.ApplicationModule;
import com.quanlt.vietcomic.di.module.NetworkModule;
import com.squareup.leakcanary.LeakCanary;

/**
 * Created by Le Thuong Quan on 15/10/2016.
 */
public class ComicApplication extends Application {
    private NetworkComponent networkComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        networkComponent = DaggerNetworkComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .networkModule(new NetworkModule())
                .build();
        if (!LeakCanary.isInAnalyzerProcess(this)) {
            LeakCanary.install(this);
        }
    }

    public NetworkComponent getNetworkComponent() {
        return networkComponent;
    }
}

