package com.pyx4j.site.demo.client.mvp;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;
import com.google.inject.Inject;
import com.google.inject.Provider;

import com.pyx4j.site.demo.client.activity.MainNavigActivity;

public class MainNavigActivityMapper implements ActivityMapper {

    Provider<MainNavigActivity> mainNavigActivityProvider;

    @Inject
    public MainNavigActivityMapper(final Provider<MainNavigActivity> mainNavigActivityProvider) {
        super();
        this.mainNavigActivityProvider = mainNavigActivityProvider;
    }

    @Override
    public Activity getActivity(Place place) {
        return mainNavigActivityProvider.get().withPlace(place);
    }

}
