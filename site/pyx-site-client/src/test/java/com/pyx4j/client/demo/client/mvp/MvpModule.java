package com.pyx4j.client.demo.client.mvp;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import com.pyx4j.client.demo.client.place.SiteMap;

public class MvpModule extends AbstractGinModule {

    @Override
    protected void configure() {
        bind(EventBus.class).to(SimpleEventBus.class).in(Singleton.class);

        bind(LogoActivityMapper.class);
        bind(ActionsActivityMapper.class);
        bind(MainNavigActivityMapper.class);

        bind(Center1ActivityMapper.class);
        bind(Center2ActivityMapper.class);
        bind(Center3ActivityMapper.class);

        bind(Left1ActivityMapper.class);
        bind(Left2ActivityMapper.class);

        bind(Right1ActivityMapper.class);
        bind(Right2ActivityMapper.class);

        bind(BottomActivityMapper.class);

    }

    @Provides
    @Singleton
    public PlaceHistoryHandler getHistoryHandler(PlaceController placeController, EventBus eventBus) {
        PlaceHistoryMapper historyMapper = GWT.create(PlaceHistoryMapper.class);
        PlaceHistoryHandler historyHandler = new PlaceHistoryHandler(historyMapper);
        historyHandler.register(placeController, eventBus, new SiteMap.Landing());

        return historyHandler;
    }

    @Provides
    @Singleton
    public PlaceController getPlaceController(EventBus eventBus) {
        return new PlaceController(eventBus);
    }

}
