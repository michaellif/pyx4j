package com.pyx4j.client.demo.client.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

import com.pyx4j.client.demo.client.place.GoodbyePlace;
import com.pyx4j.client.demo.client.ui.GoodbyeView;

public class GoodbyeActivity extends AbstractActivity {
    private String name;

    private final GoodbyeView view;

    @Inject
    public GoodbyeActivity(GoodbyeView view) {
        this.view = view;
    }

    public GoodbyeActivity withPlace(GoodbyePlace place) {
        this.name = place.getGoodbyeName();
        return this;
    }

    @Override
    public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
        view.setName(name);
        containerWidget.setWidget(view.asWidget());
    }

}