package com.pyx4j.client.demo.client.ui;

import com.google.gwt.inject.client.AbstractGinModule;

import com.pyx4j.widgets.client.style.Theme;

public class ViewModule extends AbstractGinModule {

    @Override
    protected void configure() {
        bind(SiteView.class);

        bind(GoodbyeView.class).to(GoodbyeViewImpl.class);
        bind(HelloView.class).to(HelloViewImpl.class);
        bind(SayHelloView.class).to(SayHelloViewImpl.class);
        bind(SayGoodbyeView.class).to(SayGoodbyeViewImpl.class);

        bind(LogoView.class).to(LogoViewImpl.class);
        bind(FooterView.class).to(FooterViewImpl.class);
        bind(MainNavigView.class).to(MainNavigViewImpl.class);
        bind(MainContentView.class).to(MainContentViewImpl.class);

        bind(Theme.class).to(DefaultTheme.class);

    }

}
