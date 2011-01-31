package com.pyx4j.client.demo.client.mvp;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;
import com.google.inject.Inject;
import com.google.inject.Provider;

import com.pyx4j.client.demo.client.activity.LogoActivity;
import com.pyx4j.client.demo.client.activity.SayGoodbyeActivity;

public class LogoActivityMapper implements ActivityMapper {

    Provider<LogoActivity> logoActivityProvider;

    Provider<SayGoodbyeActivity> sayGoodbyeActivityProvider;

    @Inject
    public LogoActivityMapper(Provider<LogoActivity> logoActivityProvider) {
        super();
        this.logoActivityProvider = logoActivityProvider;
    }

    @Override
    public Activity getActivity(Place place) {
        return logoActivityProvider.get();
    }

}
