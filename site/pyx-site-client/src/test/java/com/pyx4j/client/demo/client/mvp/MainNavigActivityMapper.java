package com.pyx4j.client.demo.client.mvp;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;
import com.google.inject.Inject;
import com.google.inject.Provider;

import com.pyx4j.client.demo.client.activity.GoodbyeActivity;
import com.pyx4j.client.demo.client.activity.HelloActivity;
import com.pyx4j.client.demo.client.place.GoodbyePlace;
import com.pyx4j.client.demo.client.place.HelloPlace;

public class MainNavigActivityMapper implements ActivityMapper {

    Provider<HelloActivity> helloActivityProvider;

    Provider<GoodbyeActivity> goodbyeActivityProvider;

    @Inject
    public MainNavigActivityMapper(final Provider<HelloActivity> helloActivityProvider, final Provider<GoodbyeActivity> goodbyeActivityProvider) {
        super();
        this.helloActivityProvider = helloActivityProvider;
        this.goodbyeActivityProvider = goodbyeActivityProvider;
    }

    @Override
    public Activity getActivity(Place place) {
        if (place instanceof GoodbyePlace) {
            return goodbyeActivityProvider.get().withPlace((GoodbyePlace) place);
        } else {
            return helloActivityProvider.get().withPlace((HelloPlace) place);
        }
    }

}
