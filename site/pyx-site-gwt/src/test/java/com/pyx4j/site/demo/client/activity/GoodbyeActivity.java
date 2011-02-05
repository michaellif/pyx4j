package com.pyx4j.site.demo.client.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

import com.pyx4j.site.demo.client.ui.GoodbyeView;

public class GoodbyeActivity extends AbstractActivity {

    private final GoodbyeView view;

    @Inject
    public GoodbyeActivity(GoodbyeView view) {
        this.view = view;
    }

    public GoodbyeActivity withPlace(Place place) {
        return this;
    }

    @Override
    public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
        containerWidget.setWidget(view);
    }

}