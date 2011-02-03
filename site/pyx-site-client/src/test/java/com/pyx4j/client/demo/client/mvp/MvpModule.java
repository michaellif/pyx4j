package com.pyx4j.client.demo.client.mvp;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import com.pyx4j.client.demo.client.place.AppPlaceHistoryMapper;
import com.pyx4j.client.demo.client.place.LandingPlace;

public class MvpModule extends AbstractGinModule {

    @Override
    protected void configure() {
        bind(EventBus.class).to(SimpleEventBus.class).in(Singleton.class);
        bind(PlaceHistoryMapper.class).to(AppPlaceHistoryMapper.class).in(Singleton.class);

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
    public PlaceHistoryHandler getHistoryHandler(PlaceController placeController, PlaceHistoryMapper historyMapper, EventBus eventBus) {
        PlaceHistoryHandler historyHandler = new PlaceHistoryHandler(historyMapper);
        historyHandler.register(placeController, eventBus, new LandingPlace());

        return historyHandler;
    }

    @Provides
    @Singleton
    public PlaceController getPlaceController(EventBus eventBus) {
        return new PlaceController(eventBus);
    }

}
