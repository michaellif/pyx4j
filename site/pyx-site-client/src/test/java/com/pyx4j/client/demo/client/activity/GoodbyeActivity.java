package com.pyx4j.client.demo.client.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.client.demo.client.ClientFactory;
import com.pyx4j.client.demo.client.place.GoodbyePlace;
import com.pyx4j.client.demo.client.ui.GoodbyeView;

public class GoodbyeActivity extends AbstractActivity {
    private final ClientFactory clientFactory;

    // Name that will be appended to "Good-bye, "
    private final String name;

    public GoodbyeActivity(GoodbyePlace place, ClientFactory clientFactory) {
        this.name = place.getGoodbyeName();
        this.clientFactory = clientFactory;
    }

    @Override
    public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
        GoodbyeView goodbyeView = clientFactory.getGoodbyeView();
        goodbyeView.setName(name);
        containerWidget.setWidget(goodbyeView.asWidget());
    }
}