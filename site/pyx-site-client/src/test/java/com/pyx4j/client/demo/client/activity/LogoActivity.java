package com.pyx4j.client.demo.client.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

import com.pyx4j.client.demo.client.place.GoodbyePlace;
import com.pyx4j.client.demo.client.ui.LogoView;

public class LogoActivity extends AbstractActivity {

    private final LogoView view;

    @Inject
    public LogoActivity(LogoView view) {
        this.view = view;
    }

    public LogoActivity withPlace(GoodbyePlace place) {
        return this;
    }

    @Override
    public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
        containerWidget.setWidget(view);
    }

}