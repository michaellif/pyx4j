package com.pyx4j.site.demo.client.mvp;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;
import com.google.inject.Inject;
import com.google.inject.Provider;

import com.pyx4j.site.demo.client.activity.LogoActivity;

public class LogoActivityMapper implements ActivityMapper {

    Provider<LogoActivity> logoActivityProvider;

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
