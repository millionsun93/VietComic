package com.quanlt.vietcomic.di.component;

import com.quanlt.vietcomic.di.module.ApplicationModule;
import com.quanlt.vietcomic.ui.MainActivity;
import com.quanlt.vietcomic.di.module.NetworkModule;
import com.quanlt.vietcomic.ui.detail.ComicDetailActivity;
import com.quanlt.vietcomic.ui.detail.ComicDetailFragment;
import com.quanlt.vietcomic.ui.grid.ComicFragment;
import com.quanlt.vietcomic.ui.reader.ReaderActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {ApplicationModule.class, NetworkModule.class})
public interface NetworkComponent {
    void inject(MainActivity mainActivity);
    void inject(ComicFragment comicFragment);
    void inject(ComicDetailFragment comicDetailFragment);
    void inject(ComicDetailActivity comicDetailActivity);
    void inject(ReaderActivity readerActivity);
}
